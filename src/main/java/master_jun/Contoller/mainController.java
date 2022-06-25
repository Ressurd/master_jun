package master_jun.Contoller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import master_jun.Service.ChartService;
import master_jun.Util.OkHttpClientUtil;

@Component
@Controller
@RequestMapping(value = "main")
public class mainController {
	
	@Autowired
	ChartService chartService;

	@Autowired
	public OkHttpClientUtil ohcu;
	
	@RequestMapping(value = "")
	public String main(Model model) throws Exception {
		
		chartService = new ChartService();
		
		Map<String, Object> temp = null;
		
		ohcu.getCandleMin("5", "KRW-BTC", "1", "");
		
		/*
		 * temp = chartService.getIchimokuBTMin("KRW-BTC", 5, 9, 26, 52, 26); temp =
		 * chartService.getBBMin("KRW-BTC", 5, 20, 1.5);
		 */
		
		List<Map> coinList = chartService.getCoinList("KRW");
		
		
		return "main/main";
	}
	
	
}