package com.hieu.corebank.customer.application.command;

import com.hieu.common.cqrs.CommandHandler;
import com.hieu.corebank.customer.api.dto.response.CustomerResponseDTO;
import com.hieu.corebank.customer.domain.CifGenerator;
import com.hieu.corebank.customer.domain.Customer;
import com.hieu.corebank.customer.domain.CustomerRepository;
import com.hieu.corebank.shared.constant.ProfileConstants;
import com.hieu.corebank.shared.domain.BusinessException;
import com.hieu.corebank.shared.domain.ConflictException;
import com.hieu.corebank.shared.domain.NationalId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * CreateCustomerCommandHandler — Use Case tạo khách hàng mới với 4-Level Validation Strategy (D14, AC-CB-01).
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
public class CreateCustomerCommandHandler implements CommandHandler<CreateCustomerCommand, CustomerResponseDTO> {

    private final CustomerRepository customers;
    private final CifGenerator cifGenerator;

    @Override
    @Transactional
    public CustomerResponseDTO handle(CreateCustomerCommand command) {
        // Cấp độ 1: Thẩm định Value Object (In-Memory Validation)
        if (command.nationalId() != null && !command.nationalId().isBlank()) {
            NationalId.of(command.nationalId()); // Thẩm định 12 số CCCD hoặc hộ chiếu
        }

        // Tự động sinh mã CIF chuẩn 8 số nếu client không truyền (D14, AC-CB-01)
        String cifNumber = (command.cifNumber() != null && !command.cifNumber().isBlank())
                ? command.cifNumber().trim().toUpperCase()
                : cifGenerator.nextCif();

        // Cấp độ 2: Thẩm định Use Case (Stateful Check qua Repository)
        if (customers.existsByCifNumber(cifNumber)) {
            throw new ConflictException("CIF already exists: " + cifNumber);
        }
        if (command.nationalId() != null && !command.nationalId().isBlank()
                && customers.existsByNationalId(command.nationalId().trim())) {
            throw new ConflictException("National ID already exists: " + command.nationalId());
        }

        // Cấp độ 3: Khởi tạo Domain Entity
        Customer customer = Customer.create(
                cifNumber,
                command.nationalId(),
                command.fullName(),
                command.phoneNumber(),
                command.email()
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
