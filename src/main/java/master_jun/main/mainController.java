package master_jun.main;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@Controller
@RequestMapping(value = "main")
public class mainController {
	@RequestMapping(value = "")
	public String main() {

		return "main/main";
	}
}