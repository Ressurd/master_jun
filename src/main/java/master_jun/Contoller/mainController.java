package master_jun.Contoller;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	@ResponseBody
	public String Buy(@RequestBody HashMap<String, String> so) throws IOException, NoSuchAlgorithmException, UnsupportedEncodingException {
		JSONObject jsonTest = new JSONObject(so);
		
		String ss = (String) jsonTest.get("money");
		System.out.println(ss);
		ms.upbitBuy(ss);
		return ss;
	}
	
	@RequestMapping(value = "/sell", method=RequestMethod.POST, consumes="application/json;")
	public String Sell() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		ms.upbitSell();
		return "";
	}
}