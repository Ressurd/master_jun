package master_jun.Util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ToolUtil {
	
	
	/*
	 * 날짜계산용 
	 * typeCd: M은 분단위, D는 일단위 
	 * code: -는 뺼셈, +는 덧셈
	 */
	public String getCalendarCalc(Date date, String typeCd ,String code, int num ) {
		
		String result = "";
		String n = "M";
		
		SimpleDateFormat upbitdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		
		if(typeCd.equals("M") && code.equals("-")) {
			cal.add(Calendar.MINUTE, -num);
		}else if(typeCd.equals("M") && code.equals("+")) {
			cal.add(Calendar.MINUTE, num);
		}else if(typeCd.equals("D") && code.equals("-")) {
			cal.add(Calendar.DATE, -num);
		}else if(typeCd.equals("D") && code.equals("+")) {
			cal.add(Calendar.DATE, num);
		}else {
			return null;
		}
		
		cal.add(Calendar.HOUR, -9);//한국 시간으로 볼려면 -9시간 해야됨
		
		result = upbitdate.format(cal.getTime()); 
		result = result.replaceAll(":", "%3A").replaceAll(" ", "%20");
		
		/* System.out.println("standarddate: " + result); */
		
		return result;
	}
	
	
	/*
	 * 가장 큰 값 찾기 
	 * */
	public BigDecimal getBigDecMax(List<BigDecimal> list){
		
		BigDecimal temp = list.get(0);
		
		for (BigDecimal j : list) {
            if (j.compareTo(temp) == 1)
            	temp = j;
        }
		
		return temp;
	}
	
	/*
	 * 가장 작은 값 찾기
	 * */
	public BigDecimal getBigDecMin(List<BigDecimal> list){
		
		BigDecimal temp = list.get(0);
		
		for (BigDecimal j : list) {
            if (j.compareTo(temp) == -1)
            	temp = j;
        }
		
		return temp;
	}
	
	/*
	 *  산술 평균 구하기 
	 *  */
	public static double getAvg(List<Double> arr) {  
	    double sum = 0.0;

	    for (int i = 0; i < arr.size(); i++) {
	    	sum += arr.get(i);
	    }
	    

	    String formattedResult = String.format("%.2f", sum / arr.size());

	    return Double.parseDouble(formattedResult);
	}
	
	
	/*
	 * 표준편차 구하기
	 */
	public BigDecimal getStddev(List<Double> arr, int option) {
		
	    if (arr.size() < 2) {
	    	return BigDecimal.valueOf(arr.get(0));
	    }
	    double sum = 0.0;
	    double sd = 0.0;
	    double diff;
	    double avgValue = getAvg(arr);

	    for (int i = 0; i < arr.size(); i++) {
	      diff = arr.get(i) - avgValue;
	      sum += diff * diff;
	    }
	    
	    String formattedResult = String.format("%.2f", Math.sqrt(sum / (arr.size() - option)));
	    sd= Double.parseDouble(formattedResult);

	    return BigDecimal.valueOf(sd);

	}
	
	/*
	 * 정렬
	 * */
	public List<Map> getBubble(List<Map> list){
		
		Map jtempMap = null;
		Map itempMap = null;
		BigDecimal itemp = null;
		BigDecimal jtemp = null;
		
		for (int i=1; i<list.size(); i++) {
			for(int j=0; j<list.size()-i; j++) {
				itempMap = list.get(j+1);
				itemp = (BigDecimal) itempMap.get("acc_trade_price_24h");
				jtempMap = list.get(j);
				jtemp = (BigDecimal) jtempMap.get("acc_trade_price_24h");
				if (jtemp.compareTo(itemp) == -1) {
					list.set(j+1, jtempMap);
					list.set(j, itempMap);
				}
	            	
			}
        }
		return list;
	}
	
}
