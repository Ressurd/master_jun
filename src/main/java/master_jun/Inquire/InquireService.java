package master_jun.Inquire;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;

import master_jun.Service.ChartService;
import master_jun.Service.ExchangeService;
import master_jun.Util.OkHttpClientUtil;

/**
 * 코인값 조회 서비스
 * 
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

	public String getAccessKey() {
		return "";
	}

	public String getSecretKey() {
		return "";
	}

	static List<Map<String, Object>> myMarketCoin = new ArrayList<Map<String, Object>>();

	public Map<String, Object> setValue(String CoinName, String uuid, int cnt) throws Exception {
		Map<String, Object> marketCoinValidation = new HashMap<>();
		marketCoinValidation.put("coin", CoinName);
		marketCoinValidation.put("uuid", uuid);
		marketCoinValidation.put("cnt", cnt);
		return marketCoinValidation;
	}

	@SuppressWarnings("unchecked")
	//@Scheduled(fixedDelay = 5500)
	public void GETCOIN() throws Exception {
		myMarketCoin.add(setValue("BTC", "etest", 300));
	}

	/**
	 * 나도 스케쥴 한번만..
	 * 
	 * @date 2022. 7. 5.
	 * @author 레서드
	 * @throws Exception
	 */
	@Scheduled(fixedDelay = 1000)
	public void ScheduleTest() throws Exception {
		// System.out.println("내가가진 BTC 갯수는? : " + getBalanceCoin("KRW-BTC"));
		// System.out.println(getOrderList("KRW-BTC"));
		//getOrderList("KRW-BTC");
		if(myMarketCoin.size() != 0) {
			for(int i = 0 ; i < myMarketCoin.size(); i++) { 
				int cnt = (int)myMarketCoin.get(i).get("cnt")-1; 
				myMarketCoin.get(i).put("cnt", cnt);
				if(cnt < 0) { 
					System.out.println("작아졌어!"); 
					cancelCoin(myMarketCoin.get(i).get("coin").toString(), myMarketCoin.get(i).get("uuid").toString());
					myMarketCoin.remove(i); 
				}
			}
		}

		System.out.println(myMarketCoin.toString());
	}

	public double RoundingMoneyBuyAmount(double reMoney) {
		if (reMoney >= 2000000)
			reMoney = Math.round(reMoney / 1000) * 1000;
		else if (reMoney >= 1000000 && reMoney < 2000000)
			reMoney = Math.round(reMoney / 500) * 500;
		else if (reMoney >= 500000 && reMoney < 1000000)
			reMoney = Math.round(reMoney / 100) * 100;
		else if (reMoney >= 100000 && reMoney < 500000)
			reMoney = Math.round(reMoney / 50) * 50;
		else if (reMoney >= 10000 && reMoney < 100000)
			reMoney = Math.round(reMoney / 10) * 10;
		else if (reMoney >= 1000 && reMoney < 10000)
			reMoney = Math.round(reMoney / 5) * 5;
		else if (reMoney >= 100 && reMoney < 1000)
			reMoney = Math.round(reMoney / 1) * 1;
		else if (reMoney >= 10 && reMoney < 100)
			reMoney = Math.round(reMoney / 0.1) * 0.1;
		else if (reMoney >= 1 && reMoney < 10)
			reMoney = Math.round(reMoney / 0.01) * 0.01;
		else if (reMoney >= 0.1 && reMoney < 1)
			reMoney = Math.round(reMoney / 0.001) * 0.001;
		else if (reMoney < 0.1)
			reMoney = Math.round(reMoney / 0.0001) * 0.0001;
		return reMoney;
	}

	/**
	 * 유용할지 모르겠는데 JSONArray 파서임 ㅎㅎ;; ㅈㅅ.. ㅋㅋ!! 나편할려고 만듬
	 * 
	 * @date 2022. 7. 5.
	 * @author 레서드
	 * @param parsingString
	 * @return JSONArray
	 * @throws Exception
	 */
	public JSONArray JP(String parsingString) throws Exception {
		JSONParser jp = new JSONParser();
		return (JSONArray) jp.parse(parsingString);
	}

	/**
	 * 가지고있는 코인 중 원하는코인명 밸런스(코인갯수) 반환
	 * 
	 * @date 2022. 7. 5.
	 * @author 레서드
	 * @param CoinNM 코인명
	 * @return
	 * @throws Exception
	 */
	public String getBalanceCoin(String CoinNM) throws Exception {
		String accessKey = getAccessKey();
		String secretKey = getSecretKey();
		String serverUrl = "https://api.upbit.com";
		String returnBalance = "";
		String Coin = "";
		Coin = CoinNM.replace("KRW-", "");

		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		String jwtToken = JWT.create().withClaim("access_key", accessKey)
				.withClaim("nonce", UUID.randomUUID().toString()).sign(algorithm);

		String authenticationToken = "Bearer " + jwtToken;

		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(serverUrl + "/v1/accounts");
			request.setHeader("Content-Type", "application/json");
			request.addHeader("Authorization", authenticationToken);

			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String temp = EntityUtils.toString(entity, "UTF-8").toString();
			JSONArray jsonArr = JP(temp);

			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArr.get(i);
				// System.out.println(jsonObj.get("currency").equals(CoinNM));
				if (jsonObj.get("currency").equals(Coin)) {
					// System.out.println("coinName : "+jsonObj.get("currency")+", balance :
					// "+jsonObj.get("balance"));
					returnBalance = jsonObj.get("balance").toString();
				}

			}
			System.out.println(temp);
			// System.out.println(EntityUtils.toString(entity, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return returnBalance;
	}

	/**
	 * 주문한거 리스트 뽑는건데 지금은 uuid만 쓸꺼임 + cnt까지
	 * 
	 * @date 2022. 7. 5.
	 * @author 레서드
	 * @param CoinNM "KRW-BTC" ...
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getOrderList(String CoinNM) throws Exception {
		String accessKey = getAccessKey();
		String secretKey = getSecretKey();
		String serverUrl = "https://api.upbit.com";
		List<Map<String, Object>> returnUuid = new ArrayList<Map<String, Object>>();
		
		HashMap<String, String> params = new HashMap<>();
		params.put("state", "wait");

		ArrayList<String> queryElements = new ArrayList<>();
		for (Map.Entry<String, String> entity : params.entrySet()) {
			queryElements.add(entity.getKey() + "=" + entity.getValue());
		}

		String queryString = String.join("&", queryElements.toArray(new String[0]));
		System.out.println("queryString : " + queryString);
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
			HttpGet request = new HttpGet(serverUrl + "/v1/orders?" + queryString);
			request.setHeader("Content-Type", "application/json");
			request.addHeader("Authorization", authenticationToken);

			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String temp = EntityUtils.toString(entity, "UTF-8").toString();
			JSONArray jsonArr = JP(temp);
			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArr.get(i);
				System.out.println(jsonObj.get("market"));
				if (jsonObj.get("market").equals(CoinNM)) {
					if(myMarketCoin.size() == 0) {
						myMarketCoin.add(setValue(CoinNM, jsonObj.get("uuid").toString(), 5));
					}
					else {
						for (int j = 0; j < myMarketCoin.size(); j++) {
							if (!myMarketCoin.get(j).get("uuid").equals(jsonObj.get("uuid").toString())){
								myMarketCoin.add(setValue(CoinNM, jsonObj.get("uuid").toString(), 300));
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnUuid;
	}

	/**
	 * 코인 캔슬하기
	 * 
	 * @date 2022. 7. 5.
	 * @author 레서드
	 * @param CoinNM    취소할 코인명명
	 * @param uuidValue 반드시 getOrderList 를 거쳐서 list에서 값뽑아서 uuid값 사용하자
	 * @throws Exception
	 */
	public void cancelCoin(String CoinNM, String uuidValue) throws Exception {
		String accessKey = getAccessKey();
		String secretKey = getSecretKey();
		String serverUrl = "https://api.upbit.com";

		HashMap<String, String> params = new HashMap<>();
		params.put("uuid", uuidValue);

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
			HttpDelete request = new HttpDelete(serverUrl + "/v1/order?" + queryString);
			request.setHeader("Content-Type", "application/json");
			request.addHeader("Authorization", authenticationToken);

			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();

			System.out.println(EntityUtils.toString(entity, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 지정가 매수
	 * 
	 * @date 2022. 7. 5.
	 * @author 레서드
	 * @param CoinNM 코인명
	 * @param volume 매수 지정가
	 * @throws Exception
	 */
	public void buyCoin(String CoinNM, String volume) throws Exception {
		String accessKey = getAccessKey();
		String secretKey = getSecretKey();
		String serverUrl = "https://api.upbit.com";

		double templong = 5000d;
		double reall = 0d;
		reall = RoundingMoneyBuyAmount(templong / Double.parseDouble(volume));
		String tempVolume = Double.toString(reall);

		HashMap<String, String> params = new HashMap<>();
		params.put("market", CoinNM);
		params.put("side", "bid");
		params.put("volume", tempVolume);
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
			getOrderList(CoinNM);
			System.out.println(EntityUtils.toString(entity, "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 시장가 매도
	 * 
	 * @date 2022. 7. 5.
	 * @author 레서드
	 * @param CoinNM 코인명
	 * @throws Exception
	 */
	public void sellCoin(String CoinNM) throws Exception {
		String accessKey = getAccessKey();
		String secretKey = getSecretKey();
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
