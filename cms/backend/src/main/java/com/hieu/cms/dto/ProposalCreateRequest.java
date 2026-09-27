package com.hieu.cms.dto;

import lombok.Data;

@Data
public class ProposalCreateRequest {
    private String type;
    private String customerCif;
    private String newData;
    private String documentUrls;
}
