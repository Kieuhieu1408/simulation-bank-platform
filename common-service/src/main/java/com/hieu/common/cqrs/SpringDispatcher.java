package com.hieu.common.cqrs;

import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@SuppressWarnings({"unchecked", "rawtypes"})
public class SpringDispatcher implements Dispatcher {

    private final ApplicationContext context;
    private final Map<Class<?>, CommandHandler> commandHandlers = new HashMap<>();
    private final Map<Class<?>, QueryHandler> queryHandlers = new HashMap<>();

    public SpringDispatcher(ApplicationContext context) {
        this.context = context;
        initHandlers();
    }

    private void initHandlers() {
        Map<String, CommandHandler> commandBeans = context.getBeansOfType(CommandHandler.class);
        for (CommandHandler handler : commandBeans.values()) {
            Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(handler.getClass(), CommandHandler.class);
            if (typeArguments != null && typeArguments.length > 0) {
                commandHandlers.put(typeArguments[0], handler);
            }
        }

        Map<String, QueryHandler> queryBeans = context.getBeansOfType(QueryHandler.class);
        for (QueryHandler handler : queryBeans.values()) {
            Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(handler.getClass(), QueryHandler.class);
            if (typeArguments != null && typeArguments.length > 0) {
                queryHandlers.put(typeArguments[0], handler);
            }
        }
    }

    @Override
    public <R, C extends Command<R>> R dispatch(C command) {
        CommandHandler<C, R> handler = commandHandlers.get(command.getClass());
        if (handler == null) {
            throw new IllegalArgumentException("No CommandHandler found for: " + command.getClass());
        }
        return handler.handle(command);
    }

    @Override
    public <R, Q extends Query<R>> R dispatch(Q query) {
        QueryHandler<Q, R> handler = queryHandlers.get(query.getClass());
        if (handler == null) {
            throw new IllegalArgumentException("No QueryHandler found for: " + query.getClass());
        }
        return handler.handle(query);
    }
}
