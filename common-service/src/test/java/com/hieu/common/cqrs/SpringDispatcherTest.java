package com.hieu.common.cqrs;

import com.hieu.common.cqrs.behavior.LoggingAndMetricsBehavior;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("SpringDispatcher CQRS Pipeline Unit Tests")
class SpringDispatcherTest {

    private ApplicationContext context;

    // Test Command & Query
    static class TestCommand implements Command<String> {
        final String message;
        TestCommand(String message) { this.message = message; }
    }

    static class SubTestCommand extends TestCommand {
        SubTestCommand(String message) { super(message); }
    }

    static class TestQuery implements Query<Integer> {
        final int value;
        TestQuery(int value) { this.value = value; }
    }

    static class UnhandledCommand implements Command<Void> {}

    // Test Handlers
    static class TestCommandHandler implements CommandHandler<TestCommand, String> {
        @Override
        public String handle(TestCommand command) {
            return "Handled: " + command.message;
        }
    }

    static class TestQueryHandler implements QueryHandler<TestQuery, Integer> {
        @Override
        public Integer handle(TestQuery query) {
            return query.value * 2;
        }
    }

    // Test Pipeline Behaviors
    static class OrderTrackingBehavior implements PipelineBehavior<Object, Object> {
        private final String name;
        private final List<String> trace;

        OrderTrackingBehavior(String name, List<String> trace) {
            this.name = name;
            this.trace = trace;
        }

        @Override
        public Object handle(Object request, RequestHandlerDelegate<Object> next) {
            trace.add(name + "-before");
            Object result = next.handle();
            trace.add(name + "-after");
            return result;
        }
    }

    @BeforeEach
    void setUp() {
        context = mock(ApplicationContext.class);
    }

    @Test
    @DisplayName("Should successfully dispatch command to registered handler")
    void testDispatchCommandSuccessfully() {
        TestCommandHandler handler = new TestCommandHandler();
        when(context.getBeansOfType(CommandHandler.class)).thenReturn(Map.of("testHandler", handler));
        when(context.getBeansOfType(QueryHandler.class)).thenReturn(Map.of());

        SpringDispatcher dispatcher = new SpringDispatcher(context);

        String result = dispatcher.dispatch(new TestCommand("hello"));
        assertThat(result).isEqualTo("Handled: hello");
    }

    @Test
    @DisplayName("Should successfully dispatch query to registered handler")
    void testDispatchQuerySuccessfully() {
        TestQueryHandler handler = new TestQueryHandler();
        when(context.getBeansOfType(CommandHandler.class)).thenReturn(Map.of());
        when(context.getBeansOfType(QueryHandler.class)).thenReturn(Map.of("testQueryHandler", handler));

        SpringDispatcher dispatcher = new SpringDispatcher(context);

        Integer result = dispatcher.dispatch(new TestQuery(21));
        assertThat(result).isEqualTo(42);
    }

    @Test
    @DisplayName("Should execute pipeline behaviors in correct chain-of-responsibility order")
    void testPipelineBehaviorsExecutionOrder() {
        TestCommandHandler handler = new TestCommandHandler();
        when(context.getBeansOfType(CommandHandler.class)).thenReturn(Map.of("testHandler", handler));
        when(context.getBeansOfType(QueryHandler.class)).thenReturn(Map.of());

        List<String> trace = new ArrayList<>();
        OrderTrackingBehavior b1 = new OrderTrackingBehavior("B1", trace);
        OrderTrackingBehavior b2 = new OrderTrackingBehavior("B2", trace);

        SpringDispatcher dispatcher = new SpringDispatcher(context, List.of(b1, b2));

        String result = dispatcher.dispatch(new TestCommand("chain"));
        assertThat(result).isEqualTo("Handled: chain");

        // B1 starts -> B2 starts -> Handler executes -> B2 ends -> B1 ends
        assertThat(trace).containsExactly("B1-before", "B2-before", "B2-after", "B1-after");
    }

    @Test
    @DisplayName("Should dispatch polymorphic command via isAssignableFrom and cache it")
    void testPolymorphicDispatch() {
        TestCommandHandler handler = new TestCommandHandler();
        when(context.getBeansOfType(CommandHandler.class)).thenReturn(Map.of("testHandler", handler));
        when(context.getBeansOfType(QueryHandler.class)).thenReturn(Map.of());

        SpringDispatcher dispatcher = new SpringDispatcher(context);

        String result = dispatcher.dispatch(new SubTestCommand("subclass"));
        assertThat(result).isEqualTo("Handled: subclass");

        // Second call hits cache
        String result2 = dispatcher.dispatch(new SubTestCommand("subclass-again"));
        assertThat(result2).isEqualTo("Handled: subclass-again");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when no handler found")
    void testNoHandlerFound() {
        when(context.getBeansOfType(CommandHandler.class)).thenReturn(Map.of());
        when(context.getBeansOfType(QueryHandler.class)).thenReturn(Map.of());

        SpringDispatcher dispatcher = new SpringDispatcher(context);

        assertThatThrownBy(() -> dispatcher.dispatch(new UnhandledCommand()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No CommandHandler found for");
    }
}
