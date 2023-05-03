package org.vaadin;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.adivii.companymanagement.data.service.security.CustomBase64Encoder;

public class Base64EncodeTest {
    @Test
    public void testDecode() {
        System.out.println(CustomBase64Encoder.encode("Taja"));
        System.out.println(CustomBase64Encoder.decode("zvyJRlwanbSI"));
        Assertions.assertEquals("Maju Jaya", CustomBase64Encoder.decode(CustomBase64Encoder.encode("Maju Jaya")));
    }
}
