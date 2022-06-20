package master_jun.Contoller;

import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import master_jun.Service.mainService;
import master_jun.Util.GetKeyUtil;
import master_jun.Util.HttpClientUtil;

@Component
@Controller
@RequestMapping(value = "main")
public class mainController {
	@Resource
	private mainService ms = new mainService();
	
	@RequestMapping(value = "")
	public String main() {

		return "main/main";
	}
	@RequestMapping(value = "/MainBoard")
	public String MainBoard() {

		return "main/MainBoard";
	}
	
	@RequestMapping(value = "/buy", method=RequestMethod.POST, consumes="application/json;")
	@ResponseBody
	public String Buy(@RequestBody HashMap<String, Object> test ) {
		
		ms.upbitBuy();
		return "";
	}
	
	@RequestMapping(value = "/sell", method=RequestMethod.POST, consumes="application/json;")
	public String Sell() {
		
		
		return "";
	}
}