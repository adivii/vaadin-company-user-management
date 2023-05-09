package com.adivii.companymanagement.data.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.entity.Invitation;
import com.adivii.companymanagement.data.repository.InvitationRepository;
import com.adivii.companymanagement.data.service.generator.IdGeneratorService;

@Service
public class InvitationService {

    InvitationRepository invitationRepository;

    public static String TYPE_NEW = "new";
    public static String TYPE_EXISTING = "existing";

    public InvitationService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    public List<Invitation> getByEmailAndCompanyAndDepartment(String email, Company company, Department department) {
        return this.invitationRepository.findByEmailAndCompanyAndDepartment(email, company, department);
    }

    public List<Invitation> getByInviteId(String inviteId) {
        return this.invitationRepository.findByInviteId(inviteId);
    }

    public ErrorService saveInvitation(Invitation invitation) {
        while (invitation.getInviteId().isBlank()
                || this.invitationRepository.findByInviteId(invitation.getInviteId()).size() > 0) {
            invitation.setInviteId(IdGeneratorService.generateID());
        }

        // Check for incomplete request
        if (invitation.getEmail().isBlank() || invitation.getType().isBlank() ||
                invitation.getCompany() == null || invitation.getRole() == null) {
            return new ErrorService(true, "Field can't be empty");
        }

        // Check if current request already exist
        if (this.invitationRepository.findByEmailAndCompanyAndDepartmentAndRole(invitation.getEmail(),
                invitation.getCompany(), invitation.getDepartment(), invitation.getRole())
                .size() > 0) {
            invitation = this.invitationRepository.findByEmailAndCompanyAndDepartmentAndRole(invitation.getEmail(),
                    invitation.getCompany(), invitation.getDepartment(), invitation.getRole()).get(0);
        }

        this.invitationRepository.save(invitation);
        return new ErrorService(false, null);
    }

    public void deleteInvitation(Invitation invitation) {
        this.invitationRepository.delete(invitation);
    }
}
