package com.whaleex.api.client.util;

import com.whaleex.api.client.pojo.Symbol;
import com.whaleex.api.client.pojo.request.AutoOrderRequest;
import com.whaleex.api.client.util.crypto.digest.Sha256;
import com.whaleex.api.client.util.crypto.ec.EcDsa;
import com.whaleex.api.client.util.crypto.ec.EcSignature;
import com.whaleex.api.client.util.crypto.ec.EosPrivateKey;
import com.whaleex.api.client.util.crypto.ec.EosPublicKey;
import org.junit.Test;

public class SignUtilsTest {

    @Test
    public void testSignOrder(){

        AutoOrderRequest orderRequest = new AutoOrderRequest();
        orderRequest.setSymbol("IQEOS");
        orderRequest.setAmount("1");
        orderRequest.setType(AutoOrderRequest.OrderType.sell_limit);
        orderRequest.setPrice("9999");
        Symbol symbol = new Symbol();
        symbol.setBaseContract("everipediaiq");
        symbol.setBaseCurrency("IQ");
        symbol.setQuoteContract("eosio.token");
        symbol.setQuoteCurrency("EOS");
        symbol.setQuotePrecision(4);
        symbol.setBasePrecision(3);

        final String sign = SignUtils.signDateOrder(orderRequest, "yyyyyyyyyycc", 89385080403558920l, symbol);
        System.out.println(sign);

    }
    @Test
    public void testPrivateKey(){
        EosPrivateKey eosPrivateKey = new EosPrivateKey();
        System.out.println(eosPrivateKey.toString());
        System.out.println(eosPrivateKey.getPublicKey().toString());
        EcSignature signature = EcDsa.sign(Sha256.from("".getBytes()), eosPrivateKey);
        System.out.println(signature);
    }
}
