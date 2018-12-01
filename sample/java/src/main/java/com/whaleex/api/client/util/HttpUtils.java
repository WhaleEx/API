package com.whaleex.api.client.util;

import com.whaleex.api.client.constant.WhaleexConstant;
import com.whaleex.api.client.pojo.request.AutoOrderRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class HttpUtils {


    Log log = LogFactory.getLog(HttpUtils.class);


    public String doGet(String path,Map<String,String> params){
        HttpGet httpGet = new HttpGet(WhaleexConstant.BUSINESS_URL_PREFIX+pathBuilder(path,params));
        return executeHttp(httpGet);

    }
    public String doGet(String path,Map<String,String> params,Map<String,String> headers){
        HttpGet httpGet = new HttpGet(WhaleexConstant.BUSINESS_URL_PREFIX+pathBuilder(path,params));
        if(headers!= null){
            headers.entrySet().forEach(ent->{
                httpGet.addHeader(ent.getKey(),ent.getValue());
            });
        }
        return executeHttp(httpGet);

    }

    public String doPost(String path,Map<String,String> params,Object body){
        HttpPost httpPost = new HttpPost(WhaleexConstant.BUSINESS_URL_PREFIX+pathBuilder(path,params));
        buildPostBody(body, httpPost);
        return executeHttp(httpPost);
    }

    public String doPost(String path,Map<String,String> params,Object body,Map<String,String> headers){

        HttpPost httpPost = new HttpPost(WhaleexConstant.BUSINESS_URL_PREFIX+pathBuilder(path,params));
        buildPostBody(body, httpPost);
        if(headers!= null){
            headers.entrySet().forEach(ent->{
                httpPost.addHeader(ent.getKey(),ent.getValue());
            });
        }
        return executeHttp(httpPost);
    }

    /**
     * 登录
     */
    public String doLoginRequest(String userName,String password,String countryCode){
        String path = "/oauth/token";
        HttpPost httpPost = new HttpPost(WhaleexConstant.UAA_URL_PREFIX+path);

        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("grant_type","password"));
        list.add(new BasicNameValuePair("client_id","client"));
        list.add(new BasicNameValuePair("client_secret","secret"));
        list.add(new BasicNameValuePair("clientType","api"));
        list.add(new BasicNameValuePair("source","api"));
        list.add(new BasicNameValuePair("countryCode",countryCode));
        list.add(new BasicNameValuePair("username",userName));

        list.add(new BasicNameValuePair("password",HashUtils.getHash(password)));

        try {
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(list);
            httpPost.setEntity(uefEntity);

            return executeHttp(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }



    private String pathBuilder(String path,Map<String,String> params){
        if(params == null || params.size() ==0){
            return path;
        }
        StringBuilder pathBuilder = new StringBuilder(path);
        if(path.contains("?")) {
            pathBuilder.append("&");
        }else {
            pathBuilder.append("?");
        }
        Optional.ofNullable(params).orElseGet(HashMap::new).entrySet().forEach(ent->{
            pathBuilder.append(ent.getKey()+"="+ent.getValue()+"&");
        });
        return pathBuilder.toString();
    }

    private String executeHttp(HttpUriRequest request){

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            CloseableHttpResponse execute = httpClient.execute(request);

            if(execute.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(execute.getEntity());
            }else {
                log.warn(EntityUtils.toString(execute.getEntity()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    private void buildPostBody(Object body, HttpPost httpPost) {
        if(body!= null){
            String jsonBody = JsonUtils.Obj2JsonString(body);
            try {
                StringEntity entity = new StringEntity(jsonBody);
                httpPost.setEntity(entity);
                httpPost.addHeader("Content-type", "application/json");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

}
