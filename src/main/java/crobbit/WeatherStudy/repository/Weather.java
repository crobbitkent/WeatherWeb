package crobbit.WeatherStudy.repository;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

/*
□ 하늘상태
o 중기예보 통보문에서 구름의 양에 따라 하늘상태를 3단계(맑음, 구름많음, 흐림)으로 표현, 현상에 따라 비, 눈, 비/눈, 소나기 로 표현하고 있으며, 이를 종합하여 함께 사용하고 있음
- 맑음
- 구름많음, 구름많고 비, 구름많고 눈, 구름많고 비/눈, 구름많고 소나기
- 흐림, 흐리고 비, 흐리고 눈, 흐리고 비/눈, 흐리고 소나기
* 소나기 추가(2020.9.14.)
 */

@Setter
@Getter
public class Weather
{
	private String name; // 주소지 이름
	private String nx; // 격자 위도
	private String ny; // 격자 경도
	private String pty; // 강수 형태
	private String sky; // 하늘 상태
	private String t1h; // 기온 (현재)
	private String minTemperature; // 최저 온도
	private String maxTemperature; // 최고 온도
	private String rainRate;
	private String weeklySky;
	private String fcstTime;

	// 강수형태(PTY) 코드
	public static final String[] PTYCode = new String[] {"없음", "비", "비/눈", "눈", "소나기", "빗방울", "빗방울/눈날림", "눈날림"};
	// 하늘상태(SKY) 코드
	public static final String[] SKYCode = new String[] {"", "맑음", "", "구름많음", "흐림"};
	
	//===============================CONSTRUCTOR===================================//
	public Weather() {
		// 임시로 기본 정보를 넣어 놓는다.
		String loc = "서울특별시 강남구";
		String nx = "37";
		String ny = "127";
		name = loc;
		this.nx = nx;
		this.ny = ny;
	};
	
	public Weather(String name, String nx, String ny) {
		this.name = name;
		this.nx = nx;
		this.ny = ny;
	};
	
	//===============================TO STRING===================================//
	@Override
	public String toString()
	{
		return "Weather{" +
					   "name='" + name + '\'' +
					   ", nx='" + nx + '\'' +
					   ", ny='" + ny + '\'' +
					   ", pty='" + pty + '\'' +
					   ", sky='" + sky + '\'' +
					   ", t1h='" + t1h + '\'' +
					   '}';
	}

}
