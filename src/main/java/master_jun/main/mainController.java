package master_jun.main;

import java.io.IOException;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import master_jun.Util.GetKeyUtil;

@Component
@Controller
@RequestMapping(value = "main")
public class mainController {
	
	@Resource
	private GetKeyUtil gku = new GetKeyUtil();
	
	@RequestMapping(value = "")
	public String main() {

		return "main/main";
	}
	
	@RequestMapping(value ="/getUpbitKey.do")
	public JSONObject getUpbitKey() throws IOException {
		return gku.userGetUpbitKeyInfo();
	}
	
	@RequestMapping(value = "/getTelegramKey.do")
	public JSONObject getTelegramKey() throws IOException {
		return gku.userGetTelegramKeyInfo();
	}
}