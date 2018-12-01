package com.whaleex.api.client.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.whaleex.api.client.WhaleexApiService;
import com.whaleex.api.client.constant.WhaleexConstant;
import com.whaleex.api.client.pojo.Symbol;
import com.whaleex.api.client.pojo.request.AutoOrderRequest;
import com.whaleex.api.client.pojo.response.*;
import com.whaleex.api.client.util.HttpUtils;
import com.whaleex.api.client.util.JsonUtils;
import com.whaleex.api.client.util.SignUtils;
import com.whaleex.api.client.util.StoreUtils;
import com.whaleex.api.client.util.crypto.ec.EosPrivateKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WhaleexApiServiceImpl implements WhaleexApiService {

    HttpUtils httpUtils = new HttpUtils();
    Log log = LogFactory.getLog(WhaleexApiServiceImpl.class);

    @Override
    public String getUserToken(String userName, String password, String countryCode) throws IOException {
        String responseJson = httpUtils.doLoginRequest(userName, password, countryCode);
        if(responseJson!=null){
            TokenResponse tokenResponse = JsonUtils.getObjectMapper().readValue(responseJson, TokenResponse.class);
            if(tokenResponse != null){
                return tokenResponse.getAccess_token();
            }

        }
        return null;
    }

    @Override
    public void generateKeys() {
        EosPrivateKey eosPrivateKey = new EosPrivateKey();
        StoreUtils.storePrivateKye(eosPrivateKey.toString());
        StoreUtils.storePublicKye(eosPrivateKey.getPublicKey().toString());
        log.info(" 生产公私钥成功 -- keys.properties");
        log.info("private key: " + eosPrivateKey);
        log.info("public key: " + eosPrivateKey.getPublicKey());
    }

    @Override
    public CommonResponse registerPK(String accessToken, String pk) throws IOException {
        String path ="/api/account/pk4api";
        Map<String,String> param = new HashMap<>();
        param.put("pk",pk);
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization","bearer "+accessToken);
        String responseJson = httpUtils.doPost(path, param, null, headers);
        CommonResponse commonResponse = JsonUtils.getObjectMapper().readValue(responseJson, CommonResponse.class);
        return commonResponse;
    }

    @Override
    public CommonResponse<PublicKeyResponse> checkPkBindStatus(String accessToken, String pk) throws IOException {
        String path ="/api/auth/pk/status";
        Map<String,String> param = new HashMap<>();
        param.put("pk",pk);
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization","bearer "+accessToken);
        String responseJson = httpUtils.doGet(path, param,  headers);
        CommonResponse<PublicKeyResponse> commonResponse = JsonUtils.getObjectMapper().readValue(responseJson,
                new TypeReference<CommonResponse<PublicKeyResponse>>(){});
        return commonResponse;
    }

    @Override
    public CommonResponse generateApiKey(String accessToken) throws IOException {
        String path ="/api/user/apiKey";
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization","bearer "+accessToken);
        String responseJson = httpUtils.doPost(path, null,null,  headers);
        CommonResponse commonResponse = JsonUtils.getObjectMapper().readValue(responseJson,
                new TypeReference<CommonResponse<String>>(){});
        return commonResponse;
    }

    @Override
    public CommonResponse<GlobalIdResponse> getIdFromServer(int remark, int size) throws IOException {
        String uri ="/api/v1/order/globalIds";
        Map<String,String> signParamMap = new HashMap<>();
        signParamMap.put(WhaleexConstant.API_KEY,StoreUtils.getStoreAPIkey());
        signParamMap.put("timestamp",System.currentTimeMillis()+"");
        signParamMap.put("pk",StoreUtils.getStorePublicKey());
        signParamMap.put("remark",remark+"");
        signParamMap.put("size",size+"");
        String param = SignUtils.signParam(uri,"GET",signParamMap);


        String responseJson = httpUtils.doGet(uri+"?"+param,null,null);
        CommonResponse commonResponse = JsonUtils.getObjectMapper().readValue(responseJson,
                new TypeReference<CommonResponse<GlobalIdResponse>>(){});

        return commonResponse;
    }

    @Override
    public CommonResponse placeOrder(Long orderId, String account, AutoOrderRequest order) throws IOException {
        String uri ="/api/v1/order/orders/place";
        Symbol symbol = new Symbol();
        symbol.setQuoteCurrency("EBTC");
        symbol.setQuoteContract("whaleextoken");
        symbol.setQuotePrecision(8);

        symbol.setBaseCurrency("IQS");
        symbol.setBaseContract("whaleextoken");
        symbol.setBasePrecision(3);
        String params = SignUtils.signDateOrder(order,account,orderId,symbol);
        String responseJson = httpUtils.doPost(uri+"?"+params,null,order);
        CommonResponse commonResponse = JsonUtils.getObjectMapper().readValue(responseJson,CommonResponse.class);

        return commonResponse;
    }

    @Override
    public CommonResponse cancelOrder(long orderId) throws IOException {
        String uri = "/api/v1/order/orders/" + orderId + "/submitcancel";
        Map<String,String> signParamMap = new HashMap<>();
        signParamMap.put(WhaleexConstant.API_KEY,StoreUtils.getStoreAPIkey());
        signParamMap.put("timestamp",System.currentTimeMillis()+"");
        signParamMap.put("pk",StoreUtils.getStorePublicKey());
        String params = SignUtils.signParam(uri,"POST",signParamMap);
        String responseJson = httpUtils.doPost(uri+"?"+params,null,null);
        CommonResponse commonResponse = JsonUtils.getObjectMapper().readValue(responseJson,CommonResponse.class);
        return commonResponse;
    }

    @Override
    public CommonResponse<CurrentOrderResponse> getOrderDetail(long orderId) throws IOException {
        String uri = "/api/v1/order/orders/" + orderId;
        Map<String,String> signParamMap = new HashMap<>();
        signParamMap.put(WhaleexConstant.API_KEY,StoreUtils.getStoreAPIkey());
        signParamMap.put("timestamp",System.currentTimeMillis()+"");
        signParamMap.put("pk",StoreUtils.getStorePublicKey());
        String params = SignUtils.signParam(uri,"GET",signParamMap);
        String responseJson = httpUtils.doGet(uri+"?"+params,null,null);
        CommonResponse commonResponse = JsonUtils.getObjectMapper().readValue(responseJson,
                new TypeReference<CommonResponse<CurrentOrderResponse>>(){});
        return commonResponse;
    }

    @Override
    public CommonResponse<PageResponse<TradeResponse>> queryOneOrderMatchResults(long orderId) throws IOException {
        String uri = "/api/v1/order/orders/" + orderId + "/matchresults";
        Map<String,String> signParamMap = new HashMap<>();
        signParamMap.put(WhaleexConstant.API_KEY,StoreUtils.getStoreAPIkey());
        signParamMap.put("timestamp",System.currentTimeMillis()+"");
        signParamMap.put("pk",StoreUtils.getStorePublicKey());

        signParamMap.put("page","0");
        signParamMap.put("size","20");
        String params = SignUtils.signParam(uri,"GET",signParamMap);
        String responseJson = httpUtils.doGet(uri+"?"+params,null,null);
        CommonResponse commonResponse = JsonUtils.getObjectMapper().readValue(responseJson,
                new TypeReference<CommonResponse<PageResponse<CurrentOrderResponse>>>(){});
        return commonResponse;
    }

    @Override
    public CommonResponse queryAssets() throws IOException {
        String uri = "/api/v1/assets";
        Map<String,String> signParamMap = new HashMap<>();
        signParamMap.put(WhaleexConstant.API_KEY,StoreUtils.getStoreAPIkey());
        signParamMap.put("timestamp",System.currentTimeMillis()+"");
        signParamMap.put("pk",StoreUtils.getStorePublicKey());
        String params = SignUtils.signParam(uri,"GET",signParamMap);
        String responseJson = httpUtils.doGet(uri+"?"+params,null,null);
        CommonResponse commonResponse = JsonUtils.getObjectMapper().readValue(responseJson,
                new TypeReference<CommonResponse<AssetSummaryResponse>>(){});
        return commonResponse;
    }

    @Override
    public List<SymbolResponse> getSymbol() throws IOException {
        String uri = "/api/public/symbol";
        String responseJson = httpUtils.doGet(uri, null);
        List<SymbolResponse> responses = JsonUtils.getObjectMapper().readValue(responseJson,
                new TypeReference<List<SymbolResponse>>(){});

        return responses;
    }

    @Override
    public CommonResponse<PageResponse<CurrentOrderResponse>> getOpenOrders() throws IOException {
        String uri="/api/v1/order/openOrders";
        Map<String,String> signParamMap = new HashMap<>();
        signParamMap.put(WhaleexConstant.API_KEY,StoreUtils.getStoreAPIkey());
        signParamMap.put("timestamp",System.currentTimeMillis()+"");
        signParamMap.put("pk",StoreUtils.getStorePublicKey());

        signParamMap.put("page","0");
        signParamMap.put("size","20");
        String params = SignUtils.signParam(uri,"GET",signParamMap);

        String responseJson = httpUtils.doGet(uri+"?"+params,null,null);
        CommonResponse commonResponse = JsonUtils.getObjectMapper().readValue(responseJson,
                new TypeReference<CommonResponse<PageResponse<CurrentOrderResponse>>>(){});
        return commonResponse;
    }

    @Override
    public SimpleOrderBookResponse getOrderBook(String symbol) throws IOException {
        String uri="/api/public/v1/orderBook/"+symbol;
        String responseJson = httpUtils.doGet(uri, null);
        SimpleOrderBookResponse responses = JsonUtils.getObjectMapper().readValue(responseJson,
                new TypeReference<SimpleOrderBookResponse>(){});
        return responses;
    }

    @Override
    public CommonResponse cancelOpenOrders() throws IOException {
        String uri="/api/v1/order/orders/batchCancelOpenOrders";
        Map<String,String> signParamMap = new HashMap<>();
        signParamMap.put(WhaleexConstant.API_KEY,StoreUtils.getStoreAPIkey());
        signParamMap.put("timestamp",System.currentTimeMillis()+"");
        signParamMap.put("pk",StoreUtils.getStorePublicKey());
        String params = SignUtils.signParam(uri,"POST",signParamMap);
        String responseJson = httpUtils.doPost(uri+"?"+params,null,null);
        CommonResponse commonResponse = JsonUtils.getObjectMapper().readValue(responseJson,CommonResponse.class);
        return commonResponse;

    }

}
