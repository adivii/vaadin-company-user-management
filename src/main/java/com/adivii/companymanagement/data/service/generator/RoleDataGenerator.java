package com.adivii.companymanagement.data.service.generator;

import com.adivii.companymanagement.data.entity.Role;
import com.adivii.companymanagement.data.service.RoleService;

public class RoleDataGenerator {
    
    private RoleService roleService;

    public RoleDataGenerator(RoleService roleService) {
        this.roleService = roleService;
    }

    public void generate() {
        String[][] roles = {
            {"superadmin", "Super Admin"},
            {"useradmin", "User Admin"},
            {"departmentadmin", "Department Admin"},
            {"companyadmin", "Company Admin"},
            {"user", "User"}
        };

        for (String[] role : roles) {
            Role newRole = new Role();
            newRole.setName(role[1]);
            newRole.setValue(role[0]);
            
            this.roleService.addRole(newRole);
        }
    }
}
