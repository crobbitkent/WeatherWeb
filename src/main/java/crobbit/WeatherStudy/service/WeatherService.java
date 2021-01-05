package crobbit.WeatherStudy.service;

import crobbit.WeatherStudy.api.APIParser;
import crobbit.WeatherStudy.api.APIType;
import crobbit.WeatherStudy.repository.Weather;
import crobbit.WeatherStudy.repository.WeatherDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class WeatherService {
    protected static Log log = LogFactory.getLog(WeatherService.class);

    WeatherDAO dao;
    Weather[] weatherArray;

    public WeatherService(WeatherDAO dao)throws IOException, org.json.simple.parser.ParseException {
        this.dao = dao;
        update();
    }

    public Weather getTodayWeather(){
        return dao.getTodayWeather();
    }

    // 날씨가 업데이트 된다.
    // WeatherUI에서 호출된다.
    public void update() throws IOException, org.json.simple.parser.ParseException {
        dao.readyTodayWeather();
    }

//
//    // 초단기 예보 API를 통해 현재 날씨를 설정
//    public void getShortForecast() throws IOException, ParseException {
//        String baseDate = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
//        String orgTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH")) + "00";
//        int intTime = (Integer.parseInt(orgTime) - 100);
//        String baseTime = (intTime < 1000) ? "0" + intTime + "" : intTime + "";
//
//        JSONArray jsonArray = APIParser.apiSetUp(APIType.ULTRA_SHORT, dao, baseDate, baseTime);
//
//        int size = jsonArray.size();
//
//        for (int i = 0; i < size; ++i) {
//            JSONObject weatherObj = (JSONObject) jsonArray.get(i);
//            Weather weather = dao.getTodayWeather();
//            // 오늘 날짜
//            if (true == weatherObj.get("fcstTime").equals(orgTime)) {
//                // 카테고리 별로 나눠서 정보를 받고 설정한다.
//                if (true == weatherObj.get("category").equals("PTY")) {
//                    weather.setPty((String) weatherObj.get("fcstValue"));
//                } else if (true == weatherObj.get("category").equals("SKY")) {
//                    weather.setSky((String) weatherObj.get("fcstValue"));
//                } else if (true == weatherObj.get("category").equals("T1H")) {
//                    weather.setT1h((String) weatherObj.get("fcstValue")); // T1H 정보 추출
//                }
//            }
//        } // for end
//    }
//
//    // 동네예보 API를 통해 3시간 간격의 온도를 설정
//    public void getHourlyForecast() throws IOException, ParseException {
//        LocalDateTime now = LocalDateTime.now();
//
//        String baseDate = now.format(DateTimeFormatter.BASIC_ISO_DATE);
//        String orgTime = now.format(DateTimeFormatter.ofPattern("HH")) + "00";
//        String baseTime = (Integer.parseInt(orgTime) - 100) + "";
//        baseTime = calcBaseTime(baseTime);
//
//        LocalDateTime base = LocalDateTime.parse(baseDate + baseTime + "00", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//
//        JSONArray jsonArray = APIParser.apiSetUp(APIType.VILLAGE, dao, baseDate, baseTime);
//
//        weatherArray = dao.getHourlyWeather();
//
//        int size = jsonArray.size();
//
//        int index = 1;
//
//        // 예보시간을 미리 작성
//        // - Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
//        String[] fcstTimeArray = new String[24];
//
//        for(int i = 0; i < 24; ++i){
//            String time = (base.plusHours(3 * (i + 1) + 1).format(DateTimeFormatter.ofPattern("yyyyMMddHH")) + "00");
//            fcstTimeArray[i] = time;
//        }
//
//        int length = fcstTimeArray.length;
//
//        for (int i = 0; i < size; ++i) {
//            JSONObject weatherObj = (JSONObject)jsonArray.get(i);
//            // 발표시간을 찾기 위해서 변수 생성
//            String fcstTime = (String) weatherObj.get("fcstDate") + (String) weatherObj.get("fcstTime");
//            for(int j = 0; j < length; ++j){
//                // 시간이 발표 시간이라면
//                if(true == (fcstTime).equals(fcstTimeArray[j])){
//                    // 카테고리가 '3시간 기온'인 경우만...
//                    if (true == weatherObj.get("category").equals("T3H")) {
//                        Object value = weatherObj.get("fcstValue");
//                        dao.setHourlyWeatherData("T1H", fcstTime, (String)value);
//                    } else if(true == weatherObj.get("category").equals("PTY")){
//                        Object value = weatherObj.get("fcstValue");
//                        dao.setHourlyWeatherData("PTY", fcstTime, (String)value);
//                    } else if (true == weatherObj.get("category").equals("SKY")) {
//                        Object value = weatherObj.get("fcstValue");
//                        dao.setHourlyWeatherData("SKY", fcstTime, (String)value);
//                    }
//                }
//            } // for j end
//        } // for i end
//    }
//
//    // 발표 시간을 구해준다.
//    private String calcBaseTime(String baseTime){
//        int intTime = (int) (Integer.parseInt(baseTime) * 0.01); // 시간만 남음
//        int rest = intTime % 3;
//        if (2 != intTime % 3) {
//            intTime = intTime - (intTime % 3) - 1;
//        }
//
//        // - Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)
//        baseTime = "" + (intTime);
//        baseTime = (baseTime.length() < 2 ? "0" + baseTime : baseTime) + "00"; // 현재 시간 - 1 기준 (1200 1100 형태)
//        return baseTime;
//    }
}
