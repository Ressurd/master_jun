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
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

@Component
public class HttpClientUtil {
	private final String accessKey = "";
	private final String secretKey = "";
	private final String serverUrl = "https://api.upbit.com";
    private String jwtToken = "";
    private String jwtToken2 = "";
    private String reqMsg = "";
    private String queryString = "";
    private String queryHash = null;
    private HashMap<String, String> params = null;
    
	public HttpClientUtil() {
		
	}
	
	public HttpClientUtil(String reqMsg) {
		this.reqMsg = reqMsg;
	}
	
	public HttpClientUtil(String reqMsg, HashMap<String, String> params) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        this.params = params;
        
        ArrayList<String> queryElements = new ArrayList<String>();
        
        if(!params.isEmpty()) {
        	for(Map.Entry<String, String> entity : params.entrySet()) {
                queryElements.add(entity.getKey() + "=" + entity.getValue());
            }
        }
        
        String queryString = String.join("&", queryElements.toArray(new String[0]));
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));
        this.queryHash = String.format("%0128x", new BigInteger(1, md.digest()));
		this.reqMsg = reqMsg;
		this.queryString = queryString;
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
	
	public JSONArray JP(String parsingString) throws Exception {
		
		try {
		JSONParser jp = new JSONParser();
		return (JSONArray) jp.parse(parsingString);
		}catch(Exception e) {
			throw e;
		}
	}
	
	/* 
	 * 
	 * 
	 * 떨거지들 
	 * 
	 * 
	 * */
	public JSONArray sendUpbitGet() {
		String result = "";
		jwtToken = getJwtToken();
        JSONArray jsonarray = new JSONArray();
        
		try {
			String authenticationToken = "Bearer " + jwtToken;

            HttpClient client = HttpClientBuilder.create().build();

            HttpGet request = new HttpGet(serverUrl + reqMsg + queryString);

            request.setHeader("Content-Type", "application/json");

            request.addHeader("Authorization", authenticationToken);

            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();

            result = EntityUtils.toString(entity, "UTF-8");

            //System.out.println("http get 요청 : "+result);
			/* Object objtemp = null; */
			//JSONParser jsonParser = new JSONParser();
			// objtemp = jsonParser.parse(response.body());
			//jsonarray = (JSONArray) jsonParser.parse(result);
        	//System.out.println();.out.println();
            //System.out.println(result);
            jsonarray = JP(result);
            
        } catch (Exception e) {

            e.printStackTrace();

        }
		
		return jsonarray;
          

	}
	
	
	/* 
	 * 
	 * 주문하기 
	 * 
	 * */
	public JSONArray sendUpbitPost() {
		String result = "";
		jwtToken = getJwtToken();
        JSONArray jsonarray = new JSONArray();
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
            System.out.println("http post 주문 : "+result);
            jsonarray = JP(result);

         } catch (Exception e) {

             e.printStackTrace();

         }
 		
 		return jsonarray;
	}
	
	/* 
	 * 
	 * 주문취소 
	 * 
	 * */
	public String sendUpbitDelete() {
		String result = "";
		jwtToken = getJwtToken();

		try {
			String authenticationToken = "Bearer " + jwtToken;

            HttpClient client = HttpClientBuilder.create().build();

            HttpDelete request = new HttpDelete(serverUrl + reqMsg + queryString);

            request.setHeader("Content-Type", "application/json");

            request.addHeader("Authorization", authenticationToken);
            
            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();
            
            result = EntityUtils.toString(entity, "UTF-8");
            
        } catch (IOException e) {

            e.printStackTrace();

        }
		return result;
	}

}
