package com.hieu.cms.controller;

import com.hieu.cms.dto.CustomerResponse;
import com.hieu.cms.security.CmsAuthorization;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerLookupController {

    @GetMapping("/search")
    @CmsAuthorization(menuCode = "user_info", action = "VIEW")
    public CustomerResponse searchCustomer(@RequestParam("cccd") String cccd) {
        // Mock implementation for simulation
        CustomerResponse response = new CustomerResponse();
        response.setCif("CIF" + cccd.substring(Math.max(0, cccd.length() - 4)));
        response.setFullName("Nguyen Van Mock");
        response.setPhone("0987654321");
        response.setCccd(cccd);
        return response;
    }
}
