package master_jun.Contoller;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import master_jun.Service.ChartService;
import master_jun.Service.mainService;

@Component
@Controller
@RequestMapping(value = "main")
public class mainController {
	
	@Autowired
	private mainService ms;
	
	@RequestMapping(value = "")
	public String main(Model model) throws IOException, InterruptedException {
		
		ChartService chartService = new ChartService();
		
		model.addAttribute("list", chartService.getIchimokuBTHighMin(18,52,104));
		
		return "main/main";
	}
	@RequestMapping(value = "/MainBoard")
	public String MainBoard() {
		
		return "main/MainBoard";
	}
	
	@RequestMapping(value = "/buy", method=RequestMethod.POST, consumes="application/json;")
	public String Buy() {
		System.out.println();
		ms.upbitBuy();
		return "";
	}
	
	@RequestMapping(value = "/sell", method=RequestMethod.POST, consumes="application/json;")
	public String Sell() {
		
		
		return "";
	}
}