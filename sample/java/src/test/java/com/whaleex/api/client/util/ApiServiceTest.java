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
        final String cn = apiService.getUserToken("15838261714", "qweqwe123", "CN");
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
        String token="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJDTjo6MTU4MzgyNjE3MTQiLCJncm91cElkIjoxLCJpc0FkbWluIjpmYWxzZSwiYnlQYXNzd29yZCI6dHJ1ZSwiaGFzUGFzc3dvcmQiOnRydWUsInR5cGUiOiJsb2dpbiIsImNsaWVudF9pZCI6ImNsaWVudCIsImF1ZCI6WyJvYXV0aDItcmVzb3VyY2UiXSwicGhvbmUiOiIxNTgzODI2MTcxNCIsImNvdW50cnlDb2RlIjoiQ04iLCJzY29wZSI6WyJhbGwiXSwiaWQiOjIxLCJleHAiOjE1NDM1MTM1OTQsImp0aSI6IjhhNzhjYTczLWZhNTYtNDMzYi1hYjdjLWZkMTE0MGM5MDhlMCJ9.dJvS2-vHr-ewr9kanrxthUGmHE9TC49QWDK8WrNJvj5BJ11jAuIwk6VJ02kf4XQBYYTRkvrr8unmHg05F9U_IYKX9iRxn154F61nnS7RqmaH-GSYvrK3-L0Lz1aDnHJgGZVCCBQ8wSSNmEyE_1CcI_bBJI9e_GOQ9yXXPIE7ZJoJ-AqbcVvG_C3bvxIXGVm4Y5wRkC_nxplK0YDWg4dURydOhpMS2CJLY9jsETFibhqxZO9APT7m3QopCt8oTeZl-osCzEaFmBdc6xQO4poxY7GUxz1yjCqpl4pmF12s8ar0_eM9RRcjofcoO-1QtOQCZiPQ5NvuwimOaeuvDcgdMQ";
        apiService.registerPK(token,StoreUtils.getStorePublicKey());
    }

    // 检查公钥绑定状态
    @Test
    public void testChectPk() throws IOException {
        String token="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJDTjo6MTU4MzgyNjE3MTQiLCJncm91cElkIjoxLCJpc0FkbWluIjpmYWxzZSwiYnlQYXNzd29yZCI6dHJ1ZSwiaGFzUGFzc3dvcmQiOnRydWUsInR5cGUiOiJsb2dpbiIsImNsaWVudF9pZCI6ImNsaWVudCIsImF1ZCI6WyJvYXV0aDItcmVzb3VyY2UiXSwicGhvbmUiOiIxNTgzODI2MTcxNCIsImNvdW50cnlDb2RlIjoiQ04iLCJzY29wZSI6WyJhbGwiXSwiaWQiOjIxLCJleHAiOjE1NDM1MDE0MTcsImp0aSI6IjA1YzIzMDY5LTQ0NGEtNDU2MS1hOWJmLTBlMTBmYTI2MWVlNCJ9.nU9cm0zjTi3NmnMZ7FkpnNuOxw82V5ObPNfQERV7j2Qpz0ymqgViip7n-LlSZ2XmEYb5RxLbAtacloAGopyKaPj-sw8BP0tQ3VhN80vZPmJFSulwgKmwxcGZxuQDiJzjU1qRyA8ziUARdf7j6yu08v8VnJ7Iw9bDuWGUj184rfb9owg1ILMT6nQu20lyeQm2RpEYwsYcbTdVbe6XKM1sGpECx4qDA9S-LzR1IK5PutqVL_9p9JMSjGvgoQg74V2wri2gMF7iLQQcNYk592enYcza85IzyboygrVHxbqc9LUqMu3aoIoj8hcurByMTxyo33bnQLeZa9suknd3ToP2hQ";
        CommonResponse<PublicKeyResponse> response = apiService.checkPkBindStatus(token, StoreUtils.getStorePublicKey());
        System.out.println(response.getResult());
    }

    // 生成API key
    @Test
    public void testGenAPIKey() throws IOException {
        String token="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJDTjo6MTU4MzgyNjE3MTQiLCJncm91cElkIjoxLCJpc0FkbWluIjpmYWxzZSwiYnlQYXNzd29yZCI6dHJ1ZSwiaGFzUGFzc3dvcmQiOnRydWUsInR5cGUiOiJsb2dpbiIsImNsaWVudF9pZCI6ImNsaWVudCIsImF1ZCI6WyJvYXV0aDItcmVzb3VyY2UiXSwicGhvbmUiOiIxNTgzODI2MTcxNCIsImNvdW50cnlDb2RlIjoiQ04iLCJzY29wZSI6WyJhbGwiXSwiaWQiOjIxLCJleHAiOjE1NDM1MDE0MTcsImp0aSI6IjA1YzIzMDY5LTQ0NGEtNDU2MS1hOWJmLTBlMTBmYTI2MWVlNCJ9.nU9cm0zjTi3NmnMZ7FkpnNuOxw82V5ObPNfQERV7j2Qpz0ymqgViip7n-LlSZ2XmEYb5RxLbAtacloAGopyKaPj-sw8BP0tQ3VhN80vZPmJFSulwgKmwxcGZxuQDiJzjU1qRyA8ziUARdf7j6yu08v8VnJ7Iw9bDuWGUj184rfb9owg1ILMT6nQu20lyeQm2RpEYwsYcbTdVbe6XKM1sGpECx4qDA9S-LzR1IK5PutqVL_9p9JMSjGvgoQg74V2wri2gMF7iLQQcNYk592enYcza85IzyboygrVHxbqc9LUqMu3aoIoj8hcurByMTxyo33bnQLeZa9suknd3ToP2hQ";
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
