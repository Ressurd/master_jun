package master_jun.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import master_jun.Util.GetKeyUtil;
import master_jun.Util.HttpClientUtil;

@Service
public class mainService {

	@Autowired
	private GetKeyUtil gku;
	
	@Autowired
	private ExchangeService es;
	
	@Autowired
	private HttpClientUtil hcu;
	
	public String upbitBuy(String ss) throws IOException, NoSuchAlgorithmException, UnsupportedEncodingException {
		HashMap<String, String> params2 = new HashMap<>();
		params2.put("market", "KRW-BTC");
		params2.put("side", "bid");
		params2.put("volume", "0.01");
		params2.put("price", "6000");
		params2.put("ord_type", "limit");
		//hcu = new HttpClientUtil(gku.userGetUpbitKeyInfo());
		//es.goOrder2(params2);
		es.goOrder3(params2);
		//hcu.sendUpbitPost();
		System.out.println(ss);
		return "";
	}
	
	public String upbitSell() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		es.goOrder4();
		return "";
	}

}
