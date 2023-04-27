package com.adivii.companymanagement.data.service;

public class MailTemplateGenerator {
    
    public static String getMailTemplate(String title, String content, String link) {
        String newLine = System.getProperty("line.separator");
        
        return "<h1>".concat(title).concat("</h1>")
                .concat(newLine).concat("<p>").concat(content).concat("</p>")
                .concat(newLine).concat("<a href=\"").concat(link).concat("\">Click Here to Activate Your Account</a>");
    }

    public static String getLinkTemplate(String email) {
        return "http://localhost:8080/register?email=".concat(email);
    }
}
