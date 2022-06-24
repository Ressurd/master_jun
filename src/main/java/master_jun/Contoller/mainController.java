package master_jun.Contoller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import master_jun.Service.ChartService;

@Component
@Controller
@RequestMapping(value = "main")
public class mainController {
	
	@Autowired
	ChartService chartService;
	
	@RequestMapping(value = "")
	public String main(Model model) throws IOException, InterruptedException {
		
		chartService = new ChartService();
		
		Map<String, Object> temp = null;
		
		temp = chartService.getIchimokuBTMin("KRW-BTC", 5, 9, 26, 52, 26);
		temp = chartService.getBBMin("KRW-BTC", 5, 20, 1.5);
		
		return "main/main";
	}
	
	
}