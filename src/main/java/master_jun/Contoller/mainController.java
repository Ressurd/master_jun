package master_jun.Contoller;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import master_jun.Service.ChartService;

@Component
@Controller
@RequestMapping(value = "main")
public class mainController {
	@RequestMapping(value = "")
	public String main(Model model) throws IOException, InterruptedException {
		
		ChartService chartService = new ChartService();
		
		model.addAttribute("list", chartService.getIchimokuBTHighMin(18,52,104));
		
		return "main/main";
	}
}