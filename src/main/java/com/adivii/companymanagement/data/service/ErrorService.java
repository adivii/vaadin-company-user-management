package com.adivii.companymanagement.data.service;

public class ErrorService {
    private boolean errorStatus;
    private String errorMessage;
    
    public boolean isErrorStatus() {
        return errorStatus;
    }
    public void setErrorStatus(boolean errorStatus) {
        this.errorStatus = errorStatus;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
