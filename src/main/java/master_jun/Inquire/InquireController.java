package master_jun.Inquire;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 코인값 조회 컨트롤러
 * @date 2022. 6. 24.
 * @author 레서드
 *
 */
@Controller
@RequestMapping("Inquire")
public class InquireController {
	
	@Autowired
	public InquireService inquireService;
	
	/**
	 * 임시 뷰페이지
	 * @date 2022. 6. 24.
	 * @author 레서드
	 * @param request
	 */
	@RequestMapping(value="/InquireViewPage")
	public void InquireViewPage(HttpServletRequest request) {
		
	}
	/**
	 * 조회화면으로 쓸것.
	 * @date 2022. 6. 24.
	 * @author 레서드
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/InquireSelectValue", method=RequestMethod.GET)
	public ModelAndView InquireSelectValue() {
		ModelAndView mav = new ModelAndView("Inquire/InquireBoard");
		
		return mav;
	}
	
	public void buyCoin (){
		
	}
	
	@PostMapping(value="/buyCoinAjax")
	@ResponseBody
	public void buyCoin(Map<String, Object> map) throws Exception { 
		
    }
	
	@PostMapping(value="/sellCoinAjax")
	@ResponseBody
	public void sellCoin(String option) throws Exception{
		
	}
}
