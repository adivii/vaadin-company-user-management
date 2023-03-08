package com.adivii.companymanagement.views.component;

import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;

public class CustomUploadButton extends Upload {

    public CustomUploadButton(Receiver receiver) {
        super(receiver);
        getElement().addEventListener("file-reject", event -> onFileRejected());
    }

    public void onFileRejected() {
        
    }
}
