package com.whaleex.api.client.util;

import org.junit.Assert;
import org.junit.Test;

public class PasswordUtilsTest {

    HashUtils passwordUtils = new HashUtils();

    @Test
    public void testHash(){
        final String qweqwe123 = HashUtils.getHash("qweqwe123");
        Assert.assertEquals("c9fe854ea69fc0a252340e152864b539b116c36cf1ac419652e1826c3071d5ed",qweqwe123);
    }
}
