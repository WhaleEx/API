package com.whaleex.api.client.util;

import org.junit.Assert;
import org.junit.Test;

public class StoreUtilsTest {

    @Test
    public void testProp(){
        System.out.println(StoreUtils.getStorePublicKey());
        StoreUtils.storePublicKye("aaa");
        Assert.assertEquals("aaa",StoreUtils.getStorePublicKey());
    }
}
