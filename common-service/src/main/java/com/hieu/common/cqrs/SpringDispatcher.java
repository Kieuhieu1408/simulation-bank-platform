package com.hieu.common.cqrs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Điều phối viên trung tâm (Mediator) cho kiến trúc CQRS.
 *
 * <p>Nhiệm vụ:
 * <ul>
 *   <li>Tự động phát hiện các {@link CommandHandler} và {@link QueryHandler} trong Spring ApplicationContext.</li>
 *   <li>Hỗ trợ xử lý an toàn Spring Proxy (AOP, @Transactional) qua {@link AopUtils#getTargetClass(Object)}.</li>
 *   <li>Thực thi chuỗi {@link PipelineBehavior} (Chain of Responsibility) xung quanh Handler.</li>
 *   <li>Đảm bảo an toàn đa luồng (thread-safety) với {@link ConcurrentHashMap}.</li>
 * </ul>
 */
@Component
@SuppressWarnings({"unchecked", "rawtypes"})
public class SpringDispatcher implements Dispatcher, ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(SpringDispatcher.class);

    private final ApplicationContext context;
    private final List<PipelineBehavior<?, ?>> behaviors;
    private final Map<Class<?>, CommandHandler> commandHandlers = new ConcurrentHashMap<>();
    private final Map<Class<?>, QueryHandler> queryHandlers = new ConcurrentHashMap<>();
    private volatile boolean initialized = false;

    @Autowired
    public SpringDispatcher(ApplicationContext context,
                            @Autowired(required = false) List<PipelineBehavior<?, ?>> behaviors) {
        this.context = context;
        if (behaviors != null) {
            this.behaviors = new ArrayList<>(behaviors);
            AnnotationAwareOrderComparator.sort(this.behaviors);
        } else {
            this.behaviors = Collections.emptyList();
        }
        initHandlers();
    }

    public SpringDispatcher(ApplicationContext context) {
        this(context, Collections.emptyList());
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.context) {
            initHandlers();
        }
    }

    private synchronized void initHandlers() {
        Map<String, CommandHandler> commandBeans = context.getBeansOfType(CommandHandler.class);
        for (CommandHandler handler : commandBeans.values()) {
            Class<?> targetClass = AopUtils.getTargetClass(handler);
            Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(targetClass, CommandHandler.class);
            if (typeArguments != null && typeArguments.length > 0) {
                commandHandlers.put(typeArguments[0], handler);
            }
        }

        Map<String, QueryHandler> queryBeans = context.getBeansOfType(QueryHandler.class);
        for (QueryHandler handler : queryBeans.values()) {
            Class<?> targetClass = AopUtils.getTargetClass(handler);
            Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(targetClass, QueryHandler.class);
            if (typeArguments != null && typeArguments.length > 0) {
                queryHandlers.put(typeArguments[0], handler);
            }
        }
        initialized = true;
        log.debug("Initialized SpringDispatcher with {} CommandHandlers and {} QueryHandlers",
                commandHandlers.size(), queryHandlers.size());
    }

    @Override
    public <R, C extends Command<R>> R dispatch(C command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        CommandHandler<C, R> handler = getCommandHandler(command.getClass());
        if (handler == null) {
            throw new IllegalArgumentException("No CommandHandler found for: " + command.getClass()
                    + ", available handlers: " + commandHandlers.keySet());
        }

        RequestHandlerDelegate<R> invocation = () -> handler.handle(command);

        for (int i = behaviors.size() - 1; i >= 0; i--) {
            PipelineBehavior behavior = behaviors.get(i);
            RequestHandlerDelegate<R> next = invocation;
            invocation = () -> (R) behavior.handle(command, next);
        }

        return invocation.handle();
    }

    @Override
    public <R, Q extends Query<R>> R dispatch(Q query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }

        QueryHandler<Q, R> handler = getQueryHandler(query.getClass());
        if (handler == null) {
            throw new IllegalArgumentException("No QueryHandler found for: " + query.getClass()
                    + ", available handlers: " + queryHandlers.keySet());
        }

        RequestHandlerDelegate<R> invocation = () -> handler.handle(query);

        for (int i = behaviors.size() - 1; i >= 0; i--) {
            PipelineBehavior behavior = behaviors.get(i);
            RequestHandlerDelegate<R> next = invocation;
            invocation = () -> (R) behavior.handle(query, next);
        }

        return invocation.handle();
    }

    private <R, C extends Command<R>> CommandHandler<C, R> getCommandHandler(Class<?> commandClass) {
        CommandHandler handler = commandHandlers.get(commandClass);
        if (handler != null) {
            return handler;
        }

        for (Map.Entry<Class<?>, CommandHandler> entry : commandHandlers.entrySet()) {
            if (entry.getKey().isAssignableFrom(commandClass)) {
                handler = entry.getValue();
                commandHandlers.put(commandClass, handler);
                return handler;
            }
        }
        return null;
    }

    private <R, Q extends Query<R>> QueryHandler<Q, R> getQueryHandler(Class<?> queryClass) {
        QueryHandler handler = queryHandlers.get(queryClass);
        if (handler != null) {
            return handler;
        }

        for (Map.Entry<Class<?>, QueryHandler> entry : queryHandlers.entrySet()) {
            if (entry.getKey().isAssignableFrom(queryClass)) {
                handler = entry.getValue();
                queryHandlers.put(queryClass, handler);
                return handler;
            }
        }
        return null;
    }
}
