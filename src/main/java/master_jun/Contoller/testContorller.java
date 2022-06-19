package master_jun.Contoller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import antlr.collections.List;
import master_jun.DTO.testDTO;
import master_jun.Service.testServiceImpl;
import master_jun.Util.GetKeyUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("test")
public class testContorller {
	
	/**
	 * @author ressurd
	 * @return ArrayList<String>
	 * @throws IOException 
	 * @date 가고 싶다
	 * @since 20220619
	 */
	
	@Resource
	private testServiceImpl tsi = new testServiceImpl();
	
	@GetMapping("/test1.do")
	public String getKey(Model model) throws IOException {
		model.addAttribute("key", tsi.testJSONReturn());
		model.addAttribute("kkey", tsi.testJSONReturn2());
		System.out.println(tsi.testJSONReturn().toString());
		return "main/testSite";
	}
	
	@RequestMapping(value="/test2.do", method=RequestMethod.POST, consumes="application/json;")
	@ResponseBody
	public String getNewKey(@RequestBody HashMap<String, Object> info ) throws Exception {
		String json = null;
        
    	System.out.println(info);
    	
    	return json;
	}
	
	@GetMapping("/test3.do")
	public String getKeyPage() throws Exception {
		return "main/inputKey";
	}

}
