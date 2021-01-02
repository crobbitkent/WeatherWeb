package crobbit.WeatherStudy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Locale;

@Component
public class  WeatherService
{
	protected static Log log = LogFactory.getLog(WeatherService.class);
	StringBuilder urlBuilder;
	WeatherDAO dao;
	Weather[] weatherArray;
	
	public WeatherService(WeatherDAO dao){
		this.dao = dao;
	}
	
	// 날씨가 업데이트 된다.
	// WeatherUI에서 호출된다.
	public void weatherUpdate(Weather weather) throws IOException, org.json.simple.parser.ParseException {
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
		
		jsonParse(
				apiConnection(base_date, base_time, weather, 0)
				, weather, orgTime);
		
		int intTime = (int)(Integer.parseInt(base_time) * 0.01); // 시간만 남음
		int rest =  intTime % 3;
		if(2 != intTime % 3){
			intTime = intTime - (3 - (intTime % 3));
		}
		
		base_time = "" + (intTime * 100);
		
		
		jsonParseVillage(
				apiConnection(base_date, base_time, weather, 1)
				, weather, orgTime);
	}
	
	
	
	private String apiConnection(String base_date, String base_time, Weather weather, int select) throws IOException	{
		
		switch(select){
			// 초단기
			case 0:
				urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtFcst");
				break;
			// 동네예보
			case 1:
				urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst");
				break;
		}
		
		
		// 인증키
		urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + WeatherDAO.serviceKey);
		appendUrlBuilder(urlBuilder, "&", "numOfRows", "100");
		appendUrlBuilder(urlBuilder, "&", "pageNo", "1");
		appendUrlBuilder(urlBuilder, "&", "dataType", "JSON");
		
		appendUrlBuilder(urlBuilder, "&", "base_date", base_date);
		appendUrlBuilder(urlBuilder, "&", "base_time", base_time);
		appendUrlBuilder(urlBuilder, "&", "nx", weather.getNx());
		appendUrlBuilder(urlBuilder, "&", "ny", weather.getNy());
		
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
	private void jsonParse(String jsonData, Weather weather, String orgTime) throws org.json.simple.parser.ParseException
	{
		JSONObject obj = new JSONObject();
		
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject)jsonParser.parse(jsonData);
		JSONObject parse_response = (JSONObject)jsonObject.get("response");
		JSONObject parse_body = (JSONObject)parse_response.get("body");
		JSONObject parse_items = (JSONObject)parse_body.get("items");
		JSONArray parse_item = (JSONArray)parse_items.get("item");
		
		JSONObject weatherObj;
		
		int size = parse_item.size();
		
		for(int i = 0; i < size; ++i){
			weatherObj = (JSONObject)parse_item.get(i);
			
			// 오늘 날짜
			if(true == weatherObj.get("fcstTime").equals(orgTime))
			{
				// 카테고리 별로 나눠서 정보를 받고 설정한다.
				if (true == weatherObj.get("category").equals("PTY"))
				{
					weather.setPty(WeatherDAO.PTYCode[Integer.parseInt((String) weatherObj.get("fcstValue"))]);
				}
				
				if (true == weatherObj.get("category").equals("SKY"))
				{
					weather.setSky(WeatherDAO.SKYCode[Integer.parseInt((String) weatherObj.get("fcstValue"))]);
				}
				
				if (true == weatherObj.get("category").equals("T1H"))
				{
					weather.setT1h((String) weatherObj.get("fcstValue")); // T1H 정보 추출
				}
			}
		} // for end
	}
	
	// json형태로 넘어온 기상정보를 파싱
	// 그리고 반환
	private void jsonParseVillage(String jsonData, Weather weather, String orgTime) throws org.json.simple.parser.ParseException
	{
		JSONObject obj = new JSONObject();
		
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject)jsonParser.parse(jsonData);
		JSONObject parse_response = (JSONObject)jsonObject.get("response");
		JSONObject parse_body = (JSONObject)parse_response.get("body");
		JSONObject parse_items = (JSONObject)parse_body.get("items");
		JSONArray parse_item = (JSONArray)parse_items.get("item");
		
		JSONObject weatherObj;
		
		int size = parse_item.size();
		weatherArray = new Weather[60];
		int count = 0;
		
		for (int i = 0; i < size; ++i)
		{
			weatherObj = (JSONObject) parse_item.get(i);
			
			// 카테고리가 '3시간 기온'인 경우만...
			if (true == weatherObj.get("category").equals("T3H"))
			{
				weatherArray[count] = new Weather();
				Object test1 = weatherObj.get("fcstValue");
				String test = (String)(test1);
				weatherArray[count++].setT1h(test);
			}
			
			
		} // for end
		
		int a = 0;
	}
}
