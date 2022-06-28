package master_jun.Inquire;

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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;

import master_jun.Service.ChartService;
import master_jun.Service.ExchangeService;
import master_jun.Util.OkHttpClientUtil;

/**
 * 코인값 조회 서비스
 * @date 2022. 6. 24.
 * @author 레서드
 *
 */
@Service
public class InquireService {
	@Autowired
	public InquireDAO inquireDAO;

	@Autowired
	public OkHttpClientUtil ohcu;
	
	@Autowired
	public ChartService cs;
	
	@Autowired
	public ExchangeService es;
	
	
	public void buyCoin(String CoinNM, String volume) throws Exception{
		String accessKey = "님 엑세스키";
        String secretKey = "님 쉬크릿 키";
        String serverUrl = "https://api.upbit.com";
        
        String temp = null;
        double templong = 5000d;
        double reall = 0d;
        reall = templong / Double.parseDouble(volume);
        
        System.out.println(reall);
        temp = Double.toString(reall);
		
		HashMap<String, String> params = new HashMap<>();
		params.put("market", CoinNM);
		params.put("side", "bid");
		params.put("volume", temp);
		params.put("price", "5000");
		params.put("ord_type", "limit");

		ArrayList<String> queryElements = new ArrayList<>();
		for (Map.Entry<String, String> entity : params.entrySet()) {
			queryElements.add(entity.getKey() + "=" + entity.getValue());
		}

		String queryString = String.join("&", queryElements.toArray(new String[0]));

		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(queryString.getBytes("UTF-8"));

		String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		String jwtToken = JWT.create().withClaim("access_key", accessKey)
				.withClaim("nonce", UUID.randomUUID().toString()).withClaim("query_hash", queryHash)
				.withClaim("query_hash_alg", "SHA512").sign(algorithm);

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
	
	public void sellCoin(String CoinNM) throws Exception {
		String accessKey = "님 엑세스키";
        String secretKey = "님 쉬크릿 키";
        String serverUrl = "https://api.upbit.com";
        
		HashMap<String, String> params = new HashMap<>();
		params.put("market", CoinNM);
		params.put("side", "ask");
		params.put("ord_type", "market");

		ArrayList<String> queryElements = new ArrayList<>();
		for (Map.Entry<String, String> entity : params.entrySet()) {
			queryElements.add(entity.getKey() + "=" + entity.getValue());
		}

		String queryString = String.join("&", queryElements.toArray(new String[0]));

		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(queryString.getBytes("UTF-8"));

		String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		String jwtToken = JWT.create().withClaim("access_key", accessKey)
				.withClaim("nonce", UUID.randomUUID().toString()).withClaim("query_hash", queryHash)
				.withClaim("query_hash_alg", "SHA512").sign(algorithm);

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
}
