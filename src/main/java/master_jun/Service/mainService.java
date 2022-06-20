package master_jun.Service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import master_jun.Util.GetKeyUtil;
import master_jun.Util.HttpClientUtil;

@Service
public class mainService {

	@Resource
	private GetKeyUtil gku = new GetKeyUtil();
	
	@Resource
	private HttpClientUtil hcu = new HttpClientUtil(null);
	
	public String upbitBuy() {
		
		
		return "";
	}
	

}
