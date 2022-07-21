package master_jun.Inquire;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
	public ExchangeService es;
	
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
	
	public String getKRW() throws Exception{
		JSONArray jsonarray = es.getAccounts();
		String tempCoinNM = "KRW";
		String volume = "";
		for (int i= 0 ; i < jsonarray.size(); i++) {
			JSONObject jsonobj = (JSONObject) jsonarray.get(i);
			if (jsonobj.get("currency").equals(tempCoinNM)) {
				volume = jsonobj.get("balance").toString();
			}
		}
		return volume;
	}

	/**
	 * 총 자산 조회
	 * @date 2022. 7. 14.
	 * @author 레서드
	 * @throws Exception
	 */
	public List<Map<String, String>> getBalanceCoin(List<Map<String, String>> buyList) throws Exception {
		BigDecimal calc = null;
		String coinNm = "";
		boolean chk = true;
		Map<String, String> tempMap = null;
		JSONArray exjArray = es.getAccounts();
		for( int i =0;  i<exjArray.size(); i++) {
			JSONObject temp = (JSONObject) exjArray.get(i);
			calc = BigDecimal.valueOf(Double.parseDouble(temp.get("balance").toString()));
			calc = calc.multiply(BigDecimal.valueOf(Double.parseDouble(temp.get("avg_buy_price").toString())));
			
			if(calc.compareTo(BigDecimal.valueOf(5000)) != -1 && !temp.get("currency").equals("KRW") && !temp.get("currency").equals("KMD")) {
				coinNm = temp.get("unit_currency") + "-" + temp.get("currency");
				tempMap = new HashMap<String, String>();
				System.out.println(coinNm + "   " + calc);
				tempMap.put("market", coinNm);
				for(int j = 0; j<buyList.size(); j++) {
					Map<String, String> buyMap = buyList.get(j);
					if(buyMap.get("market").equals(tempMap.get("market"))) {
						chk=false;
					}
				}
				if(chk) {
					buyList.add(tempMap);
				}else {
					chk=true;
				}
			}
		}
		
		return buyList;
	}

	/**
	 * 주문 리스트 조회
	 * @date 2022. 7. 14.
	 * @author 레서드
	 * @throws Exception
	 */
	public void getOrderList() throws Exception {
		HashMap<String, String> params = new HashMap<>();
		params.put("state", "wait");
		System.out.println(es.getOrderlist(params));
	}

	/**
	 * 주문 취소 접수
	 * @date 2022. 7. 14.
	 * @author 레서드
	 * @param uuidValue - uuid값 필요
	 * @throws Exception
	 */
	public void cancelCoin(String uuidValue) throws Exception {
		HashMap<String, String> params = new HashMap<>();
		params.put("uuid", uuidValue);
		System.out.println(es.getOrderDel(params));
	}

	/**
	 * 지정가 매수
	 * @date 2022. 7. 14.
	 * @author 레서드
	 * @param CoinNM - 코인명
	 * @param volume - 지정가격
	 * @param price - 매수할금액
	 * @throws Exception
	 */
	public void buyCoin(String CoinNM, String volume) throws Exception {
        BigDecimal reall = RoundingMoneyBuyAmount(new BigDecimal(volume));
        BigDecimal testVal = new BigDecimal(getKRW());
        double a = reall.doubleValue();
        double b = testVal.doubleValue();
        b = b / (7 - getTotalCoinNumber());
        HashMap<String, String> params = new HashMap<>();
        params.put("market", CoinNM);
        params.put("side", "bid");
        params.put("volume", String.valueOf((b/a)));
        params.put("price", String.valueOf(reall));
        params.put("ord_type", "limit");
        System.out.println(es.goOrder(params));
	}

	/**
	 * 지정가 매도
	 * @date 2022. 7. 14.
	 * @author 레서드
	 * @param CoinNM - 코인명
	 * @throws Exception
	 */
	public void sellCoin(String CoinNM) throws Exception {
		JSONArray jsonarray = es.getAccounts();
		String tempCoinNM = CoinNM.replace("KRW-", "");
		String volume = "";
		for (int i= 0 ; i < jsonarray.size(); i++) {
			JSONObject jsonobj = (JSONObject) jsonarray.get(i);
			if (jsonobj.get("currency").equals(tempCoinNM)) {
				volume = jsonobj.get("balance").toString();
			}
		}
		
		HashMap<String, String> params = new HashMap<>();
		params.put("market", CoinNM);
		params.put("side", "ask");
		params.put("volume", volume);
		params.put("ord_type", "market");
		System.out.println(es.goOrder(params));

	}
	
	public double getTotalCoinNumber() throws Exception{
        JSONArray jsonarray = es.getAccounts();
        double cnt = 0;
        for (int i= 0 ; i < jsonarray.size(); i++) {
            JSONObject jsonobj = (JSONObject) jsonarray.get(i);
            double temp = Double.parseDouble(jsonobj.get("avg_buy_price").toString());
            if (temp > 0) {
                cnt++;
            }
        }
        return cnt;
    }
}