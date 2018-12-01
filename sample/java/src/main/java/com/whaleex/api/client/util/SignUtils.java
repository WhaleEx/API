package com.whaleex.api.client.util;

import com.whaleex.api.client.constant.WhaleexConstant;
import com.whaleex.api.client.pojo.StringPair;
import com.whaleex.api.client.pojo.Symbol;
import com.whaleex.api.client.pojo.request.AutoOrderRequest;
import com.whaleex.api.client.util.crypto.util.HexUtils;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class SignUtils {

    public static String signDateOrder(AutoOrderRequest order,String account,long orderId,Symbol symbol){
        List<StringPair> list = new ArrayList<>();
        String timestamp = System.currentTimeMillis()+"";
//        String timestamp = "0";
        list.add(new StringPair("APIKey",StoreUtils.getStoreAPIkey()));
        list.add(new StringPair("pk",StoreUtils.getStorePublicKey()));
        list.add(new StringPair("timestamp",timestamp ));
        list.add(new StringPair("orderId",orderId+""));

        String sigParams = getSigParam(list);
        String sig = signOrder(orderId, order, timestamp, account, symbol);


        return sigParams+"&Signature="+sig;
    }

    private static  String signOrder(Long orderId, AutoOrderRequest order, String timestamp, String account, Symbol symbol){
        int length = account.length() + WhaleexConstant.EX_EOSACCOUNT.length() + 3*8 +4+4+
                symbol.getBaseContract().length() + symbol.getBaseCurrency().length() + symbol.getQuoteContract().length() +
                symbol.getQuoteCurrency().length();
        ByteBuffer buffer = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(account.getBytes());
        buffer.put(WhaleexConstant.EX_EOSACCOUNT.getBytes());
        buffer.putLong(orderId);
        buffer.putInt(Integer.valueOf((Long.valueOf(timestamp)/1000)+""));
        if(AutoOrderRequest.OrderType.buy_limit.equals(order.getType())){
            buffer.put(symbol.getQuoteContract().getBytes());
            buffer.put(symbol.getQuoteCurrency().getBytes());
            buffer.putLong(multiply(Double.valueOf(order.getPrice()),Double.valueOf(order.getAmount()),symbol.getQuotePrecision(),true));
            buffer.put(symbol.getBaseContract().getBytes());
            buffer.put(symbol.getBaseCurrency().getBytes());
            buffer.putLong(multiply(1d,Double.valueOf(order.getAmount()),symbol.getBasePrecision(),false));
        }
        if(AutoOrderRequest.OrderType.sell_limit.equals(order.getType())){
            buffer.put(symbol.getBaseContract().getBytes());
            buffer.put(symbol.getBaseCurrency().getBytes());
            buffer.putLong(multiply(1d,Double.valueOf(order.getAmount()),symbol.getBasePrecision(),false));
            buffer.put(symbol.getQuoteContract().getBytes());
            buffer.put(symbol.getQuoteCurrency().getBytes());
            buffer.putLong(multiply(Double.valueOf(order.getPrice()),Double.valueOf(order.getAmount()),symbol.getQuotePrecision(),false));
        }
        if(AutoOrderRequest.OrderType.buy_market.equals(order.getType())){
            buffer.put(symbol.getQuoteContract().getBytes());
            buffer.put(symbol.getQuoteCurrency().getBytes());
            buffer.putLong(multiply(1d,Double.valueOf(order.getAmount()),symbol.getQuotePrecision(),false));
            buffer.put(symbol.getBaseContract().getBytes());
            buffer.put(symbol.getBaseCurrency().getBytes());
            buffer.putLong(0l);
        }
        if(AutoOrderRequest.OrderType.sell_market.equals(order.getType())){
            buffer.put(symbol.getBaseContract().getBytes());
            buffer.put(symbol.getBaseCurrency().getBytes());
            buffer.putLong(multiply(1d,Double.valueOf(order.getAmount()),symbol.getBasePrecision(),false));
            buffer.put(symbol.getQuoteContract().getBytes());
            buffer.put(symbol.getQuoteCurrency().getBytes());
            buffer.putLong(0l);
        }
        buffer.putShort(new Short("10").shortValue());
        buffer.putShort(new Short("10").shortValue());
        byte[] array = buffer.array();
        System.out.println(array);
        buffer.clear();

//        String hashData = HashUtils.sha256String(array);
        String sign = Ecc.Sign(array,StoreUtils.getStorePrivateKey());

        return sign;

    }

    private static long multiply(double m,double n,int precision,boolean ceil){
        BigDecimal res = new BigDecimal(m*n);
        if(ceil){
            return res.movePointRight(precision).setScale(0,BigDecimal.ROUND_UP).longValue();
        }else {
            return res.movePointRight(precision).longValue();
        }
    }


    private static  String getSigParam(List<StringPair> sigParam){
        StringBuilder param = new StringBuilder();
        sigParam.forEach(par->{
            param.append(par.getFirst()+"="+par.getSecond()+"&");
        });
        return param.deleteCharAt(param.length()-1).toString();
    }


    public static String signParam(String uri,String method,Map<String,String> params) throws UnsupportedEncodingException {
        List<StringPair> list = new LinkedList<>();
        Optional.ofNullable(params).orElseGet(HashMap::new).entrySet().forEach(ent->{
            list.add(new StringPair(ent.getKey(),ent.getValue()));
        });
        Collections.sort(list, Comparator.comparing(StringPair::getFirst));
        String paramString = getSigParam(list);

        String signData = method.toUpperCase()+"\n"+WhaleexConstant.API_BASE_URL+"\n"+uri+"\n"+URLEncoder.encode(paramString,"UTF-8");
        String signature = Ecc.Sign(signData,StoreUtils.getStorePrivateKey());

        return paramString+"&Signature="+signature;
    }
}
