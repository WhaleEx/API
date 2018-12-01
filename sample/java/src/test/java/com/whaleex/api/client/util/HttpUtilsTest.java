package com.whaleex.api.client.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.junit.Test;

import java.io.IOException;

public class HttpUtilsTest {

    HttpUtils httpUtils = new HttpUtils();

    @Test
    public void testLogin() throws IOException {
        String cn = httpUtils.doLoginRequest("15838261714", "qweqwe123", "CN");
        System.out.println(cn);

    }
}
