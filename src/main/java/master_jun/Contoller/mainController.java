package master_jun.Contoller;

import java.io.IOException;

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
		
		model.addAttribute("list", chartService.getIchimokuBTHighMin("KRW-BTC", 5, 9,26,52,26));
		
		return "main/main";
	}
}