package com.hieu.corebank.customer.domain;

/**
 * CifGenerator — Port sinh mã Định danh Khách hàng (CIF).
 */
public interface CifGenerator {

    /**
     * Sinh mã CIF kế tiếp (chuẩn 8 chữ số, ví dụ 00000001 - 99999999).
     */
    String nextCif();
}
