package master_jun.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
	
	public String getMarketCd()  throws IOException, InterruptedException {
		okhttpClientUtil = new OkHttpClientUtil();

		return okhttpClientUtil.getMarketCd();
	}
	
	public BigDecimal getIchimokuBTHighMin(String coinNm,int stand, int num1, int num2, int num3, int num4) throws IOException, InterruptedException {
		
		JSONObject temp = null;
		
		Date today = new Date();
		int std = stand * num4;//num4 기준으로 봉 개수 앞당기기때문에 분봉단위 * num4 필요
		BigDecimal changeline = null;//전환선
		BigDecimal standardline = null;//기준선
		BigDecimal prereqSpan1 = null;//선행스팬1
		BigDecimal prereqSpan2 = null;//선행스팬2
		BigDecimal postreqSpan2 = null;//후행스팬. 굳이 안만듦 ㅋ
		List<BigDecimal> hspanCarr = new ArrayList<BigDecimal>();//전환선용 고가 목록
		List<BigDecimal> lspanCarr = new ArrayList<BigDecimal>();//전환선용 저가 목록
		List<BigDecimal> hspanSarr = new ArrayList<BigDecimal>();//기준선용 고가 목록
		List<BigDecimal> lspanSarr = new ArrayList<BigDecimal>();//기준선용 저가 목록
		List<BigDecimal> hspan1arr = new ArrayList<BigDecimal>();//선행스팬1 고가 목록
		List<BigDecimal> lspan1arr = new ArrayList<BigDecimal>();//선행스팬1 저가 목록
		List<BigDecimal> hspan2arr = new ArrayList<BigDecimal>();//선행스팬1 고가 목록
		List<BigDecimal> lspan2arr = new ArrayList<BigDecimal>();//선행스팬1 저가 목록
		List<BigDecimal> hspanParr = new ArrayList<BigDecimal>();//선행스팬2 고가 목록
		List<BigDecimal> lspanParr = new ArrayList<BigDecimal>();//선행스팬2 저가 목록
		
		okhttpClientUtil = new OkHttpClientUtil();
		
		System.out.println("????");
		
		toolUtil = new ToolUtil();
		
		System.out.println("date "+ toolUtil.getCalendarCalc(today, "M", "-", num1));
		
		JSONArray hspan1Src = okhttpClientUtil.getCandleMin(Integer.toString(stand), coinNm, "200", toolUtil.getCalendarCalc(today, "M", "-", stand));
		/*
		 * JSONArray hspan2Src = okhttpClientUtil.getCandleMin(Integer.toString(stand),
		 * coinNm, Integer.toString(num2), toolUtil.getCalendarCalc(today, "M", "-",
		 * 5));
		 * 
		 * JSONArray hspan3Src = okhttpClientUtil.getCandleMin(Integer.toString(stand),
		 * coinNm, Integer.toString(num3), toolUtil.getCalendarCalc(today, "M", "-",
		 * 5));
		 */	
		int Csize = num1+num4-1; //선행스팬1 기준일 전 전환선용
		int Ssize = num2+num4-1; //선행스팬1 기준일 전 기준선용
		
		if(num1 <= 200 && num2 <= 200 && num3 <= 200 && Csize <= 200 && Ssize <= 200) {
			for(int i=0; i<num1; i++) {
				temp = (JSONObject)hspan1Src.get(i);
				hspanCarr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("high_price").toString())));
				lspanCarr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("low_price").toString())));
			}
			for(int i=0; i<num2; i++) {
				temp = (JSONObject)hspan1Src.get(i);
				hspanSarr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("high_price").toString())));
				lspanSarr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("low_price").toString())));
			}
			for(int i=num4-1; i<Csize; i++) {
				temp = (JSONObject)hspan1Src.get(i);
				hspan1arr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("high_price").toString())));
				lspan1arr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("low_price").toString())));
			}
			for(int i=num4-1; i<Ssize; i++) {
				temp = (JSONObject)hspan1Src.get(i);
				hspan2arr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("high_price").toString())));
				lspan2arr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("low_price").toString())));
			}
			for(int i=num4-1; i<num3+num4; i++) {
				temp = (JSONObject)hspan1Src.get(i);
				hspanParr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("high_price").toString())));
				lspanParr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("low_price").toString())));
			}
		}
		
		/*
		 * changeline =
		 * (hspan1arr.get(h1size-1).doubleValue()+lspan1arr.get(0).doubleValue())/2;
		 */
		try {
			changeline = toolUtil.getBigDecMax(hspanCarr).add(toolUtil.getBigDecMin(lspanCarr)).divide(BigDecimal.valueOf(2));
			standardline = toolUtil.getBigDecMax(hspanSarr).add(toolUtil.getBigDecMin(lspanSarr)).divide(BigDecimal.valueOf(2));
			prereqSpan1 = toolUtil.getBigDecMax(hspan1arr).add(toolUtil.getBigDecMin(lspan1arr)).add(toolUtil.getBigDecMax(hspan2arr).add(toolUtil.getBigDecMin(lspan2arr))).divide(BigDecimal.valueOf(4));
			prereqSpan2 = toolUtil.getBigDecMax(hspanParr).add(toolUtil.getBigDecMin(lspanParr)).divide(BigDecimal.valueOf(2));;
			
			System.out.println("changeline  "+changeline.toPlainString());
			System.out.println("standardline  "+standardline.toPlainString());
			System.out.println("prereqSpan1  "+prereqSpan1.toPlainString());
			System.out.println("prereqSpan2  "+prereqSpan2.toPlainString());
			
		}catch(NullPointerException e) {
			throw e;
		}
		
		if(prereqSpan1.compareTo(prereqSpan2)==1) {
			return prereqSpan1;
		}else {
			return prereqSpan2;
		}

	}
}
