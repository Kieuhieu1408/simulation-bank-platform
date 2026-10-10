package com.hieu.corebank.security;

import com.hieu.common.constant.ActionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CoreBankAuthorization {
    String menuCode();
    ActionType action();
}
