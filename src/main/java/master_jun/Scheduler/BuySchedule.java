package master_jun.Scheduler;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import master_jun.Service.ChartService;
import master_jun.Util.OkHttpClientUtil;

@Component
public class BuySchedule {
	
	@Autowired
	public OkHttpClientUtil ohcu;
	
	@Autowired
	public ChartService chartService;
	
	static int CycCnt= 1;
	static List<Map> coinList = null;
	static List<Map<String,String>> buyList = new ArrayList<Map<String,String>>();//매수 목록
	static List<String> banList = new ArrayList<String>();//잠시 타점 분석 제외
	
	/* 
	 * 코인리스트 불러오기 
	 * */
	@Scheduled(cron="0 0 0/1 * * *")
	public void CoinListSearch() throws Exception{
		coinList = chartService.getCoinList("KRW");
	}
	
	/*
	 * 매수스케줄 0626 수정
	 */
	@Scheduled(fixedDelay=100)
	public void BuySearch() throws Exception{
		System.out.println(CycCnt+"번 싸이클 시작 : " + LocalTime.now()); 
		BigDecimal temp_low = null;
		BigDecimal temp_high = null;
		BigDecimal stanHigh = null;//현재가 고가
		BigDecimal stanLow = null;//현재가 저가
		BigDecimal stanNow = null;//현재가 현재가
		String coinNm = "";
		Map <String, Object> standichimokuMap = null;
		Map <String, Object> realichimokuMap = null;
		Map <String, Object> bbMap = null;
		JSONArray jsArray = null;
		JSONObject tmpJson = null;
		HashMap tmpMap = new HashMap<String, BigDecimal>();
		int sleepCnt = 0;
		BigDecimal ma60 = null;
		BigDecimal ma120 = null;
		BigDecimal ma180 = null;
		
		System.out.println("-------------------------------------");
		if(buyList.size()>10) {
			System.out.println("자산을 다 썼음");
		}else {
			
			if(coinList == null) {
				coinList = chartService.getCoinList("KRW");
			}
			int cnt= 0;
			boolean alreadyCoin = false;
			try {
				for(int i=0; i<100; i++) {
					coinNm = coinList.get(i).get("market").toString();
					
					for(int b=0; b>buyList.size();b++) {
						if(buyList.get(b).get("market").equals(coinNm))
						{
							if(sleepCnt>0) {
								sleepCnt--;
							}
							alreadyCoin = true;
						}
					}
					
					if(alreadyCoin) {
						alreadyCoin = false;
						continue;
					}
					
					jsArray = (JSONArray) ohcu.getTicker(coinNm);
					
					//System.out.println(coinNm1);
					
					standichimokuMap = chartService.getIchimokuBTMin(coinNm,5,20,60,120,60);
					realichimokuMap = chartService.getIchimokuBTMin(coinNm,5,10,30,60,10);
					ma60 = chartService.getMAMin(coinNm,5,60);
					ma120 = chartService.getMAMin(coinNm,5,120);
					ma180 = chartService.getMAMin(coinNm,5,180);
					tmpJson= (JSONObject) jsArray.get(0);
					temp_low= BigDecimal.valueOf(Double.parseDouble(realichimokuMap.get("low_prereqSpan").toString()));
					temp_high= BigDecimal.valueOf(Double.parseDouble(realichimokuMap.get("high_prereqSpan").toString()));
					stanHigh = BigDecimal.valueOf(Double.parseDouble(tmpJson.get("high_price").toString()));
					stanLow = BigDecimal.valueOf(Double.parseDouble(tmpJson.get("low_price").toString()));
					stanNow = BigDecimal.valueOf(Double.parseDouble(tmpJson.get("trade_price").toString()));
					
					//System.out.println(temp1.toPlainString());
					/*if(temp.compareTo((BigDecimal) standichimokuMap.get("high_prereqSpan"))==1) {*/
					if(temp_low.compareTo((BigDecimal) standichimokuMap.get("high_prereqSpan"))==1) {
						//if(ma60.compareTo(ma120)==1 && ma120.compareTo(ma180)==1) {
							//if(stanHigh.compareTo(temp_high) != -1 && stanHigh.compareTo(stanLow) != 1) {
								System.out.println("-------매수타점 발생 알림-------");
								System.out.println(coinNm);
								System.out.println(jsArray.toString());
								System.out.println("매수가 : " + standichimokuMap.get("high_prereqSpan").toString());
								System.out.println("-------매수타점 발생 끝-------");
								cnt++;
							//}
							
						//}
						
					}
					chartService.free();
					sleepCnt++;
					
					if(sleepCnt >= 5) {
						sleepCnt = 0;
						Thread.sleep(100);
					}
					
				
				}
			}catch(Exception e) {
				throw e;
			}
			System.out.println("총매수 타점 : " + cnt);
			System.out.println(CycCnt+"번 싸이클 끝 : " + LocalTime.now());
			CycCnt++;
		}
		System.out.println("-------------------------------------");
        //System.out.println(" scheduling job end : " + LocalTime.now()); 
	}
	
	/*
	 * 매도 스케줄
	 * 0626 수정
	 */
	@Scheduled(fixedDelay=1000)
	public void SellSearch() throws Exception{
		
		BigDecimal temp1 = null;
		String coinNm = "";
		Map <String, Object> ichimokuMap = null;
		Map <String, Object> bbMap = null;
		JSONArray jsArray = null;
		JSONObject tmpJson = null;
		HashMap tmpMap = new HashMap<String, BigDecimal>();
		int sleepCnt = 0;
		
		if(buyList.size()>0) {
			
			try {
				for(int i=0; i<=buyList.size(); i++) {
					coinNm = coinList.get(i).get("market").toString();
					jsArray = (JSONArray) ohcu.getTicker(coinNm);
					
					//System.out.println(coinNm1);
					
					ichimokuMap = chartService.getIchimokuBTMin(coinNm,1,18,52,104,26);
					bbMap = chartService.getBBMin(coinNm,1,20,2);
					tmpJson= (JSONObject) jsArray.get(0);
					temp1= BigDecimal.valueOf(Double.parseDouble(tmpJson.get("trade_price").toString()));
					//System.out.println(temp1.toPlainString());
					if(temp1.compareTo((BigDecimal) ichimokuMap.get("high_prereqSpan"))==1) {
						System.out.println("-------매도타점 발생 알림-------");
						System.out.println(coinNm);
						System.out.println(temp1.toPlainString());
						System.out.println("기준" + ichimokuMap.get("high_prereqSpan").toString());
						System.out.println("-------매도타점 발생 끝-------");
	
					}
					
					chartService.free();
					sleepCnt++;
					
					if(sleepCnt >= 5) {
						sleepCnt = 0;
						Thread.sleep(200);
					}
				
				}
			}catch(Exception e) {
				throw e;
			}
			
			
		}else {

		}
		
	}

}
