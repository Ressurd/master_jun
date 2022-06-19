package master_jun.Service;

import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
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
}
