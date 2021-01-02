package crobbit.WeatherStudy;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Locale;

public class WeeklyWeatherService
{
	WeatherDAO dao;
	StringBuilder urlBuilder;
	
	public WeeklyWeatherService(WeatherDAO dao){
		this.dao = dao;
	}
	
	public void update() throws IOException, org.json.simple.parser.ParseException {
		Calendar cal = Calendar.getInstance(Locale.KOREA);
		
		String year = String.valueOf(cal.get(cal.YEAR));
		String month = String.valueOf(cal.get(cal.MONTH) + 1);
		String day = String.valueOf(cal.get(cal.DATE)); // 기상청 api 스프링
		String time = String.valueOf(cal.get(cal.HOUR_OF_DAY) - 1); // 해당 시간 예보 검색 을 위한 (현재 시간 - 1)
		String originTime = String.valueOf(cal.get(cal.HOUR_OF_DAY)); // 현재 시간
		
		int intTime = Integer.parseInt(time) * 100;
		
		String base_time;
		// 오전이라면
		if(intTime < 1200) {
			base_time = "0600";
		}
		else
		{
			base_time = "1800";
		}
		
		day = day.length() < 2 ? "0" + day : day;
		String base_date = year + ((month.length() < 2) ? "0" + month : month) + day; // (20180815 형식)
		
		// 중기예보 조회 서비스 (최소, 최고 온도)
		jsonParseTemperature(
				apiConnection(base_date, base_time, 0)
				);
		
		// 중기육상예보 조회(강수량, 하늘 상태)
		jsonParseSky(
				apiConnection(base_date, base_time, 1)
		);
		
		int a = 0;
		
	}
	
	
	
	private String apiConnection(String base_date, String base_time, int select) throws IOException	{
		String regId;
		String tmFc = base_date + base_time;   // 발표 시각

 
		switch(select){
			// 중기예보 조회 서비스 (최소, 최고 온도)
			case 0:
				urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa");
				regId = WeatherDAO.regId; // 예보 구역 코드 = 서울로 고정
				break;
			// 중기육상예보 조회(강수량, 하늘 상태)
			case 1:
				urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst");
				regId = "11B00000"; // 예보 구역 코드 = 중기는 서울/경기
				break;
			default:
				// 오류
				return null;
		}
		
		// 인증키
		urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + WeatherDAO.serviceKey);
		appendUrlBuilder(urlBuilder, "&", "numOfRows", "100");
		appendUrlBuilder(urlBuilder, "&", "pageNo", "1");
		appendUrlBuilder(urlBuilder, "&", "dataType", "JSON");
		
		appendUrlBuilder(urlBuilder, "&", "regId", regId);
		appendUrlBuilder(urlBuilder, "&", "tmFc", tmFc);
		
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
		
		while(null != (line = reader.readLine())){
			sb.append(line);
		}
		
		reader.close();
		conn.disconnect();
		
		return sb.toString();
	}
	
	private void appendUrlBuilder(StringBuilder builder, String mark, String a1, String a2) throws IOException {
		builder.append(mark + URLEncoder.encode(a1, "UTF-8") + "=" + URLEncoder.encode(a2, "UTF-8"));
	}
	
	
	// json형태로 넘어온 기상정보를 파싱
	// 그리고 반환
	public void jsonParseTemperature(String jsonData) throws org.json.simple.parser.ParseException
	{
		JSONObject obj = new JSONObject();
		
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject)jsonParser.parse(jsonData);
		JSONObject parse_response = (JSONObject)jsonObject.get("response");
		JSONObject parse_body = (JSONObject)parse_response.get("body");
		JSONObject parse_items = (JSONObject)parse_body.get("items");
		JSONArray parse_item = (JSONArray)parse_items.get("item");
		
		JSONObject weatherObj = (JSONObject)parse_item.get(0);
		
		Weather[] weeklyWeather = dao.getWeeklyWeather();
		int length = weeklyWeather.length;
		for(int i = 0; i < length; ++i){
			weeklyWeather[i] = new Weather();
			
			String minStr = "taMin" + (i + 3);
			String maxStr = "taMax" + (i + 3);
			
			weeklyWeather[i].setMinTemperature(Long.toString((Long)weatherObj.get(minStr)));
			weeklyWeather[i].setMaxTemperature(Long.toString((Long)weatherObj.get(maxStr)));
		}
	}
	
	// json형태로 넘어온 기상정보를 파싱
	// 그리고 반환
	public void jsonParseSky(String jsonData) throws org.json.simple.parser.ParseException
	{
		JSONObject obj = new JSONObject();
		
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject)jsonParser.parse(jsonData);
		JSONObject parse_response = (JSONObject)jsonObject.get("response");
		JSONObject parse_body = (JSONObject)parse_response.get("body");
		JSONObject parse_items = (JSONObject)parse_body.get("items");
		JSONArray parse_item = (JSONArray)parse_items.get("item");
		
		JSONObject weatherObj = (JSONObject)parse_item.get(0);
		
		Weather[] weeklyWeather = dao.getWeeklyWeather();
		int length = weeklyWeather.length;
		for(int i = 0; i < length; ++i){
			String rain = (i < 5) ? "rnSt" + (i + 3) + "Am" : "rnSt" + (i + 3);
			String sky =  (i < 5) ? "wf" + (i + 3) + "Am" : "wf" + (i + 3);
			
			Object test1 = weatherObj.get(rain);
			Object test2 = weatherObj.get(sky);
			
			
			weeklyWeather[i].setRainRate(Long.toString((Long)weatherObj.get(rain)));
			weeklyWeather[i].setWeeklySky((String)weatherObj.get(sky));
		}
		
		int a = 100;
	}
}
