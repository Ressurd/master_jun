package master_jun.DAO;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import master_jun.Util.GetKeyUtil;

@Repository
public class testDAO {
	
	@Resource 
	private GetKeyUtil gku = new GetKeyUtil();
	
	public void insert(JSONObject KeyKey) {
		System.out.println(KeyKey);
	}
	
	public void select(JSONObject KeyKey) {
		System.out.println(KeyKey);
	}
}
