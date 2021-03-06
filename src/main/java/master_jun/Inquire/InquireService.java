package master_jun.Inquire;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
import master_jun.Util.HttpClientUtil;
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

	public static BigDecimal RoundingMoneyBuyAmount(BigDecimal reMoney) {
		if (reMoney.compareTo(new BigDecimal(2000000)) != -1)
			reMoney = reMoney.divide(new BigDecimal(1000)).multiply(new BigDecimal(1000));
		else if (reMoney.compareTo(new BigDecimal(1000000)) != -1 && reMoney.compareTo(new BigDecimal(2000000)) == -1)
			reMoney = reMoney.divide(new BigDecimal(500)).multiply(new BigDecimal(500));
		else if (reMoney.compareTo(new BigDecimal(500000)) != -1 && reMoney.compareTo(new BigDecimal(1000000)) == -1)
			reMoney = reMoney.divide(new BigDecimal(100)).multiply(new BigDecimal(100));
		else if (reMoney.compareTo(new BigDecimal(100000)) != -1 && reMoney.compareTo(new BigDecimal(500000)) == -1)
			reMoney = reMoney.divide(new BigDecimal(50)).multiply(new BigDecimal(50));
		else if (reMoney.compareTo(new BigDecimal(10000)) != -1 && reMoney.compareTo(new BigDecimal(100000)) == -1)
			reMoney = reMoney.divide(new BigDecimal(10)).multiply(new BigDecimal(10));
		else if (reMoney.compareTo(new BigDecimal(1000)) != -1 && reMoney.compareTo(new BigDecimal(10000)) == -1)
			reMoney = reMoney.divide(new BigDecimal(5)).multiply(new BigDecimal(5));
		else if (reMoney.compareTo(new BigDecimal(100)) != -1 && reMoney.compareTo(new BigDecimal(1000)) == -1)
			reMoney = reMoney.setScale(0, RoundingMode.DOWN);
		else if (reMoney.compareTo(new BigDecimal(10)) != -1 && reMoney.compareTo(new BigDecimal(100)) == -1)
			reMoney = reMoney.setScale(1, RoundingMode.DOWN);
		else if (reMoney.compareTo(new BigDecimal(1)) != -1 && reMoney.compareTo(new BigDecimal(10)) == -1)
			reMoney = reMoney.setScale(2, RoundingMode.DOWN);
		else if (reMoney.compareTo(new BigDecimal(0.1)) != -1 && reMoney.compareTo(new BigDecimal(1)) == -1)
			reMoney = reMoney.setScale(3, RoundingMode.DOWN);
		else if (reMoney.compareTo(new BigDecimal(0.1)) == -1)
			reMoney = reMoney.setScale(4, RoundingMode.DOWN);
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
	 * 지갑 반환
	 * 
	 * @date 2022. 7. 5.
	 * @author 레서드
	 * @param CoinNM 코인명
	 * @return
	 * @throws Exception
	 */
	public void getBalanceCoin() throws Exception {
		HttpClientUtil hcu = new HttpClientUtil();
		hcu.sendUpbitGetBalanceCoin();
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
	public void getOrderList(String CoinNM) throws Exception {
		HashMap<String, String> params = new HashMap<>();
		params.put("state", "wait");
		HttpClientUtil hcu = new HttpClientUtil(CoinNM, params);
		hcu.sendUpbitGetOrderList();
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
		HashMap<String, String> params = new HashMap<>();
		params.put("uuid", uuidValue);
		HttpClientUtil hcu = new HttpClientUtil(CoinNM, params);
		hcu.sendUpbitDelete();
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
		BigDecimal reall = RoundingMoneyBuyAmount(new BigDecimal(volume));
		String newVolume = reall.toPlainString();
		HashMap<String, String> params = new HashMap<>();
		params.put("market", CoinNM);
		params.put("side", "bid");
		params.put("volume", newVolume);
		params.put("price", "5000");
		params.put("ord_type", "limit");
		HttpClientUtil hcu = new HttpClientUtil(CoinNM, params);
		hcu.sendUpbitPostBuyCoin();
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
		HashMap<String, String> params = new HashMap<>();
		params.put("market", CoinNM);
		params.put("side", "ask");
		params.put("ord_type", "market");
		HttpClientUtil hcu = new HttpClientUtil(CoinNM, params);
		hcu.sendUpbitSellCoin();
	}
}
