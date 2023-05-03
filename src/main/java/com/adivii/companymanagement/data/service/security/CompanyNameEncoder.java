package com.adivii.companymanagement.data.service.security;

import java.util.regex.Pattern;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.service.CompanyService;

public class CompanyNameEncoder {
    
    public static String encode(Company company) {
        
        String rawString = company.getCompanyName();

        return CustomBase64Encoder.encode(rawString);
    }

    public static Company decode(CompanyService companyService, String encoded) {

        String compName = CustomBase64Encoder.decode(encoded).strip();

        if(companyService.getByName(compName).size() == 0) {
            return null;
        }

        return companyService.getByName(compName).get(0);
    }
}
