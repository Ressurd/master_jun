package master_jun.Util;


import com.auth0.jwt.JWT;

import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.json.simple.JSONObject;
import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

@Component
public class HttpClientUtil {
	private String accessKey = "e62t7jKhzPMp14xmgAG54vAzhCLbiXAhxuPasFSl";
	private String secretKey = "Dx37xf7bRe7gxaURNmibUMjGqxpviacn9AgxqSUx";
	private final String serverUrl = "https://api.upbit.com";
    private String jwtToken = "";
    private String reqMsg = "";
    private String queryHash = null;
    private HashMap<String, String> params = null;
    
	public HttpClientUtil() {
		
	}
	
	public HttpClientUtil(JSONObject key) {
		accessKey = (String) key.get("access_key");
		secretKey = (String) key.get("secret_key");
        this.jwtToken = getJwtToken();
        System.out.println("UTIL : " + jwtToken.toString());
	}
	
	
	public HttpClientUtil(String reqMsg) {
		this.reqMsg = reqMsg;
        this.jwtToken = getJwtToken();
	}
	
	public HttpClientUtil(String reqMsg, HashMap<String, String> params) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        this.jwtToken = getJwtToken();
        this.params = params;
        System.out.println("UTIL : " + jwtToken.toString());
    	System.out.println("HttpClientUtil - 1");
        ArrayList<String> queryElements = new ArrayList<>();
        System.out.println("HttpClientUtil - 2");
        for(Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }
        System.out.println("HttpClientUtil - 3");
        String queryString = String.join("&", queryElements.toArray(new String[0]));
        System.out.println("HttpClientUtil - 4");
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));
        this.queryHash = String.format("%0128x", new BigInteger(1, md.digest()));
        this.reqMsg = reqMsg+queryString;
	}
	
	
	public String getJwtToken() {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		
		if(queryHash == null) {
	        return JWT.create()
	                .withClaim("access_key", accessKey)
	                .withClaim("nonce", UUID.randomUUID().toString())
	                .sign(algorithm);}
		else {
			return JWT.create()

	                .withClaim("access_key", accessKey)
	                .withClaim("nonce", UUID.randomUUID().toString())
	                .withClaim("query_hash", queryHash)
	                .withClaim("query_hash_alg", "SHA512")
	                .sign(algorithm);
		}
	}
	
	/*
	 * reqMsg
	 * 총자산 get /v1/accounts
	 * 주문가능정보 get /v1/orders/chance?
	 * 주문하기 post /v1/orders
	 * 주문취소 delete /v1/order?
	 * 주문리스트조회 get /v1/orders?
	 * 
	 * */
	
	/* 
	 * 
	 * 
	 * 떨거지들 
	 * 
	 * 
	 * */
	public String sendUpbitGet() {
		String result = "";
		try {
			String authenticationToken = "Bearer " + jwtToken;

            HttpClient client = HttpClientBuilder.create().build();

            HttpGet request = new HttpGet(serverUrl + reqMsg);

            request.setHeader("Content-Type", "application/json");

            request.addHeader("Authorization", authenticationToken);

            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();

            result = EntityUtils.toString(entity, "UTF-8");
            
            System.out.println("sendUpbit result: "+result);

        } catch (IOException e) {

            e.printStackTrace();

        }
		return result;
	}
	
	
	/* 
	 * 
	 * 주문하기 v1
	 * 
	 * */
	public String sendUpbitPost() {
		String result = "";
		
		try {
			String authenticationToken = "Bearer " + jwtToken;
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(serverUrl + "/v1/orders");

            request.setHeader("Content-Type", "application/json");
            request.addHeader("Authorization", authenticationToken);
            request.setEntity(new StringEntity(new Gson().toJson(params)));

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
		} catch (IOException e) {

            e.printStackTrace();

        }
		return result;
	}
	
	/**
	 * 주문하기 new 버전 넘기는 값은 params에 넣어야함.
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public void sendUpbitPost2() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		HashMap<String, String> params = new HashMap<>();
        params.put("market", "KRW-BTC");
        params.put("side", "bid");
        //params.put("volume", "0.01");
        params.put("price", "15000");
        params.put("ord_type", "price");

        ArrayList<String> queryElements = new ArrayList<>();
        for(Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }

        String queryString = String.join("&", queryElements.toArray(new String[0]));

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));

        String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(serverUrl + "/v1/orders");
            request.setHeader("Content-Type", "application/json");
            request.addHeader("Authorization", authenticationToken);
            request.setEntity(new StringEntity(new Gson().toJson(params)));

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            System.out.println(EntityUtils.toString(entity, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * 판매하기 만들기 (아직 가지고있는 값 못받아와서 못만들었쯤)
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	
	public void sendUpbitPost3() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		HashMap<String, String> params = new HashMap<>();
        params.put("market", "KRW-BTC");
        params.put("side", "ask");
        params.put("volume", "0.01");
        //params.put("price", "15000");
        params.put("ord_type", "market");

        ArrayList<String> queryElements = new ArrayList<>();
        for(Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }

        String queryString = String.join("&", queryElements.toArray(new String[0]));

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));

        String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(serverUrl + "/v1/orders");
            request.setHeader("Content-Type", "application/json");
            request.addHeader("Authorization", authenticationToken);
            request.setEntity(new StringEntity(new Gson().toJson(params)));

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            System.out.println(EntityUtils.toString(entity, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	
	/* 
	 * 
	 * 주문취소 
	 * 
	 * */
	public String sendUpbitDelete() {
		String result = "";
		try {
			String authenticationToken = "Bearer " + jwtToken;

            HttpClient client = HttpClientBuilder.create().build();

            HttpDelete request = new HttpDelete(serverUrl + reqMsg);

            request.setHeader("Content-Type", "application/json");

            request.addHeader("Authorization", authenticationToken);


            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();
            
            System.out.println("sendUpbit result: "+result);

        } catch (IOException e) {

            e.printStackTrace();

        }
		return result;
	}

}
