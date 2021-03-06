package master_jun.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import master_jun.Util.OkHttpClientUtil;
import master_jun.Util.ToolUtil;

@Service
public class ChartService {

	@Autowired
	OkHttpClientUtil okhttpClientUtil;

	@Autowired
	ToolUtil toolUtil;

	static JSONArray candle = null;

	public JSONArray getMarketCd() throws Exception {
		okhttpClientUtil = new OkHttpClientUtil();

		return okhttpClientUtil.getMarketCd();
	}
	
	public Map<String, Double> getPrice(String coinNm, int stand) throws Exception {
		Map<String, Double> result = new HashMap<String, Double>();
		
		if (candle == null) 
			candle = okhttpClientUtil.getCandleMin(Integer.toString(stand), coinNm, "200", "");
	
		
		JSONObject temp = (JSONObject) candle.get(0);
		
		if(!temp.get("market").equals(coinNm)) {
			candle = okhttpClientUtil.getCandleMin(Integer.toString(stand), coinNm, "200", "");
			temp = (JSONObject) candle.get(0);
			
		}
				
		result.put("high_price", (Double) temp.get("high_price"));
		result.put("low_price", (Double) temp.get("low_price"));
		result.put("trade_price", (Double) temp.get("trade_price"));
		
		return result;
	}

	public List<Map> getCoinList(String market) throws Exception {

		okhttpClientUtil = new OkHttpClientUtil();
		toolUtil = new ToolUtil();
		JSONArray coinList = okhttpClientUtil.getMarketCd();
		List<Map> resultList = new ArrayList<Map>();
		List<Map> tempList = new ArrayList<Map>();
		String coinStr = "";
		String coinNm = "";
		JSONObject tmp = null;
		//
		int cnt = 0;

		for (int i = 0; i < coinList.size(); i++) {
			tmp = (JSONObject) coinList.get(i);
			coinNm = tmp.get("market").toString();
			if (coinNm.matches(market + "-(.*)")) {
				if (coinNm.equals("KRW-BTT") || coinNm.equals("KRW-XEC")) {
					continue;
				}
				if (tmp.get("market_warning").equals("CAUTION")) {
					continue;
				}
				if (coinStr.equals("")) {
					coinStr = tmp.get("market").toString();
				} else {
					coinStr = coinStr + "," + tmp.get("market").toString();
				}
			}
		}

		coinList = okhttpClientUtil.getTicker(coinStr);

		for (int i = 0; i < coinList.size(); i++) {

			tmp = (JSONObject) coinList.get(i);
			HashMap tmpMap = new HashMap<String, BigDecimal>();
			tmpMap.put("market", tmp.get("market")); // ).get("acc_trade_price_24h");
			tmpMap.put("acc_trade_price_24h",
					BigDecimal.valueOf(Double.parseDouble(tmp.get("acc_trade_price_24h").toString())).setScale(0,
							RoundingMode.DOWN));
			// System.out.println(tmpMap);
			tempList.add(tmpMap);
			cnt++;
		}

		resultList = toolUtil.getBubble(tempList);

		/*
		 * for(int i=0; i< resultList.size();i++) { System.out.println("?????? ?????? : "
		 * +resultList.get(i).get("market")+ " \t"
		 * +resultList.get(i).get("acc_trade_price_24h")); }
		 */
		System.out.println("******?????? ?????? ??? : " + cnt);

		return resultList;
	}

	public Map<String, Object> getIchimokuBTMin(String coinNm, int stand, int num1, int num2, int num3, int num4)
			throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();
		JSONObject temp = null;

