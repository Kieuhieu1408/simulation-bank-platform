package com.hieu.cms.controller;

import com.hieu.cms.dto.ProposalCreateRequest;
import com.hieu.cms.dto.ProposalRejectRequest;
import com.hieu.cms.security.CmsAuthorization;
import com.hieu.cms.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    @PostMapping
    @CmsAuthorization(menuCode = "proposal_management", action = "CREATE")
    public void createProposal(@RequestBody ProposalCreateRequest request) {
        proposalService.createProposal(request);
    }

    @PostMapping("/{id}/cancel")
    @CmsAuthorization(menuCode = "proposal_management", action = "CANCEL")
    public void cancelProposal(@PathVariable("id") UUID id) {
        proposalService.cancelProposal(id);
    }

    @PostMapping("/{id}/approve")
    @CmsAuthorization(menuCode = "proposal_management", action = "APPROVE")
    public void approveProposal(@PathVariable("id") UUID id) {
        proposalService.approveProposal(id);
    }

    @PostMapping("/{id}/reject")
    @CmsAuthorization(menuCode = "proposal_management", action = "REJECT")
    public void rejectProposal(@PathVariable("id") UUID id, @RequestBody ProposalRejectRequest request) {
        proposalService.rejectProposal(id, request.getReason());
    }
}
