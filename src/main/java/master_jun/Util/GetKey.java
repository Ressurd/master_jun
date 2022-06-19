package master_jun.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@RequestMapping(value = "getKey")
public class GetKey {
	/**
	 * @author ressurd
	 * @return ArrayList<String>
	 * @throws IOException 
	 * @date 가고 싶다
	 * @since 20220619
	 */
	@RequestMapping("/getUpbitKey.do")
	public ArrayList<String> userGetUpbitKeyInfo() throws IOException {
		BufferedReader bufferdReader = new BufferedReader(
				new FileReader("D:\\yek\\upbit.key"));
				
		String str;
		ArrayList<String> al = new ArrayList<>();
		while ((str = bufferdReader.readLine()) != null) {
			al.add(str);
		}
		bufferdReader.close();
		
		return al;
	}
	
	@RequestMapping("/getTelegramKey.do")
	public ArrayList<String> userGetTelegramKeyInfo() throws IOException {
		BufferedReader bufferdReader = new BufferedReader(
				new FileReader("D:\\yek\\telegram.key"));
				
		String str;
		ArrayList<String> al = new ArrayList<>();
		while ((str = bufferdReader.readLine()) != null) {
			al.add(str);
		}
		bufferdReader.close();
		
		return al;
	}
}
