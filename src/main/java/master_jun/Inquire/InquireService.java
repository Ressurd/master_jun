package master_jun.Inquire;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	
	/**
	 * 나중에 job기능을 이용해서 스케줄러 이용하기.
	 * 추가할 기능 정리
	 *  1. 마켓값 넘기는 ExchangeService 이용
	 *  2. 현재 String으로 나오는데 저기 서비스에서 String to JSON (or List<Map>) 변환 필요
	 *  3. 1초마다 제대로 값이 불러와지는지 확인하기.
	 * @date 2022. 6. 24.
	 * @author 레서드
	 * @param option
	 * @throws Exception 
	 */
	public void job(String option) throws Exception { 
		JSONArray jsnarray= null;
        System.out.println(option + " scheduling job start : " + LocalTime.now());
		//System.out.println(ohcu.getTicker("KRW-BTC"));
		jsnarray = new JSONArray();
		JSONParser jsonParser=new JSONParser();
		jsnarray = (JSONArray) jsonParser.parse(ohcu.getMarketCd());
		//System.out.println(jsnarray);
		jsnarray = (JSONArray) jsonParser.parse(ohcu.getTicker("KRW-BTC"));
		//System.out.println(jsnarray);
		//System.out.println(ohcu.getJsonToList(ohcu.getTicker("KRW-BTC")));
		System.out.println(ohcu.getJsonValue("trade_price", ohcu.getJsonToList(ohcu.getTicker("KRW-BTC"))));
		
        System.out.println(option + " scheduling job end : " + LocalTime.now()); 
    } 
}
