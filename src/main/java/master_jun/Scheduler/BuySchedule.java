package master_jun.Scheduler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.http.HttpClient;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import master_jun.Inquire.InquireService;
import master_jun.Service.ChartService;
import master_jun.Service.ExchangeService;
import master_jun.Util.OkHttpClientUtil;

@Component
public class BuySchedule {

	@Autowired
	public OkHttpClientUtil ohcu;

	@Autowired
	public OkHttpClientUtil ohcuS;

	@Autowired
	public ChartService chartService;

	@Autowired
	public ExchangeService exchangeService;
	@Autowired
	public InquireService inquireService;
	@Autowired
	public ChartService chartServiceS;

	static int buyCoinCnt = 0;
	static int sellCoinCnt = 0;
	static int CycCnt = 1;
	static List<Map> coinList = null;
	static List<Map<String, String>> buyList = new ArrayList<Map<String, String>>();// 매수 목록
	static List<Map<String, String>> banList = new ArrayList<Map<String, String>>();// 매도 후 일정시간 벤 3분가량
	static BigDecimal last = BigDecimal.valueOf(1.00);
	static List<Map<String, Object>> myMarketCoin = new ArrayList<Map<String, Object>>();// 미체결 목록

	/*
	 * 코인리스트 불러오기
	 */
	@Scheduled(cron = "0 0/10 * * * *")
	public void CoinListSearch() throws Exception {
		coinList = chartService.getCoinList("KRW");

	}

	@Scheduled(fixedDelay = 1000)
	public void getcoin() throws Exception {
		
		HashMap<String, String> params = new HashMap<>();
		params.put("state", "wait");
		JSONArray jsonarray = exchangeService.getOrderlist(params);
		for (int i = 0; i < jsonarray.size(); i++) {
			JSONObject jsonobj = (JSONObject) jsonarray.get(i);
			String tempTime = jsonobj.get("created_at").toString();
			SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date coinTime = input.parse(tempTime);
			Date nowTime = new Date();
			if (((nowTime.getTime() - coinTime.getTime()) / 1000) > 600) {
				inquireService.cancelCoin(jsonobj.get("uuid").toString());
			}
		}
	}

	@Scheduled(fixedDelay = 300000)
	public void BuyListView() throws Exception {

		buyList = inquireService.getBalanceCoin(buyList);
		
		Map<String, String> buyMap = new HashMap<String, String>();
		if (buyList.size() > 0) {
			System.out.println("----보유 목록------");
			for (int b = 0; b < buyList.size(); b++) {
				buyMap = buyList.get(b);
				System.out.println(buyMap.get("market"));
			}
			System.out.println("----보유 목록 끝----");
		}
		System.out.println("----현재 보유 코인 수 : " + buyList.size() + " 개----");
		System.out.println("----현재 총 매도했던 코인 수 : " + sellCoinCnt + " 개----");

		/* 벤리스트 초기화 */
		if (banList.size() > 0) {
			banList.clear();
		}
	}

