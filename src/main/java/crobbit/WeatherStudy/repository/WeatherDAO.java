package crobbit.WeatherStudy.repository;

import crobbit.WeatherStudy.api.APIParser;
import crobbit.WeatherStudy.api.APIType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// DATA ACCESS OBJECT

@Component
public class WeatherDAO
{

	
	private Weather[] weeklyWeather; // 8
	private Weather todayWeather;
	private Weather[] hourlyWeather; // 24
	private int hourIndex;
	
	//==================================CONSTRUCTOR=====================================//
	public WeatherDAO(){
		todayWeather = new Weather();
		weeklyWeather = new Weather[8];
		hourlyWeather = new Weather[24];
		hourIndex = 0;
	}



	
	//==================================METHOD==================================//
	public void readyTodayWeather() throws IOException, ParseException {
		getShortForecast();
		getHourlyForecast();
		getMidTa();
		getMidLandFcst();
	}



	// 초단기 예보 API를 통해 현재 날씨를 설정
	private void getShortForecast() throws IOException, ParseException {
		String baseDate = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		String orgTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH")) + "00";
		int intTime = (Integer.parseInt(orgTime) - 100);
		String baseTime = (intTime < 1000) ? "0" + intTime + "" : intTime + "";

		JSONArray jsonArray = APIParser.apiSetUp(APIType.ULTRA_SHORT, this, baseDate, baseTime);

		int size = jsonArray.size();

		for (int i = 0; i < size; ++i) {
			JSONObject weatherObj = (JSONObject) jsonArray.get(i);
			Weather weather = todayWeather;
			// 오늘 날짜
			if (true == weatherObj.get("fcstTime").equals(orgTime)) {
				// 카테고리 별로 나눠서 정보를 받고 설정한다.
				// 강수 상태
				if (true == weatherObj.get("category").equals("PTY")) {
					weather.setPty((String) weatherObj.get("fcstValue"));
				} else if (true == weatherObj.get("category").equals("SKY")) {
					weather.setSky((String) weatherObj.get("fcstValue"));
				} else if (true == weatherObj.get("category").equals("T1H")) {
					weather.setT1h((String) weatherObj.get("fcstValue")); // T1H 정보 추출
				}
			}
		} // for end
	}

