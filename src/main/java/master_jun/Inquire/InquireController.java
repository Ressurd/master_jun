package master_jun.Inquire;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 코인값 조회 컨트롤러
 * 주로 스케쥴러 돌려서 만들거긴함~
 * 
 * 나중에 웹소켓이던 뭐던..
 * @since 20220624
 * @author ressu
 * @category Inquire
 *
 */
@Controller
@RequestMapping("Inquire")
public class InquireController {
	
	@Autowired
	public InquireService inquireService;
	
	/**
	 * 나중에 뷰페이지
	 * @param request
	 */
	@RequestMapping(value="/InquireViewPage")
	public void InquireViewPage(HttpServletRequest request) {
		
	}
	/**
	 * 화면조회
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
	 * @throws IOException
	 * @throws InterruptedException
	 */
	//@Scheduled(cron="*/1 * * * * *")
	@Scheduled(fixedDelay=1000)
	public void ScheduleJob() throws IOException, InterruptedException {
		inquireService.job("fixedDelay");
	}
	//@Scheduled(cron="*/2 * * * * *")
	@Scheduled(fixedDelay=1000)
	public void ScheduleJob2() throws IOException, InterruptedException {
		inquireService.job("fixedDelay2");
	}
}
