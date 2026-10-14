package com.hieu.corebank.account.domain;

/**
 * Domain Port sinh số tài khoản ngân hàng duy nhất.
 */
public interface AccountNumberGenerator {

    String nextAccountNumber();
}
