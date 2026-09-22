package com.hieu.common.cqrs;

public interface PipelineBehavior<C, R> {
    R handle(C request, RequestHandlerDelegate<R> next);
}
