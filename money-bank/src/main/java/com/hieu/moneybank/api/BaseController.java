package com.hieu.moneybank.api;

import com.hieu.common.cqrs.Command;
import com.hieu.common.cqrs.Dispatcher;
import com.hieu.common.cqrs.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    @Autowired
    protected Dispatcher dispatcher;

    protected <R, C extends Command<R>> ResponseEntity<R> execute(C command, Class<R> responseType) {
        R result = dispatcher.dispatch(command);
        return ResponseEntity.ok(result);
    }

    protected <R, Q extends Query<R>> ResponseEntity<R> executeQuery(Q query, Class<R> responseType) {
        R result = dispatcher.dispatch(query);
        return ResponseEntity.ok(result);
    }
}