	/*
	 * 매수스케줄 0709 수정
	 */
	@Scheduled(fixedDelay = 100)
	public void BuySearch() throws Exception {

		// System.out.println(CycCnt+"번 싸이클 시작 : " + LocalTime.now());
		// System.out.println("-------------------------------------");
		if (coinList == null) {
			coinList = chartService.getCoinList("KRW");
		}

		for (int i = 0; i < 70; i++) {
			BigDecimal temp_low = null;
			BigDecimal temp_high = null;
			BigDecimal stanHigh = null;// 고가
			BigDecimal stanNow = null;// 현재가
			BigDecimal stanlow = null;// 저가
			String coinNm = "";
			String coinNmB = "";
			String type = "";
			String cause = "";
			Map<String, Object> fiveIchimokuMap = null;
			Map<String, Object> twentyichimokuMap = null;
			Map<String, Object> tenIchimokuMap_s = null;
			Map<String, Object> tenIchimokuMap = null;
			Map<String, Object> ThreeIchimokuMap = null;
			Map<String, String> buyTmpMap = null;

			int sleepCnt = 0;
			int bSize = 0;
			BigDecimal ma60 = null;
			BigDecimal ma120 = null;
			BigDecimal ma180 = null;
			int cnt = 0;
			int bCnt = 0;
			boolean alreadyCoin = false;
			boolean sellCoin = false;

			bSize = buyList.size();
			sellCoin = false;
			coinNm = coinList.get(i).get("market").toString();

			try {
				// 현재가를 가져옴
				/* 매도타점 찾기 */
				if (bSize > 0) {
					Map<String, String> buyMap = new HashMap<String, String>();
					;
					for (int b = 0; b < bSize; b++) {
						buyMap = buyList.get(b);
						if (buyMap.get("market").equals(coinNm)) {
							alreadyCoin = true;
							break;
						}
					}

					bCnt = i % bSize;

					buyTmpMap = buyList.get(bCnt);
					coinNmB = buyTmpMap.get("market");
					type = buyTmpMap.get("type");

					Map<String, Object> bb60Map = chartService.getBBMin(coinNmB, 5, 60, 0.5);

					fiveIchimokuMap = chartService.getIchimokuBTMin(coinNmB, 5, 5, 10, 20, 5);
					tenIchimokuMap = chartService.getIchimokuBTMin(coinNmB, 5, 10, 30, 60, 10);
					twentyichimokuMap = chartService.getIchimokuBTMin(coinNmB, 5, 20, 60, 120, 60);
					tenIchimokuMap_s = chartService.getIchimokuBTMin(coinNmB, 5, 10, 120, 120, 10);
					ma60 = BigDecimal.valueOf(Double.parseDouble(bb60Map.get("ma").toString()));
					ma120 = chartService.getMAMin(coinNm, 5, 120);
					BigDecimal bb60h = BigDecimal.valueOf(Double.parseDouble(bb60Map.get("highbb").toString()));
					BigDecimal bb60l = BigDecimal.valueOf(Double.parseDouble(bb60Map.get("lowbb").toString()));
					BigDecimal bb120l = (BigDecimal) chartService.getBBMin(coinNmB, 5, 120, 0.5).get("lowbb");
					BigDecimal bb120h = (BigDecimal) chartService.getBBMin(coinNmB, 5, 120, 2).get("highbb");

					stanNow = BigDecimal.valueOf(
							Double.parseDouble(chartService.getPrice(coinNmB, 5).get("high_price").toString()));
					temp_high = BigDecimal
							.valueOf(Double.parseDouble(tenIchimokuMap.get("high_prereqSpan").toString()));

					/*
					 * if (type.equals("10")) { 손절 if (stanNow.compareTo((BigDecimal)
					 * tenIchimokuMap.get("high_prereqSpan")) == -1 &&
					 * stanNow.compareTo((BigDecimal) twentyichimokuMap.get("low_prereqSpan")) ==
					 * -1) { sellCoin = true; cause = "손절"; }
					 * 
					 * if (stanNow.compareTo((BigDecimal) tenIchimokuMap.get("high_prereqSpan")) ==
					 * 1 && stanNow.compareTo((BigDecimal) fiveIchimokuMap.get("high_prereqSpan"))
					 * == -1) { sellCoin = true; cause = "10구름대 위 5구름대 아래"; }
					 * 
					 * } else if (type.equals("60l")) {
					 */
					/* 손절부터 확인 */
					/*
					 * if (stanNow.compareTo((BigDecimal) fiveIchimokuMap.get("low_prereqSpan")) ==
					 * -1) { sellCoin = true; cause="손절"; }
					 */
					if (temp_high.compareTo((BigDecimal) bb120l) == -1) {
						sellCoin = true;
						cause = "손절";
					}

					if (temp_high.compareTo((BigDecimal) tenIchimokuMap.get("high_prereqSpan")) != -1
							&& stanNow.compareTo(ma120) == 1) {
						sellCoin = true;
						cause = "120일선";
					}

					if (temp_high.compareTo((BigDecimal) tenIchimokuMap.get("high_prereqSpan")) == -1
							&& stanNow.compareTo((BigDecimal) twentyichimokuMap.get("low_prereqSpan")) == 1) {
						sellCoin = true;
						cause = "20구름대 아래";
					}
					/*
					 * } else if (type.equals("20")) { 손절부터 확인 if (stanNow.compareTo((BigDecimal)
					 * twentyichimokuMap.get("high_prereqSpan")) == -1 &&
					 * stanNow.compareTo((BigDecimal) tenIchimokuMap.get("low_prereqSpan")) == -1) {
					 * sellCoin = true; cause = "손절"; }
					 * 
					 * if (stanNow.compareTo((BigDecimal) twentyichimokuMap.get("high_prereqSpan"))
					 * == 1 && stanNow.compareTo((BigDecimal)
					 * tenIchimokuMap_s.get("high_prereqSpan")) == -1) { sellCoin = true; cause =
					 * "20구름대 위 10구름대 아래"; }
					 * 
					 * }
					 */

					/* 매도타점확인 */
					if (sellCoin) {

						inquireService.sellCoin(coinNmB);
						banList.add(buyTmpMap);
						buyList.remove(bCnt);
						System.out.println("-------매도타점 발생 끝-------" + cause);
						sellCoin = false;
						sellCoinCnt++;
					}
					chartService.free();
					Thread.sleep(51);
				}

				if (bSize > 4)
					continue;

				// System.out.println(coinNm);
				int banCnt = 0;

				if (banList.size() > 0) {
					for (int ban = 0; ban < banList.size(); ban++) {
						buyTmpMap = banList.get(ban);
						if (coinNm.equals(buyTmpMap.get("market"))) {
							alreadyCoin = true;
							break;
						}
					}
				}

				/* 매수타점 찾기 */
				if (alreadyCoin) {
					alreadyCoin = false;
					continue;
				}

				twentyichimokuMap = chartService.getIchimokuBTMin(coinNm, 5, 20, 60, 120, 60);
				tenIchimokuMap = chartService.getIchimokuBTMin(coinNm, 5, 10, 30, 60, 10);
				ThreeIchimokuMap = chartService.getIchimokuBTMin(coinNm, 5, 3, 6, 12, 3);
				tenIchimokuMap_s = chartService.getIchimokuBTMin(coinNm, 5, 5, 10, 20, 5);

				ma60 = chartService.getMAMin(coinNm, 5, 60);
				ma120 = chartService.getMAMin(coinNm, 5, 120);
				ma180 = chartService.getMAMin(coinNm, 5, 180);

				Map<String, Object> bb60Map = chartService.getBBMin(coinNmB, 5, 60, 0.5);
				BigDecimal bb60h = BigDecimal.valueOf(Double.parseDouble(bb60Map.get("highbb").toString()));

				temp_low = BigDecimal.valueOf(Double.parseDouble(tenIchimokuMap.get("low_prereqSpan").toString()));
				temp_high = BigDecimal.valueOf(Double.parseDouble(tenIchimokuMap.get("high_prereqSpan").toString()));

				stanNow = BigDecimal
						.valueOf(Double.parseDouble(chartService.getPrice(coinNm, 3).get("trade_price").toString()));
				stanHigh = BigDecimal
						.valueOf(Double.parseDouble(chartService.getPrice(coinNm, 3).get("high_price").toString()));
				stanlow = BigDecimal
						.valueOf(Double.parseDouble(chartService.getPrice(coinNm, 3).get("low_price").toString()));

				// System.out.println(temp1.toPlainString());
				/*
				 * if(temp.compareTo((BigDecimal) standichimokuMap.get("high_prereqSpan"))==1) {
				 */

				/*
				 * if (stanlow.compareTo(ma180) != -1) { 10일목
				 * 
				 * if (temp_low.compareTo((BigDecimal) twentyichimokuMap.get("high_prereqSpan"))
				 * == 1) { if (temp_low.compareTo((BigDecimal)
				 * ThreeIchimokuMap.get("high_prereqSpan")) == 1 &&
				 * temp_high.compareTo((BigDecimal) tenIchimokuMap_s.get("high_prereqSpan")) ==
				 * 1) { if (ma60.compareTo(ma120) == 1) { if (stanHigh.compareTo(temp_high) == 1
				 * && stanlow.compareTo(temp_high) == -1) {
				 * System.out.println("-------매수타점 발생 알림-------"); System.out.println(coinNm +
				 * " 10"); // System.out.println(jsArray.toString());
				 * System.out.println("매수가 : " + temp_high.toPlainString());
				 * System.out.println("현재봉 고가 : " + stanHigh.toPlainString());
				 * System.out.println("일목HIGH : " + temp_high.toPlainString()); Map<String,
				 * String> buyMap = new HashMap<String, String>(); buyMap.put("market", coinNm);
				 * buyMap.put("buyValue", temp_high.toPlainString()); buyMap.put("type", "10");
				 * buyList.add(buyMap); System.out.println("-------매수타점 발생 끝-------"); cnt++;
				 * continue; } } } }
				 * 
				 * 20일목 if (temp_high.compareTo((BigDecimal)
				 * twentyichimokuMap.get("low_prereqSpan")) == -1) { if (((BigDecimal)
				 * twentyichimokuMap.get("low_prereqSpan")) .compareTo((BigDecimal)
				 * ThreeIchimokuMap.get("high_prereqSpan")) == 1 &&
				 * temp_high.compareTo((BigDecimal) ThreeIchimokuMap.get("low_prereqSpan")) ==
				 * -1) { if (ma120.compareTo(ma60) == 1) { if (stanHigh.compareTo(((BigDecimal)
				 * twentyichimokuMap.get("high_prereqSpan"))) != -1 && stanlow.compareTo(
				 * (BigDecimal) twentyichimokuMap.get("high_prereqSpan")) != 1) {
				 * System.out.println("-------매수타점 발생 알림-------"); System.out.println(coinNm +
				 * " 20"); // System.out.println(jsArray.toString());
				 * System.out.println("매수가 : " + ((BigDecimal)
				 * twentyichimokuMap.get("high_prereqSpan")).toPlainString());
				 * System.out.println("stanHigh : " + stanHigh.toPlainString());
				 * System.out.println("일목HIGH : " + ((BigDecimal)
				 * twentyichimokuMap.get("high_prereqSpan")).toPlainString()); Map<String,
				 * String> buyMap = new HashMap<String, String>(); buyMap.put("market", coinNm);
				 * buyMap.put("buyValue", ((BigDecimal)
				 * twentyichimokuMap.get("high_prereqSpan")).toPlainString());
				 * buyMap.put("type", "20"); buyList.add(buyMap);
				 * System.out.println("-------매수타점 발생 끝-------"); cnt++; continue; }
				 * 
				 * } } } }
				 */

				BigDecimal sixtyltemp = BigDecimal
						.valueOf(Double.parseDouble(twentyichimokuMap.get("low_prereqSpan").toString()));

				/* 60로우 */
				if (temp_high.compareTo((BigDecimal) twentyichimokuMap.get("low_prereqSpan")) == -1 && sixtyltemp
						.divide(temp_high, 4, BigDecimal.ROUND_DOWN).compareTo(BigDecimal.valueOf(1.005)) != -1) {
					if (ma120.compareTo(ma180) == -1 && ma120.compareTo(bb60h) == 1
							&& temp_high.compareTo(ma60) != -1) {
						if (temp_high.compareTo(ma120) != 1) {
							if (stanHigh.compareTo(temp_high) == 1 && stanNow.compareTo(temp_high) == -1) {
								Map<String, String> buyMap = new HashMap<String, String>();
								buyMap.put("market", coinNm);
								buyMap.put("buyValue", stanNow.toPlainString());
								/* buyMap.put("type", "60l"); */
								buyList.add(buyMap);
								inquireService.buyCoin(coinNm, temp_high.toPlainString());
								System.out.println("-------매수타점 발생 끝-------");
								cnt++;
								continue;
							}
						}
					}
				}

			} catch (Exception e) {
				throw e;
			}
			chartService.free();
			if (bSize > 0)
				Thread.sleep(66);
			else
				Thread.sleep(101);
			
		}

		// System.out.println("총매수 타점 : " + cnt);
		System.out.println("--" + CycCnt + "번 매수 싸이클 끝 : " + LocalTime.now());
		CycCnt++;

		// System.out.println("-------------------------------------");
		// System.out.println(" scheduling job end : " + LocalTime.now());
	}

