package master_jun.Service;

import java.io.IOException;
import java.util.Iterator;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import master_jun.DAO.testDAO;
import master_jun.Util.GetKeyUtil;

@Service
public class testServiceImpl implements testService{
	
	@Resource
	private GetKeyUtil gku = new GetKeyUtil();
	
	@Resource
	private testDAO tDAO = new testDAO();
	
	@Override
	public void insertKey(JSONObject KeyKey) {
		tDAO.insert(KeyKey);
	}
	
	@Override
	public JSONObject testJSONReturn() throws IOException{
		JSONObject json = new JSONObject();
		json = gku.userGetUpbitKeyInfo();
		
		Iterator<String> keys = json.keys();
		
		// 혹시 몰라서 foreach 만들어봄 깔까
		/*
		 * json.keySet().forEach(keyStr ->{ Object keyvalue = json.get(keyStr); });
		 */
		
		return json;
	}
	
	@Override
	public JSONObject testJSONReturn2() throws IOException{
		JSONObject json = new JSONObject();
		json = gku.userGetTelegramKeyInfo();
		
		Iterator<String> keys = json.keys();
		
		// 혹시 몰라서 foreach 만들어봄 깔까
		/*
		 * json.keySet().forEach(keyStr ->{ Object keyvalue = json.get(keyStr); });
		 */
		
		return json;
	}

	@Override
	public JSONObject updateKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
