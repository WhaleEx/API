package com.whaleex.api.client;

import com.whaleex.api.client.constant.WhaleexConstant;
import com.whaleex.api.client.impl.WhaleexApiServiceImpl;
import com.whaleex.api.client.pojo.request.AutoOrderRequest;
import com.whaleex.api.client.pojo.response.*;
import com.whaleex.api.client.util.StoreUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.*;

public class JavaClientApplication {


    private static WhaleexApiService apiService = new WhaleexApiServiceImpl();

    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner sc = new Scanner(System.in);
        System.out.println(" 请输入whaleex 用户名:");
        String phone = sc.nextLine();
        System.out.println(" 请输入whaleex 密码:");
        String password = sc.nextLine();
        System.out.println(" 请输入国家代码 (中国: CN):");
        String countryCode = sc.nextLine();

        String userToken = apiService.getUserToken(phone, password, countryCode);

        final List<String> check = onStartUp();
        if(check.size()>0){

            if(StringUtils.isEmpty(userToken)){
                System.out.println("密码错误");
                System.exit(0);
            }
            System.out.println(" 登录成功  ");

            if(check.contains("APIKey")){
                //没有apiKey  申请一个
                CommonResponse apiKeyResponse = apiService.generateApiKey(userToken);
                if(StringUtils.isNotEmpty(apiKeyResponse.getResult().toString())){
                    System.out.println(" apiKey 生成成功");
                    StoreUtils.storeAPIKey(apiKeyResponse.getResult().toString());
                }
            }
            if(check.contains("privateKey")){
              genAndRegPk(userToken);
            }

        }

        printBindHelpMsg();
        System.out.println(" 是否已完成转账? Y/N");
        String s = sc.nextLine();
        if(s.equalsIgnoreCase("Y")||s.equalsIgnoreCase("YES")) {
            loopPkStatus(userToken);
        }

        showSymbolList();
        showAssets();
        showOpenOrders();
        showOrderBook("IQEBTC");


        CommonResponse<GlobalIdResponse> idFromServer = apiService.getIdFromServer(0, 5);
        if(idFromServer != null){
            GlobalIdResponse idResult = idFromServer.getResult();
            System.out.println(" 开始下单 ...");
            idResult.getList().stream().forEach(orderId->{

                String account =WhaleexConstant.USER_EOSACCOUNT;
                AutoOrderRequest orderRequest = new AutoOrderRequest();
                orderRequest.setType(AutoOrderRequest.OrderType.sell_market);
                orderRequest.setAmount("100");
                orderRequest.setPrice("0.01");
                orderRequest.setSymbol("IQEBTC");

                try {
                    CommonResponse orderResponse = apiService.placeOrder(Long.valueOf(orderId), account, orderRequest);

                    System.out.println(orderResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            System.out.println(" 撤单..");
            CommonResponse cancelResp = apiService.cancelOpenOrders();
            System.out.println(cancelResp.toString());


        }else {
            System.err.println(" 系统错误 ");
           System.exit(0);
        }



    }


    private static void showAssets() throws IOException {
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


    private static void showSymbolList() throws IOException {
        System.out.println(" 交易对信息");
       List<SymbolResponse> symbolResponse =Optional.ofNullable( apiService.getSymbol()).orElseGet(ArrayList::new);
        symbolResponse.forEach(symbol->{
            System.out.println(" baseCurrency  "+symbol.getBaseCurrency() +" | basePrecision "+symbol.getBasePrecision()+
                    " | quoteCurrency "+symbol.getQuoteCurrency()+" | quotePrecision "+symbol.getQuotePrecision());
        });
        System.out.println("-------------------");

    }

    private static void genAndRegPk(String accessToken) throws IOException {
        System.out.println(" 生成本地公私钥...");
        apiService.generateKeys();
        apiService.registerPK(accessToken,StoreUtils.getStorePublicKey());
    }

    private static void printBindHelpMsg(){

        System.out.println(" 本地公私钥注册成功 ");
        System.out.println(" 请向合约账户转账小额eos资产以激活公钥 ");
        System.out.println("合约账户: "+WhaleexConstant.EX_ACCOUNT);
        System.out.println("memo: bind:"+StoreUtils.getStorePublicKey()+":WhaleEx");
    }

    private static void showOrderBook(String symbol) throws IOException {
        SimpleOrderBookResponse orderBook = apiService.getOrderBook(symbol);
        System.out.println("买盘:");
        System.out.println("------------");
        orderBook.getAsks().stream().sorted(Comparator.comparing(a->a.getPrice())).forEach(as->{
            System.out.println("价格:\t"+as.getPrice()+"\t数量:"+as.getQuantity());
        });
        System.out.println("##############");
        System.out.println("卖盘:");
        System.out.println("------------");
        orderBook.getBids().stream().sorted(Comparator.comparing(a->a.getPrice())).forEach(as->{
            System.out.println("价格:\t"+as.getPrice()+"\t数量:"+as.getQuantity());
        });
    }

    private static void loopPkStatus(String userToken) throws InterruptedException {
        System.out.print("检查"+StoreUtils.getStorePublicKey()+"公钥绑定状态.");
        while (!"ACTIVED".equals(checkPublicKeyStatus(userToken))){
            System.out.print(".");
            Thread.sleep(5000l);

        }
        System.out.println("");
        System.out.println(" 公钥 "+StoreUtils.getStorePublicKey()+" 已绑定");
    }

    private static void showOpenOrders() throws IOException {
        System.out.println(" 当前委托订单:  ");
        CommonResponse<PageResponse<CurrentOrderResponse>> openOrders = apiService.getOpenOrders();
        if(openOrders != null){
            PageResponse<CurrentOrderResponse> result = openOrders.getResult();
            result.getContent().forEach(order->{
                System.out.println("orderId "+order.getOrderId());
                System.out.println("price "+order.getPrice());
                System.out.println("createTime "+order.getCreateTime());
                System.out.println("type "+order.getType());
                System.out.println("---------------------");

            });


        }
    }



    private static String checkPublicKeyStatus(String accessToken){
        String pk = StoreUtils.getStorePublicKey();
        try {
            CommonResponse<PublicKeyResponse> response = apiService.checkPkBindStatus(accessToken, pk);
            if(response!= null && response.getResult() != null ){
                return response.getResult().getStatus();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }


    private static List<String> onStartUp(){
        List<String> checkRes = new ArrayList<>();
        if(StringUtils.isEmpty(StoreUtils.getStorePrivateKey())){
            checkRes.add("privateKey");
        }
        if(StringUtils.isEmpty(StoreUtils.getStoreAPIkey())){
            checkRes.add("APIKey");
        }

        return checkRes;
    }
}
