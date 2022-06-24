package master_jun.Inquire;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	public ModelAndView InquireSelectValue(HttpServletRequest request) {
		System.out.println("test");
		ModelAndView mav = new ModelAndView("Inquire/InquireBoard");
		//mav.addObject("getBTCPrice", )
		return mav;
	}
	
	/**
	 * 스케줄러
	 * @date 2022. 6. 24.
	 * @author 레서드
	 * @throws Exception 
	 */
	//@Scheduled(cron="*/1 * * * * *")
	@Scheduled(fixedDelay=1000)
	public void ScheduleJob() throws Exception {
		inquireService.job("fixedDelay");
	}
	
	//@Scheduled(cron="*/2 * * * * *")
	/*
	 * @Scheduled(fixedDelay=1000) public void ScheduleJob2() throws IOException,
	 * InterruptedException { inquireService.job("fixedDelay2"); }
	 */
}
