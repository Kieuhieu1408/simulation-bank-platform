package com.hieu.corebank.customer.application.command;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.customer.api.dto.request.CustomerCreateRequestDTO;
import com.hieu.corebank.customer.api.dto.response.CustomerResponseDTO;
import com.hieu.corebank.customer.domain.CifGenerator;
import com.hieu.corebank.customer.domain.Customer;
import com.hieu.corebank.customer.domain.CustomerRepository;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.ConflictException;
import com.hieu.corebank.shared.domain.NationalId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * CustomerHandler — Use Case xử lý tạo khách hàng mới (Thẩm định 4 Cấp độ).
 *
 * <p>Level 1 (Value Object): Thẩm định định dạng CCCD/Hộ chiếu (NationalId).
 * <p>Level 2 (Use Case): Kiểm tra tính duy nhất qua CustomerRepository.
 * <p>Level 3 (Domain): Khởi tạo Customer Entity và sinh CIF tự động nếu không truyền.
 * <p>Level 4 (Database): Bắt DataIntegrityViolationException từ unique constraint uk_customer_national_id.
 */
@Slf4j
@Profile({ProfileConstants.COMMAND, ProfileConstants.ALL, ProfileConstants.TEST})
@Component
@RequiredArgsConstructor
public class CustomerHandler implements CommandHandler<CustomerCreateRequestDTO, CustomerResponseDTO> {

    private final CustomerRepository customers;
    private final CifGenerator cifGenerator;

    @Override
    @Transactional
    public CustomerResponseDTO handle(CustomerCreateRequestDTO command) {
        // Cấp độ 1: Thẩm định Value Object (In-Memory Validation)
        if (command.getNationalId() != null && !command.getNationalId().isBlank()) {
            NationalId.of(command.getNationalId());
        }

        // Tự động sinh mã CIF chuẩn 8 số nếu client không truyền (D14, AC-CB-01)
        String cifNumber = (command.getCifNumber() != null && !command.getCifNumber().isBlank())
                ? command.getCifNumber().trim().toUpperCase()
                : cifGenerator.nextCif();

        // Cấp độ 2: Thẩm định Use Case (Stateful Check qua Repository)
        if (customers.existsByCifNumber(cifNumber)) {
            throw new ConflictException("CIF already exists: " + cifNumber);
        }
        if (command.getNationalId() != null && !command.getNationalId().isBlank()
                && customers.existsByNationalId(command.getNationalId().trim())) {
            throw new ConflictException("National ID already exists: " + command.getNationalId());
        }

        // Cấp độ 3: Khởi tạo Domain Entity
        Customer customer = Customer.create(
                cifNumber,
                command.getNationalId(),
                command.getFullName(),
                command.getPhoneNumber(),
                command.getEmail()
        );

        // Cấp độ 4: Lưu trữ và chốt chặn Unique Constraint tại DB
        try {
            Customer saved = customers.save(customer);
            log.info("Customer created: cif={} nationalId={}", saved.cifNumber(), saved.maskedNationalId());
            return CustomerResponseDTO.from(saved);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Database constraint violation creating customer cif={}: {}", cifNumber, ex.getMessage());
            throw new ConflictException("Customer with given CIF or National ID already exists");
        }
    }
}
