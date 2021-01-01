package crobbit.WeatherStudy;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Locale;

@Component
@Deprecated
public class  WeatherService
{
	protected static Log log = LogFactory.getLog(WeatherService.class);
	
	// 강수형태(PTY) 코드
	private static String[] PTYCode = new String[] {"없음", "비", "비/눈", "눈", "소나기", "빗방울", "빗방울/눈날림", "눈날림"};
	
	// 하늘상태(SKY) 코드
	private static String[] SKYCode = new String[] {"", "맑음", "", "구름많음", "흐림"};
	
	public Location weatherUpdate(Location location) throws IOException, org.json.simple.parser.ParseException {
		Calendar cal = Calendar.getInstance(Locale.KOREA);
		
		String year = String.valueOf(cal.get(cal.YEAR));
		String month = String.valueOf(cal.get(cal.MONTH) + 1);
		String day = String.valueOf(cal.get(cal.DATE)); // 기상청 api 스프링
		String time = String.valueOf(cal.get(cal.HOUR_OF_DAY) - 1); // 해당 시간 예보 검색 을 위한 (현재 시간 - 1)
		String originTime = String.valueOf(cal.get(cal.HOUR_OF_DAY)); // 현재 시간
		
		day = day.length() < 2 ? "0" + day : day;
		String base_date = year + ((month.length() < 2) ? "0" + month : month) + day; // (20180815 형식)
		String base_time = (time.length() < 2 ? "0" + time : time) + "00"; // 현재 시간 - 1 기준 (1200 1100 형태)
		String orgTime = (originTime.length() < 2 ? "0" + originTime : originTime) + "00"; // 현재 시간 기준
		
		return jsonParse(
				apiConnection(base_date, base_time, location.getNx(), location.getNy())
				, location.getName(), orgTime);
		
		
	}
	
	
	
	private String apiConnection(String base_date, String base_time, String nx, String ny) throws IOException	{
		StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtFcst");
		// 인증키
		urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + "ca0nV8nj7B%2FvPU35vfkKH2KPcEXrq42CjvUiRYBuwcFGoIKKY44h4LGLSJYiSTVeQ2KoAgKu9gz6op2SUHRaEA%3D%3D");
		appendUrlBuilder(urlBuilder, "&", "numOfRows", "100");
		appendUrlBuilder(urlBuilder, "&", "pageNo", "1");
		appendUrlBuilder(urlBuilder, "&", "dataType", "JSON");
		
		appendUrlBuilder(urlBuilder, "&", "base_date", base_date);
		appendUrlBuilder(urlBuilder, "&", "base_time", base_time);
		appendUrlBuilder(urlBuilder, "&", "nx", nx);
		appendUrlBuilder(urlBuilder, "&", "ny", ny);
		
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
		
		BufferedReader reader;
		
		if(200 <= conn.getResponseCode() && 300 >= conn.getResponseCode()){
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		
		StringBuilder sb = new StringBuilder();
		
		String line = "";
		int debugCount = 0;
		while(null != (line = reader.readLine())){
			sb.append(line);
			++debugCount;
		}
		
		System.out.println(debugCount);
		reader.close();
		conn.disconnect();
		
		return sb.toString();
	}

	private void appendUrlBuilder(StringBuilder builder, String mark, String a1, String a2) throws IOException {
		builder.append(mark + URLEncoder.encode(a1, "UTF-8") + "=" + URLEncoder.encode(a2, "UTF-8"));
	}
	
	// json형태로 넘어온 기상정보를 파싱
	// 그리고 반환
	public Location jsonParse(String jsonData, String name, String orgTime) throws org.json.simple.parser.ParseException
	{
		Location location = new Location();
		
/*		JsonParser parser = new JsonParser();
		JsonObject obj = (JsonObject)parser.parse(jsonData);
		JsonObject response = (JsonObject)obj.get("response");
		JsonObject body = (JsonObject)response.get("body");
		JsonObject items = (JsonObject)body.get("items");
		JsonObject item = (JsonObject)items.get("item");
		*/
		
		JSONObject obj = new JSONObject();
		
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject)jsonParser.parse(jsonData);
		JSONObject parse_response = (JSONObject)jsonObject.get("response");
		JSONObject parse_body = (JSONObject)parse_response.get("body");
		JSONObject parse_items = (JSONObject)parse_body.get("items");
		JSONArray parse_item = (JSONArray)parse_items.get("item");
		
		JSONObject weather;
		
		int size = parse_item.size();
		
		for(int i = 0; i < size; ++i){
			weather = (JSONObject)parse_item.get(i);
			
			if(true == weather.get("fcstTime").equals(orgTime))
			{
				if (true == weather.get("category").equals("PTY"))
				{
					location.setPty(PTYCode[Integer.parseInt((String) weather.get("fcstValue"))]);
				}
				
				if (true == weather.get("category").equals("SKY"))
				{
					location.setSky(SKYCode[Integer.parseInt((String) weather.get("fcstValue"))]);
				}
				
				if (true == weather.get("category").equals("T1H"))
				{
					location.setT1h((String) weather.get("fcstValue")); // T1H 정보 추출
				}
			}
		} // for end
		
		location.setName(name);
		
		return location;
	}
}
