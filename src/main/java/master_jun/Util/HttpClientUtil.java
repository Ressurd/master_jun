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

import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
	private String accessKey = System.getenv("UPBIT_OPEN_API_ACCESS_KEY");
	private String secretKey = System.getenv("UPBIT_OPEN_API_SECRET_KEY");
	private String serverUrl = System.getenv("UPBIT_OPEN_API_SERVER_URL");
    private String jwtToken = "";
    private String reqMsg = "";
    private String queryHash = null;
    HashMap<String, String> params = null;
    
	public HttpClientUtil(String reqMsg) {
		this.reqMsg = reqMsg;
        this.jwtToken = getJwtToken();
	}
	
	public HttpClientUtil(String reqMsg, HashMap<String, String> params, ArrayList<String> queryElements) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        this.jwtToken = getJwtToken();
        this.params = params;
        for(Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }

        String queryString = String.join("&", queryElements.toArray(new String[0]));
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
	 * 주문하기 
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
            
            System.out.println("sendUpbit result: "+result);

        } catch (IOException e) {

            e.printStackTrace();

        }
		return result;
	}
	
	/* 
	 * 
	 * 주문취소 
	 * 
	 * */
	public String sendUpbitdelete() {
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
