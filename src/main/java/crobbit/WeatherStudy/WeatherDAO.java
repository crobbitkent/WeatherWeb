package crobbit.WeatherStudy;

import org.springframework.stereotype.Component;

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
