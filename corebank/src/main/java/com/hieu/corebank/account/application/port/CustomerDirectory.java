package com.hieu.corebank.account.application.port;

/**
 * Port giao tiếp để Account Bounded Context tra cứu thông tin Khách hàng (Customer).
 * Đảm bảo Account Context không phụ thuộc trực tiếp vào Customer Infrastructure/Persistence.
 */
public interface CustomerDirectory {

    /**
     * Kiểm tra CIF khách hàng có tồn tại và hợp lệ trong hệ thống hay không.
     *
     * @param cif Mã định danh khách hàng (CIF)
     * @return true nếu tồn tại, ngược lại false
     */
    boolean existsByCif(String cif);
}
