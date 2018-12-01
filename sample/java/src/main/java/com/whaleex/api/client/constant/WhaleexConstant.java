package com.whaleex.api.client.constant;

/**
 * Constants used throughout Whaleex's API.
 */
public class WhaleexConstant {

    /**
     * REST API base URL.
     */
    public static final String API_BASE_URL = "trade.stg.whaleex.com.cn";
//    public static final String API_BASE_URL = "127.0.0.1:8888";

    public static final String BUSINESS_URL_PREFIX ="https://"+API_BASE_URL+"/BUSINESS";
    public static final String UAA_URL_PREFIX ="https://"+API_BASE_URL+"/UAA";

    public static final String EX_EOSACCOUNT ="whaleexchang";

    public static final String EX_ACCOUNT ="whaleex12345";

    public static final String USER_EOSACCOUNT="yyyyyyyyyycc";


    /**
     * HTTP Header to be used for API-KEY authentication.
     */
    public static final String API_KEY = "APIKey";


}
