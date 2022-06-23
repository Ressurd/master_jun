package master_jun.Inquire;

import java.io.IOException;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import master_jun.Util.OkHttpClientUtil;


/**
 * 코인값 조회 서비스
 * @since 20220624
 * @author ressu
 * @category Inquire
 */
@Service
public class InquireService {
	@Autowired
	public InquireDAO inquireDAO;
	
	@Autowired
	public OkHttpClientUtil ohcu;
	
	
	/**
	 * @아직못한거 스케줄러 실행할건데,, 나중에 ExchangeService에서 string을 json이던 List<Map>이던 지지고볶짱
	 * @since 20220624
	 * 
	 * @param option
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void job(String option) throws IOException, InterruptedException { 
        System.out.println(option + " scheduling job start : " + LocalTime.now()); 
		//System.out.println(ohcu.getMarketCd());
		//System.out.println(ohcu.getTicker("KRW-BTC"));
        System.out.println(option + " scheduling job end : " + LocalTime.now()); 
    } 
}
