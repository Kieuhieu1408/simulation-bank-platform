package com.hieu.corebank.shared.infrastructure.web;

import com.hieu.common.cqrs.Command;
import com.hieu.common.cqrs.Dispatcher;
import com.hieu.common.cqrs.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public abstract class BaseController {

    @Autowired
    protected Dispatcher dispatcher;

    @Autowired(required = false)
    protected Environment environment;

    protected <R, C extends Command<R>> ResponseEntity<R> execute(C command, Class<R> responseType) {
        if (isQueryOnlyProfile()) {
            throw new ResponseStatusException(
                    HttpStatus.METHOD_NOT_ALLOWED,
                    "Write commands are not accepted on corebank-query instances (CQRS read replica)"
            );
        }
        R result = dispatcher.dispatch(command);
        return ResponseEntity.ok(result);
    }

    protected <R, Q extends Query<R>> ResponseEntity<R> executeQuery(Q query, Class<R> responseType) {
        return executeQueryInternal(query);
    }

    protected <R, Q extends Query<R>> ResponseEntity<R> executeQueryInternal(Q query) {
        R result = dispatcher.dispatch(query);
        return ResponseEntity.ok(result);
    }

    private boolean isQueryOnlyProfile() {
        if (environment == null) return false;
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length == 0) return false;
        boolean hasQuery = false;
        boolean hasCommandOrAllOrTest = false;
        for (String p : profiles) {
            if ("query".equalsIgnoreCase(p)) hasQuery = true;
            if ("command".equalsIgnoreCase(p) || "all".equalsIgnoreCase(p) || "test".equalsIgnoreCase(p)) {
                hasCommandOrAllOrTest = true;
            }
        }
        return hasQuery && !hasCommandOrAllOrTest;
    }
}
