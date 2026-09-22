package com.hieu.common.cqrs;

public interface Dispatcher {
    <R, C extends Command<R>> R dispatch(C command);
    <R, Q extends Query<R>> R dispatch(Q query);
}
