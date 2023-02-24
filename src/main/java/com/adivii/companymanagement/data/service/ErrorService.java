package com.adivii.companymanagement.data.service;

// TODO: Convert to static method
public class ErrorService {
    private boolean errorStatus;
    private String errorMessage;

    public ErrorService(boolean errorStatus, String errorMessage) {
        this.errorStatus = errorStatus;
        this.errorMessage = errorMessage;
    }
    
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
