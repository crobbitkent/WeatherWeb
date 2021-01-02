package crobbit.WeatherStudy;

import org.springframework.stereotype.Component;

@Component
public class WeatherDAO
{
	// 강수형태(PTY) 코드
	public static final String[] PTYCode = new String[] {"없음", "비", "비/눈", "눈", "소나기", "빗방울", "빗방울/눈날림", "눈날림"};
	
	// 하늘상태(SKY) 코드
	public static final String[] SKYCode = new String[] {"", "맑음", "", "구름많음", "흐림"};
	
	public static final String serviceKey = "ca0nV8nj7B%2FvPU35vfkKH2KPcEXrq42CjvUiRYBuwcFGoIKKY44h4LGLSJYiSTVeQ2KoAgKu9gz6op2SUHRaEA%3D%3D";
	
	public static final String regId = "11B10101";
	
	private Weather[] weeklyWeather;
	
	public Weather[] getWeeklyWeather()
	{
		return weeklyWeather;
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
	
	private Weather todayWeather;
	
	public WeatherDAO(){
		weeklyWeather = new Weather[8];
	}
}