		Date today = new Date();
		int std = stand * num4;// num4 ???????????? ??? ?????? ????????????????????? ???????????? * num4 ??????
		BigDecimal changeline = null;// ?????????
		BigDecimal standardline = null;// ?????????
		BigDecimal prereqSpan1 = null;// ????????????1
		BigDecimal prereqSpan2 = null;// ????????????2
		BigDecimal postreqSpan2 = null;// ????????????. ?????? ????????? ???
		List<BigDecimal> hspanCarr = new ArrayList<BigDecimal>();// ???????????? ?????? ??????
		List<BigDecimal> lspanCarr = new ArrayList<BigDecimal>();// ???????????? ?????? ??????
		List<BigDecimal> hspanSarr = new ArrayList<BigDecimal>();// ???????????? ?????? ??????
		List<BigDecimal> lspanSarr = new ArrayList<BigDecimal>();// ???????????? ?????? ??????
		List<BigDecimal> hspan1arr = new ArrayList<BigDecimal>();// ????????????1 ?????? ??????
		List<BigDecimal> lspan1arr = new ArrayList<BigDecimal>();// ????????????1 ?????? ??????
		List<BigDecimal> hspan2arr = new ArrayList<BigDecimal>();// ????????????1 ?????? ??????
		List<BigDecimal> lspan2arr = new ArrayList<BigDecimal>();// ????????????1 ?????? ??????
		List<BigDecimal> hspanParr = new ArrayList<BigDecimal>();// ????????????2 ?????? ??????
		List<BigDecimal> lspanParr = new ArrayList<BigDecimal>();// ????????????2 ?????? ??????

		okhttpClientUtil = new OkHttpClientUtil();

		toolUtil = new ToolUtil();
		/*
		 * System.out.println("date "+ toolUtil.getCalendarCalc(today, "M", "-", num1));
		 */
		if (candle == null)
			candle = okhttpClientUtil.getCandleMin(Integer.toString(stand), coinNm, "200", "");
		/*
		 * JSONArray hspan2Src = okhttpClientUtil.getCandleMin(Integer.toString(stand),
		 * coinNm, Integer.toString(num2), toolUtil.getCalendarCalc(today, "M", "-",
		 * 5));
		 * 
		 * JSONArray hspan3Src = okhttpClientUtil.getCandleMin(Integer.toString(stand),
		 * coinNm, Integer.toString(num3), toolUtil.getCalendarCalc(today, "M", "-",
		 * 5));
		 */
		int Csize = num1 + num4; // ????????????1 ????????? ??? ????????????
		int Ssize = num2 + num4; // ????????????1 ????????? ??? ????????????

