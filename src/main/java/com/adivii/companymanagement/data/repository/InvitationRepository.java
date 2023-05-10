package com.adivii.companymanagement.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.entity.Invitation;
import com.adivii.companymanagement.data.entity.Role;

public interface InvitationRepository extends JpaRepository<Invitation, Integer> {
    public List<Invitation> findByInviteId(String inviteId);
    public List<Invitation> findByEmailAndCompanyAndDepartmentAndRole(String email, Company company, Department department, Role role);
    public List<Invitation> findByEmailAndCompanyAndDepartment(String email, Company company, Department department);
}
