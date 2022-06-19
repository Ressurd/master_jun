package master_jun.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;
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
	 * 
	 */
	@RequestMapping("/getUpbitKey.do")
	public JSONObject userGetUpbitKeyInfo() throws IOException {
		BufferedReader bufferdReader = new BufferedReader(
				new FileReader("D:\\yek\\upbit.key"));
		JSONObject jo = new JSONObject();
		String str;
		ArrayList<String> al = new ArrayList<>();
		while ((str = bufferdReader.readLine()) != null) {
			al.add(str);
		}
		bufferdReader.close();
		
		jo.put("access_key", al.get(0));
		jo.put("secret_key", al.get(1));
		
		return jo;
	}
	
	@RequestMapping("/getTelegramKey.do")
	public JSONObject userGetTelegramKeyInfo() throws IOException {
		BufferedReader bufferdReader = new BufferedReader(
				new FileReader("D:\\yek\\telegram.key"));
		JSONObject jo = new JSONObject();
		String str;
		ArrayList<String> al = new ArrayList<>();
		while ((str = bufferdReader.readLine()) != null) {
			al.add(str);
		}
		bufferdReader.close();
		jo.put("access_key", al.get(0));
		jo.put("secret_key", al.get(1));
		return jo;
	}
}
