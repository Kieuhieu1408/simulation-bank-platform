package com.hieu.cms.dto;

import com.hieu.cms.configuration.masking.MaskString;
import lombok.Data;

@Data
public class CustomerResponse {
    private String cif;

    @MaskString(visibleTail = 3)
    private String fullName;

    @MaskString(visibleTail = 3)
    private String phone;

    @MaskString(visibleTail = 3)
    private String cccd;
}
