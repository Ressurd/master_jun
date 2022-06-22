package master_jun.Util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
		String n="M";
		
		SimpleDateFormat upbitdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		
		System.out.println("standarddate: " + date + typeCd + code + num + typeCd.equals("M")+code.equals("-"));
		
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
		
		System.out.println("standarddate: " + result);
		
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
	
	
}
