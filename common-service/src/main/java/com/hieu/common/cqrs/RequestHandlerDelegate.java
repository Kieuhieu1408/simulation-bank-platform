package com.hieu.common.cqrs;

public interface RequestHandlerDelegate<R> {
    R handle();
}
