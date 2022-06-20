package master_jun.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import master_jun.Util.OkHttpClientUtil;

@Service
public class ChartService {
	
	@Autowired
	OkHttpClientUtil okhttpClientUtil;
	
	public String getMarketCd()  throws IOException, InterruptedException {
		okhttpClientUtil = new OkHttpClientUtil();

		return okhttpClientUtil.getMarketCd();
	}
	
	public String[] getIchimokuBTHighMin(int num1, int num2, int num3) throws IOException, InterruptedException {
		
		JSONObject temp = null;
		
		long changeline = 0;//전환선
		long standardline = 0;//기준선
		long prereqSpan1 = 0;//선행스팬1
		long prereqSpan2 = 0;//선행스팬2
		long postreqSpan2 = 0;//후행스팬
		List<BigDecimal> hspan1arr = new ArrayList<BigDecimal>();//선행스팬1 고가 목록
		List<BigDecimal> lspan1arr = new ArrayList<BigDecimal>();//선행스팬1 저가 목록
		List<BigDecimal> hspan2arr = new ArrayList<BigDecimal>();//선행스팬2 고가 목록
		List<BigDecimal> lspan2arr = new ArrayList<BigDecimal>();//선행스팬2 저가 목록
		
		okhttpClientUtil = new OkHttpClientUtil();
		
		JSONArray hspan1Src = okhttpClientUtil.getCandleMin("3", "KRW-BTC", Integer.toString(num1));
		JSONArray hspan2Src = okhttpClientUtil.getCandleMin("3", "KRW-BTC", Integer.toString(num2));
		
		int h1size = hspan1Src.size();
		int h2size = hspan2Src.size();
		
		/* System.out.println(h1size); */
		
		for(int i=0; i<h1size; i++) {
			/* System.out.println(hspan1Src.get(i)); */
			temp = (JSONObject)hspan1Src.get(i);
			hspan1arr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("high_price").toString())));
			lspan1arr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("low_price").toString())));
		}
		
		for(int i=0; i<h2size; i++) {
			/* System.out.println(hspan1Src.get(i)); */
			temp = (JSONObject)hspan2Src.get(i);
			System.out.println(Double.parseDouble(temp.get("high_price").toString()));
			System.out.println(Double.parseDouble(temp.get("low_price").toString()));
			hspan2arr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("high_price").toString())));
			lspan2arr.add(BigDecimal.valueOf(Double.parseDouble(temp.get("low_price").toString())));
		}
			
		Collections.sort(hspan1arr);
		Collections.sort(lspan1arr);
		Collections.sort(hspan2arr);
		Collections.sort(lspan2arr);
		
		System.out.println(h1size + " "+ hspan1arr.size());
		System.out.println(lspan1arr.size());
		System.out.println(hspan2arr.size());
		System.out.println(lspan2arr.size());
		
		System.out.println(hspan1arr.get(h1size-1).longValue());
		System.out.println(lspan1arr.get(h1size-1).longValue());
		System.out.println(hspan2arr.get(h2size-1).longValue());
		System.out.println(lspan2arr.get(h2size-1).longValue());
		
		changeline = (hspan1arr.get(h1size-1).longValue()+lspan1arr.get(h1size-1).longValue())/2;
		standardline = (hspan2arr.get(h1size-1).longValue()+lspan2arr.get(h1size-1).longValue())/2;
		/* prerequSpan1 = */
		
		Calendar cal = Calendar.getInstance ( );//오늘 날짜를 기준으루..
		cal.add ( cal.MONTH, -2 ); //2개월 전....
		System.out.println ( cal.get ( cal.YEAR ) );
		System.out.println ( cal.get ( cal.MONTH ) + 1 );
		System.out.println ( cal.get ( cal.DATE ) );
		
		Date today= new Date();
		System.out.println(today);
		SimpleDateFormat upbitdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		SimpleDateFormat date = new SimpleDateFormat("yyyy:MM:dd");
		SimpleDateFormat time = new SimpleDateFormat("24HH:mm:ss");
		System.out.println("upbitdate: "+upbitdate.format(today));
		System.out.println("Date: "+date.format(today));
		System.out.println("Time: "+time.format(today));
		
	
		
		
		System.out.println("");
		
		
		return null;
	}
}
