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
public class GetKeyUtil {
	/**
	 * @author 레서드
	 * @return access_key , secret_key // chat_id , Bot_token JSON Type으로 반환함
	 * @return JSONObject
	 * @throws IOException 
	 * @date 가고 싶다
	 * @since 20220619
	 * @param no no no no
	 * 
	 */
	@RequestMapping("/getUpbitKey.do")
	public JSONObject userGetUpbitKeyInfo() throws IOException {
		JSONObject jo = new JSONObject();
		
		/* 일단 넣고 나주엥 DB에서 */
		
		BufferedReader bufferdReader = new BufferedReader(
				new FileReader("D:\\yek\\upbit.key"));
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
		JSONObject jo = new JSONObject();
		BufferedReader bufferdReader = new BufferedReader(
				new FileReader("D:\\yek\\telegram.key"));
		String str;
		ArrayList<String> al = new ArrayList<>();
		while ((str = bufferdReader.readLine()) != null) {
			al.add(str);
		}
		bufferdReader.close();
		jo.put("chat_id", al.get(0));
		jo.put("Bot_token", al.get(1));
		return jo;
	}
}
