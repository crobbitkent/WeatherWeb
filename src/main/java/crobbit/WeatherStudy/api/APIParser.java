package crobbit.WeatherStudy.api;

import crobbit.WeatherStudy.repository.WeatherDAO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class APIParser {
    public static final String serviceKey = "ca0nV8nj7B%2FvPU35vfkKH2KPcEXrq42CjvUiRYBuwcFGoIKKY44h4LGLSJYiSTVeQ2KoAgKu9gz6op2SUHRaEA%3D%3D";
    public static final String regId1 = "11B10101";
    public static final String regId2 = "11B00000";

    public static final String[] apiBaseURL = {
            "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst",
            "http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtFcst",
            "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa",
            "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst"
    };


    // API에 접근해서 JSON Data를 item 부분만 JSON ARRAY로 반환
    public static JSONArray apiSetUp(APIType type, WeatherDAO dao, String base_date, String base_time) throws IOException, org.json.simple.parser.ParseException {
        StringBuilder urlBuilder = new StringBuilder(apiBaseURL[type.ordinal()]);

        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
        appendUrlBuilder(urlBuilder, "&", "numOfRows", "500");
        appendUrlBuilder(urlBuilder, "&", "pageNo", "1");
        appendUrlBuilder(urlBuilder, "&", "dataType", "JSON");

        // API 주소를 2개로 나누기
        if(APIType.MID == type || APIType.MIDLAND == type){
            appendUrlBuilder(urlBuilder, "&", "regId", (APIType.MID == type) ? regId1 : regId2);
            appendUrlBuilder(urlBuilder, "&", "tmFc", base_date + base_time); // 기준 날짜와 시간
        }else {
            appendUrlBuilder(urlBuilder, "&", "base_date", base_date); // 기준 날짜
            appendUrlBuilder(urlBuilder, "&", "base_time", base_time); // 기준 시간
            appendUrlBuilder(urlBuilder, "&", "nx", dao.getTodayWeather().getNx());
            appendUrlBuilder(urlBuilder, "&", "ny", dao.getTodayWeather().getNy());
        }

        // 여기서 부턴 동일
        // URL 빌더
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader reader;

        if (200 <= conn.getResponseCode() && 300 >= conn.getResponseCode()) {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();

        String line = "";

        while (null != (line = reader.readLine())) {
            sb.append(line);
        }

        reader.close();
        conn.disconnect();

        String jsonData = sb.toString();

        JSONObject obj = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonData);
        JSONObject parse_response = (JSONObject) jsonObject.get("response");
        JSONObject parse_body = (JSONObject) parse_response.get("body");
        JSONObject parse_items = (JSONObject) parse_body.get("items");

        return (JSONArray) parse_items.get("item");
    }

    private static void appendUrlBuilder(StringBuilder builder, String mark, String a1, String a2) throws IOException {
        builder.append(mark + URLEncoder.encode(a1, "UTF-8") + "=" + URLEncoder.encode(a2, "UTF-8"));
    }
}

