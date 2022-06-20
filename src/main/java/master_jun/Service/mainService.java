package master_jun.Service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import master_jun.Util.GetKeyUtil;
import master_jun.Util.HttpClientUtil;

@Service
public class mainService {

	@Autowired
	private GetKeyUtil gku;
	
	@Autowired
	private HttpClientUtil hcu;
	
	public String upbitBuy() {
		
		
		return "";
	}
	

}
