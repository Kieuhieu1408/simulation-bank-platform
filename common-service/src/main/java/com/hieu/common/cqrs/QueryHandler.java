package com.hieu.common.cqrs;

public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}
