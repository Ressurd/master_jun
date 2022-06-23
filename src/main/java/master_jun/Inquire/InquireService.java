package master_jun.Inquire;

import java.io.IOException;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import master_jun.Util.OkHttpClientUtil;

@Service
public class InquireService {
	@Autowired
	public InquireDAO inquireDAO;
	
	@Autowired
	public OkHttpClientUtil ohcu;
	
	public void job(String option) throws IOException, InterruptedException { 
        System.out.println(option + " scheduling job start : " + LocalTime.now()); 
		//System.out.println(ohcu.getMarketCd());
		//System.out.println(ohcu.getTicker("KRW-BTC"));
        System.out.println(option + " scheduling job end : " + LocalTime.now()); 
    } 
}
