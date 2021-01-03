package crobbit.WeatherStudy;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeeklyWeatherService {
    WeatherDAO dao;
    StringBuilder urlBuilder;

    public WeeklyWeatherService(WeatherDAO dao) {
        this.dao = dao;
    }

    public void update() throws IOException, org.json.simple.parser.ParseException {
        getMidTa(); // 중기 기온 예보를 통해 3일후 ~ 10일후의 하늘상태와 강수확률을 가져온다.
        getMidLandFcst(); // 중기 육상 예보를 통해 3일후 ~ 10일후의 하늘상태와 강수확률을 가져온다.
    }

    private void appendUrlBuilder(StringBuilder builder, String mark, String a1, String a2) throws IOException {
        builder.append(mark + URLEncoder.encode(a1, "UTF-8") + "=" + URLEncoder.encode(a2, "UTF-8"));
    }

    // 중기 기온 예보를 통해 3일후 ~ 10일후의 최저 최고 온도를 가져온다.
    public void getMidTa() throws IOException, ParseException {
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

        JSONArray jsonArray = APIParser.apiSetUp(APIType.MID, dao, baseDate, baseTime);
        JSONObject weatherObj = (JSONObject) jsonArray.get(0);

        Weather[] weeklyWeather = dao.getWeeklyWeather();
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
    public void getMidLandFcst() throws IOException, ParseException {
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

        JSONArray jsonArray = APIParser.apiSetUp(APIType.MIDLAND, dao, baseDate, baseTime);
        JSONObject weatherObj = (JSONObject) jsonArray.get(0);

        Weather[] weeklyWeather = dao.getWeeklyWeather();
        int length = weeklyWeather.length;
        for (int i = 0; i < length; ++i) {
            String rain = (i < 5) ? "rnSt" + (i + 3) + "Am" : "rnSt" + (i + 3);
            String sky = (i < 5) ? "wf" + (i + 3) + "Am" : "wf" + (i + 3);

            weeklyWeather[i].setRainRate(Long.toString((Long) weatherObj.get(rain)));
            weeklyWeather[i].setWeeklySky((String) weatherObj.get(sky));
        }
    }
}