	/*
	 * 매도 스케줄 0626 수정
	 */
	/*
	 * @Scheduled(fixedDelay = 555) public void SellSearch() throws Exception {
	 * 
	 * BigDecimal temp1 = null; String coinNm = ""; Map<String, Object> ichimokuMap
	 * = null; Map<String, Object> bbMap = null; JSONArray jsArray = null;
	 * JSONObject tmpJson = null; HashMap tmpMap = new HashMap<String,
	 * BigDecimal>(); int sleepCnt = 0;
	 * 
	 * //if (buyList.size() > 0) {
	 * 
	 * 
	 * for (int i = 0; i < 5; i++) { try { coinNm =
	 * coinList.get(i).get("market").toString(); coinNm="KRW-BTC"; jsArray =
	 * (JSONArray) ohcuS.getTicker(coinNm);
	 * 
	 * // System.out.println(coinNm1);
	 * 
	 * ichimokuMap = chartServiceS.getIchimokuBTMin(coinNm, 1, 18, 52, 104, 26);
	 * bbMap = chartServiceS.getBBMin(coinNm, 1, 20, 2); tmpJson = (JSONObject)
	 * jsArray.get(0); temp1 =
	 * BigDecimal.valueOf(Double.parseDouble(tmpJson.get("trade_price").toString()))
	 * ; // System.out.println(temp1.toPlainString()); if
	 * (temp1.compareTo((BigDecimal) ichimokuMap.get("high_prereqSpan")) == 1) {
	 * 
	 * System.out.println("-------매도타점 발생 알림-------"); System.out.println(coinNm);
	 * System.out.println(temp1.toPlainString()); System.out.println("기준" +
	 * ichimokuMap.get("high_prereqSpan").toString());
	 * System.out.println("-------매도타점 발생 끝-------");
	 * 
	 * 
	 * }
	 * 
	 * chartServiceS.free(); sleepCnt++; Thread.sleep(50); } catch (Exception e) {
	 * throw e; } if (sleepCnt > 1) { sleepCnt = 0; Thread.sleep(500); }
	 * 
	 * } //} else {} System.out.println(CycCnt+"번 매도 싸이클 끝 : " + LocalTime.now());
	 * 
	 * 
	 * }
	 */

}