		if (num1 <= 200 && num2 <= 200 && num3 <= 200 && Csize <= 200 && Ssize <= 200) {
			for (int i = 1; i < num1; i++) {
				temp = (JSONObject) candle.get(i);
				hspanCarr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("high_price").toString())));
				lspanCarr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("low_price").toString())));
			}
			for (int i = 1; i < num2; i++) {
				temp = (JSONObject) candle.get(i);
				hspanSarr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("high_price").toString())));
				lspanSarr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("low_price").toString())));
			}
			for (int i = num4; i < Csize; i++) {
				temp = (JSONObject) candle.get(i);
				hspan1arr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("high_price").toString())));
				lspan1arr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("low_price").toString())));
			}
			for (int i = num4; i < Ssize; i++) {
				temp = (JSONObject) candle.get(i);
				hspan2arr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("high_price").toString())));
				lspan2arr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("low_price").toString())));
			}
			for (int i = num4; i < num3 + num4; i++) {
				temp = (JSONObject) candle.get(i);
				hspanParr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("high_price").toString())));
				lspanParr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("low_price").toString())));
			}
		}

		/*
		 * changeline =
		 * (hspan1arr.get(h1size-1).doubleValue()+lspan1arr.get(0).doubleValue())/2;
		 */
		try {
			changeline = toolUtil.getBigDecMax(hspanCarr).add(toolUtil.getBigDecMin(lspanCarr))
					.divide(BigDecimal.valueOf(2), 4, BigDecimal.ROUND_DOWN);
			standardline = toolUtil.getBigDecMax(hspanSarr).add(toolUtil.getBigDecMin(lspanSarr))
					.divide(BigDecimal.valueOf(2), 4, BigDecimal.ROUND_DOWN);
			prereqSpan1 = toolUtil.getBigDecMax(hspan1arr).add(toolUtil.getBigDecMin(lspan1arr))
					.add(toolUtil.getBigDecMax(hspan2arr).add(toolUtil.getBigDecMin(lspan2arr)))
					.divide(BigDecimal.valueOf(4), 4, BigDecimal.ROUND_DOWN);
			prereqSpan2 = toolUtil.getBigDecMax(hspanParr).add(toolUtil.getBigDecMin(lspanParr))
					.divide(BigDecimal.valueOf(2), 4, BigDecimal.ROUND_DOWN);
			;
			/*
			 * result.put("hspanCarr", toolUtil.getBigDecMax(hspanCarr));
			 * result.put("hspanSarr", toolUtil.getBigDecMax(hspanSarr));
			 * result.put("hspan1arr", toolUtil.getBigDecMax(hspan1arr));
			 * result.put("hspanParr", toolUtil.getBigDecMax(hspanParr));
			 * result.put("candle1", candle.get(1)); result.put("candle2", candle.get(2));
			 * result.put("candle3", candle.get(3));
			 */
			result.put("changeline", changeline);
			result.put("standardline", standardline);
			result.put("prereqSpan1", prereqSpan1);
			result.put("prereqSpan2", prereqSpan2);

		} catch (NullPointerException e) {
			throw e;
		}

		if (prereqSpan1.compareTo(prereqSpan2) == 1) {
			result.put("high_prereqSpan", prereqSpan1);
			result.put("low_prereqSpan", prereqSpan2);
		} else {
			result.put("high_prereqSpan", prereqSpan2);
			result.put("low_prereqSpan", prereqSpan1);
		}

		
		
		/*
		 * System.out.println("---------------------");
		 * System.out.println(candle.get(0)); System.out.println("changeline  " +
		 * changeline.toPlainString()); System.out.println("standardline  " +
		 * standardline.toPlainString()); System.out.println("prereqSpan1  " +
		 * prereqSpan1.toPlainString()); System.out.println("prereqSpan2  " +
		 * prereqSpan2.toPlainString()); System.out.println("high_prereqSpan  " +
		 * result.get("high_prereqSpan")); System.out.println("low_prereqSpan  " +
		 * result.get("low_prereqSpan"));
		 */
		 
		return result;
	}

	public Map<String, Object> getBBMin(String coinNm, int cType, int stand, double d) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		JSONObject temp = null;
		BigDecimal stdDev = null;// ???????????? ????????? ??????
		BigDecimal ma = null;// ????????? ????????? ??????
		BigDecimal highbb = null;// ????????? ????????? ??????
		BigDecimal lowbb = null;// ????????? ????????? ??????
		List<Double> arr = new ArrayList<Double>();// ???????????? ?????? ??????

		if (candle == null)
			candle = okhttpClientUtil.getCandleMin(Integer.toString(cType), coinNm, "200", "");
		for (int i = 0; i < stand; i++) {
			temp = (JSONObject) candle.get(i);
			arr.add(Double.parseDouble(temp.get("trade_price").toString()));
		}

		toolUtil = new ToolUtil();

		ma = BigDecimal.valueOf(toolUtil.getAvg(arr));
		stdDev = toolUtil.getStddev(arr, 1);
		highbb = ma.add(stdDev.divide(BigDecimal.valueOf(d), 4, BigDecimal.ROUND_DOWN));
		lowbb = ma.subtract(stdDev.divide(BigDecimal.valueOf(d), 4, BigDecimal.ROUND_DOWN));

		result.put("ma", ma);
		result.put("highbb", highbb);
		result.put("lowbb", lowbb);

		/*
		 * System.out.println("ma  "+ma.toPlainString());
		 * System.out.println("stdDev  "+stdDev.toPlainString());
		 * System.out.println("highbb  "+highbb.toPlainString());
		 * System.out.println("lowbb  "+lowbb.toPlainString());
		 */

		return result;
	}

	public BigDecimal getMAMin(String coinNm, int cType, int stand) throws Exception {

		JSONObject temp = null;
		List<Double> arr = new ArrayList<Double>();// ???????????? ?????? ??????

		if (candle == null)
			candle = okhttpClientUtil.getCandleMin(Integer.toString(stand), coinNm, "200", "");
		for (int i = 1; i < stand; i++) {
			temp = (JSONObject) candle.get(i);
			arr.add(Double.parseDouble(temp.get("trade_price").toString()));
		}

		toolUtil = new ToolUtil();

		/*
		 * System.out.println("ma  "+ma.toPlainString());
		 * System.out.println("stdDev  "+stdDev.toPlainString());
		 * System.out.println("highbb  "+highbb.toPlainString());
		 * System.out.println("lowbb  "+lowbb.toPlainString());
		 */

		return BigDecimal.valueOf(toolUtil.getAvg(arr));
	}

	public void free() {
		candle = null;
	}

}