	// 동네예보 API를 통해 3시간 간격의 온도를 설정
	private void getHourlyForecast() throws IOException, ParseException {
		LocalDateTime now = LocalDateTime.now();

		String baseDate = now.format(DateTimeFormatter.BASIC_ISO_DATE);
		String orgTime = now.format(DateTimeFormatter.ofPattern("HH")) + "00";
		String baseTime = (Integer.parseInt(orgTime) - 100) + "";
		baseTime = calcBaseTime(baseTime);

		LocalDateTime base = LocalDateTime.parse(baseDate + baseTime + "00", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

		JSONArray jsonArray = APIParser.apiSetUp(APIType.VILLAGE, this, baseDate, baseTime);

		int size = jsonArray.size();

		int index = 1;

		// 예보시간을 미리 작성
		// - Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
		String[] fcstTimeArray = new String[24];

		for(int i = 0; i < 24; ++i){
			String time = (base.plusHours(3 * (i + 1) + 1).format(DateTimeFormatter.ofPattern("yyyyMMddHH")) + "00");
			fcstTimeArray[i] = time;
		}

		int length = fcstTimeArray.length;

		for (int i = 0; i < size; ++i) {
			JSONObject weatherObj = (JSONObject)jsonArray.get(i);
			// 발표시간을 찾기 위해서 변수 생성
			String fcstTime = (String) weatherObj.get("fcstDate") + (String) weatherObj.get("fcstTime");
			for(int j = 0; j < length; ++j){
				// 시간이 발표 시간이라면
				if(true == (fcstTime).equals(fcstTimeArray[j])){
					// 카테고리가 '3시간 기온'인 경우만...
					if (true == weatherObj.get("category").equals("T3H")) {
						Object value = weatherObj.get("fcstValue");
						setHourlyWeatherData("T1H", fcstTime, (String)value);
					} else if(true == weatherObj.get("category").equals("PTY")){
						Object value = weatherObj.get("fcstValue");
						setHourlyWeatherData("PTY", fcstTime, (String)value);
					} else if (true == weatherObj.get("category").equals("SKY")) {
						Object value = weatherObj.get("fcstValue");
						setHourlyWeatherData("SKY", fcstTime, (String)value);
					}
				}
			} // for j end
		} // for i end
	}

	// 발표 시간을 구해준다.
	private String calcBaseTime(String baseTime){
		int intTime = (int) (Integer.parseInt(baseTime) * 0.01); // 시간만 남음
		int rest = intTime % 3;
		if (2 != intTime % 3) {
			intTime = intTime - (intTime % 3) - 1;
		}

		// - Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
		baseTime = "" + (intTime);
		baseTime = (baseTime.length() < 2 ? "0" + baseTime : baseTime) + "00"; // 현재 시간 - 1 기준 (1200 1100 형태)
		return baseTime;
	}








	// 카테고리에 관련한 정보를 시간에 맞게 넣는 함수
	// 그 카테고리에 맞는 예보시간이 포함된 날씨를 우선 검색.
	// 없으면 새로 생성 후 정보 삽입
	public void setHourlyWeatherData(String category, String fcstTime, String fcstValue){
		// 같은 시간이 있다면
		int index = checkSameFcstTime(fcstTime);


		if(-1 != index){
			// 예보시간 저장
			hourlyWeather[index].setFcstTime(fcstTime);
			// 카테고리에 따른 정보 저장
			switch(category){
				case "T1H":
					hourlyWeather[index].setT1h(fcstValue);
					break;
				case "PTY":
					hourlyWeather[index].setPty(fcstValue);
					break;
				case "SKY":
					hourlyWeather[index].setSky(fcstValue);
					break;
			}
		} else{
			// 없으면 새로 만들어서 삽입
			Weather temp = new Weather();
			// 예보시간 저장
			temp.setFcstTime(fcstTime);
			switch(category){
				case "T1H":
					temp.setT1h(fcstValue);
					break;
				case "PTY":
					temp.setPty(fcstValue);
					break;
				case "SKY":
					temp.setSky(fcstValue);
					break;
			}

			hourlyWeather[hourIndex++] = temp;
		}

	}

	// 같은 예보 시간의 날씨가 있다면 그 인덱스를 반환, 없으면 -1
	private int checkSameFcstTime(String fcstTime){
		int length = hourIndex;
		for(int i = 0; i < length; ++i){
			// 같은 시간이 있는지 확인
			if(null != hourlyWeather[i] && true == fcstTime.equals(hourlyWeather[i].getFcstTime())){
				return i;
			}
		}

		return -1;
	}

	//==================================WEEKLY WEATHER=================================//
	// 중기 기온 예보를 통해 3일후 ~ 10일후의 최저 최고 온도를 가져온다.
	private void getMidTa() throws IOException, ParseException {
		String baseDate = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		String orgTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH")) + "00";
		int intTime = (Integer.parseInt(orgTime) - 100);
		String baseTime;

		// 오전이라면
		if (intTime < 1200) {
			baseTime = "0600";
		} else {
			baseTime = "1800";
		}
		//====================== 여기까지 날짜세팅

		JSONArray jsonArray = APIParser.apiSetUp(APIType.MID, this, baseDate, baseTime);
		JSONObject weatherObj = (JSONObject) jsonArray.get(0);

		int length = weeklyWeather.length;
		for (int i = 0; i < length; ++i) {
			weeklyWeather[i] = new Weather();

			String minStr = "taMin" + (i + 3);
			String maxStr = "taMax" + (i + 3);

			weeklyWeather[i].setMinTemperature(Long.toString((Long) weatherObj.get(minStr)));
			weeklyWeather[i].setMaxTemperature(Long.toString((Long) weatherObj.get(maxStr)));
		}
	}

	// 중기 육상 예보를 통해 3일후 ~ 10일후의 하늘상태와 강수확률을 가져온다.
	private void getMidLandFcst() throws IOException, ParseException {
		String baseDate = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		String orgTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH")) + "00";
		int intTime = (Integer.parseInt(orgTime) - 100);
		String baseTime;

		// 오전이라면
		if (intTime < 1200) {
			baseTime = "0600";
		} else {
			baseTime = "1800";
		}
		//====================== 여기까지 날짜세팅

		JSONArray jsonArray = APIParser.apiSetUp(APIType.MIDLAND, this, baseDate, baseTime);
		JSONObject weatherObj = (JSONObject) jsonArray.get(0);

		int length = weeklyWeather.length;
		for (int i = 0; i < length; ++i) {
			String rain = (i < 5) ? "rnSt" + (i + 3) + "Am" : "rnSt" + (i + 3);
			String sky = (i < 5) ? "wf" + (i + 3) + "Am" : "wf" + (i + 3);

			weeklyWeather[i].setRainRate(Long.toString((Long) weatherObj.get(rain)));
			weeklyWeather[i].setWeeklySky((String) weatherObj.get(sky));
		}
	}




	//==================================GETTER SETTER=====================================//
	public Weather[] getWeeklyWeather()
	{
		return weeklyWeather;
	}

	public Weather[] getHourlyWeather()
	{
		return hourlyWeather;
	}

	public void setWeeklyWeather(Weather[] weeklyWeather)
	{
		this.weeklyWeather = weeklyWeather;
	}

	public Weather getTodayWeather()
	{
		return todayWeather;
	}

	public void setTodayWeather(Weather todayWeather)
	{
		this.todayWeather = todayWeather;
	}

	
}
