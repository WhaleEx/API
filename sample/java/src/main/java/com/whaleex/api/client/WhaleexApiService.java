package com.whaleex.api.client;

import com.whaleex.api.client.pojo.request.AutoOrderRequest;
import com.whaleex.api.client.pojo.response.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface WhaleexApiService {

    /**
     * 登录 获取token
     */
    String getUserToken(String userName,String password,String countryCode) throws IOException;

    /**
     * 生产公私钥
     * @return
     */
    void generateKeys();

    /**
     * 注册公钥
     */
    CommonResponse registerPK(String accessToken,String pk) throws IOException;

    /**
     * 检查公钥绑定状态
     */
    CommonResponse<PublicKeyResponse> checkPkBindStatus(String accessToken, String pk) throws IOException;

    /**
     * 生成apiKey
     * @param accessToken
     * @return
     */
    CommonResponse generateApiKey(String accessToken) throws IOException;

    /**
     * 获取orderId
     */
    CommonResponse<GlobalIdResponse> getIdFromServer(int remark, int size) throws IOException;

    /**
     * 下单
     */
    CommonResponse placeOrder(Long orderId, String account, AutoOrderRequest order) throws IOException;

    /**
     * 撤单
     */
    CommonResponse cancelOrder(long orderId) throws IOException;

    /**
     * 订单详情
     */
    CommonResponse<CurrentOrderResponse> getOrderDetail(long orderId) throws IOException;


    /**
     * 成交详情
     */
    CommonResponse<PageResponse<TradeResponse>> queryOneOrderMatchResults(long orderId) throws IOException; //todo  pageimpl


    /**
     * 用户资产
     * @return
     * @throws IOException
     */
    CommonResponse queryAssets() throws IOException;

    /**
     * 交易对
     * @return
     * @throws IOException
     */
    List<SymbolResponse> getSymbol() throws IOException;

    CommonResponse<PageResponse<CurrentOrderResponse>> getOpenOrders() throws IOException;

    /**
     * 获取订单簿
     * @return
     */
    SimpleOrderBookResponse getOrderBook(String symbol) throws IOException;

    CommonResponse cancelOpenOrders() throws IOException;
}
