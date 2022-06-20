package master_jun.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;


import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import master_jun.Util.HttpClientUtil;

@Service
public class ExchangeService {
	
	@Resource
	HttpClientUtil httpClientUtil;
	
	
	/* 총자산 조회 */
	public String getAccounts(HashMap<String, String> params, ArrayList<String> queryElements) {
		
		httpClientUtil = new HttpClientUtil("/v1/accounts");
		
		return httpClientUtil.sendUpbitGet();
	}

	/* 주문 가능 조회 */
	public String getChance(HashMap<String, String> params, ArrayList<String> queryElements) {
	
		try {
			httpClientUtil = new HttpClientUtil("/v1/orders/chance?", params, queryElements);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return httpClientUtil.sendUpbitGet(); 
	}

	/* 개별 주문 조회 */
	public String getOrderChk(HashMap<String, String> params, ArrayList<String> queryElements) {
		
		try {
			httpClientUtil = new HttpClientUtil("/v1/order?", params, queryElements);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return httpClientUtil.sendUpbitGet(); 
	}

	/* 주문 리스트 조회 */
	public String getOderlist(HashMap<String, String> params, ArrayList<String> queryElements) {
			
			try {
				httpClientUtil = new HttpClientUtil("/v1/orders?", params, queryElements);
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return httpClientUtil.sendUpbitGet(); 
		}

	/* 주문 취소 접수 */
	public String getOrderDel(HashMap<String, String> params, ArrayList<String> queryElements) {
		
		try {
			httpClientUtil = new HttpClientUtil("/v1/order?", params, queryElements);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return httpClientUtil.sendUpbitDelete(); 
	}

	/* 주문하기 */
	public String goOrder(HashMap<String, String> params, ArrayList<String> queryElements) {
		
		try {
			httpClientUtil = new HttpClientUtil("/v1/orders", params, queryElements);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return httpClientUtil.sendUpbitPost(); 
	}
	
	
	
}