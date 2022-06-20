package master_jun.Service;

import java.io.IOException;

import org.json.JSONObject;

public interface testService {

	JSONObject testJSONReturn() throws IOException;

	JSONObject testJSONReturn2() throws IOException;

	JSONObject updateKey();

	void insertKey(JSONObject KeyKey);

}
