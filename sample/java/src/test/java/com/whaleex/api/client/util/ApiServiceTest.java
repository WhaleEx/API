package com.whaleex.api.client.util;

import com.whaleex.api.client.WhaleexApiService;
import com.whaleex.api.client.impl.WhaleexApiServiceImpl;
import com.whaleex.api.client.pojo.request.AutoOrderRequest;
import com.whaleex.api.client.pojo.response.*;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ApiServiceTest {
    WhaleexApiService apiService= new WhaleexApiServiceImpl();

    // 登录获取access token
    @Test
    public void testLogin() throws IOException {
        final String cn = apiService.getUserToken("", "", "");
        System.out.println(cn);

    }

    // 本地生成公私钥对
    @Test
    public void testGenKeyPair() throws IOException {
        apiService.generateKeys();
        System.out.println("Key pairs are saved into key.properties");
    }

    // 向服务器注册公钥
    @Test
    public void testRegisterPk() throws IOException {
        String token="";
        apiService.registerPK(token,StoreUtils.getStorePublicKey());
    }

    // 检查公钥绑定状态
    @Test
    public void testChectPk() throws IOException {
        String token="";
        CommonResponse<PublicKeyResponse> response = apiService.checkPkBindStatus(token, StoreUtils.getStorePublicKey());
        System.out.println(response.getResult());
    }

    // 生成API key
    @Test
    public void testGenAPIKey() throws IOException {
        String token="";
        String apiKey = apiService.generateApiKey(token).getResult().toString();
        StoreUtils.storeAPIKey(apiKey);
    }

    // 获取10个订单ID
    @Test
    public void testGetGlobleId() throws IOException {
        final CommonResponse<GlobalIdResponse> idFromServer = apiService.getIdFromServer(0, 10);

        System.out.println(idFromServer.getResult());
    }

    // 下单
    @Test
    public void testPlaseOrder(){
        long orderId =92687347103399335l;
        String account ="yyyyyyyyyycc";
        AutoOrderRequest orderRequest = new AutoOrderRequest();
        orderRequest.setType(AutoOrderRequest.OrderType.sell_market);
        orderRequest.setAmount("100");
        orderRequest.setPrice("0.01");
        orderRequest.setSymbol("IQEBTC");
        try {
            CommonResponse commonResponse = apiService.placeOrder(orderId, account, orderRequest);
            System.out.println(commonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 查询订单成交
    @Test
    public void testQueryOneOrderMatchResults(){
        try {
             CommonResponse<PageResponse<TradeResponse>> pageResponseCommonResponse = apiService.queryOneOrderMatchResults(90193004341697964l);
            System.out.println(pageResponseCommonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 撤单
    @Test
    public void testCancelOrder(){
        try {
            final CommonResponse commonResponse = apiService.cancelOrder(92687347103399305l);
            System.out.println(commonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取订单详情
    @Test
    public void testOrderDtl(){
        try {
            final CommonResponse commonResponse = apiService.getOrderDetail(92687347103399305l);
            System.out.println(commonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取用户资产
    @Test
    public void testGetUserAsset(){
        try {
            final CommonResponse commonResponse = apiService.queryAssets();
            System.out.println(commonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSymbol() throws IOException {
        final List<SymbolResponse> symbolResponse = apiService.getSymbol();
        List<SymbolResponse> symbolList = Optional.ofNullable(symbolResponse).orElseGet(ArrayList::new);
        System.out.println("baseCurrency\tbasePrecision\tquoteCurrency\tquotePrecision");
        symbolList.forEach(symbol->{
            System.out.println("\t"+symbol.getBaseCurrency() +"\t\t\t"+symbol.getBasePrecision()+ "\t\t\t"+symbol.getQuoteCurrency()+"\t\t\t"+symbol.getQuotePrecision());
        });
    }
    @Test
    public void testGetOpenOrders() throws IOException {
         CommonResponse openOrders = apiService.getOpenOrders();
        System.out.println(openOrders);

    }
    @Test
    public void testOrderBook() throws IOException {
        final SimpleOrderBookResponse iqebtc = apiService.getOrderBook("IQEBTC");
        System.out.println(iqebtc);
    }
    @Test
    public void testAllCancelOrder(){
        try {
            final CommonResponse commonResponse = apiService.cancelOpenOrders();
            System.out.println(commonResponse.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testAssetShow() throws IOException {
        CommonResponse<AssetSummaryResponse> assetsResponse = apiService.queryAssets();
        AssetSummaryResponse assetSummary = assetsResponse.getResult();
        if(assetSummary != null){
            AssetSummaryResponse.Whale wal = assetSummary.getWal();
            System.out.println("WAL 资产 ");
            System.out.println("可用: "+wal.getFreeAmount());
            System.out.println("私募: "+wal.getPrivatePlacement());
            System.out.println("锁仓: "+wal.getStakeAmount());
            System.out.println("解锁中: "+wal.getUnStakingAmount());

            List<AssetSummaryResponse.AssetContent> assetContents = Optional.ofNullable(assetSummary.getList().getContent())
                    .orElseGet(ArrayList::new);
            assetContents.forEach(asset->{
                System.out.println(" 币种 : "+asset.getCurrency());
                System.out.println(" 可用 : "+asset.getAvailableAmount());
                System.out.println(" 持仓 : "+asset.getFixedAmount());
                System.out.println(" 冻结 : "+asset.getFrozenAmount());
                System.out.println(" 私募 : "+asset.getPrivatePlacement());
                System.out.println(" 总计 : "+asset.getTotalAmount());
                System.out.println("---------------------");
            });
        }
    }
}
