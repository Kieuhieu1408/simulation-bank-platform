package com.hieu.corebank.account.infrastructure.customer;

import com.hieu.common.cqrs.Dispatcher;
import com.hieu.corebank.account.application.port.CustomerDirectory;
import com.hieu.corebank.customer.api.dto.request.CustomerGetRequestDTO;
import com.hieu.corebank.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adapter hiện thực CustomerDirectory port bằng cách gọi Application Query của Customer Context.
 * Tuyệt đối không import Customer Repository/Infrastructure hay Customer Entity trực tiếp.
 */
@Component
@RequiredArgsConstructor
public class LocalCustomerDirectory implements CustomerDirectory {

    private final Dispatcher dispatcher;

    @Override
    public boolean existsByCif(String cif) {
        if (cif == null || cif.isBlank()) {
            return false;
        }
        try {
            return dispatcher.dispatch(new CustomerGetRequestDTO(cif)) != null;
        } catch (NotFoundException ex) {
            return false;
        }
    }
}
