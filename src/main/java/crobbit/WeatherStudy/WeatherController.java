package crobbit.WeatherStudy;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Controller
public class WeatherController
{
	WeatherService weatherService;
	WeeklyWeatherService weeklyWeatherService;
	Weather weather;
	
	public WeatherController() throws IOException, org.json.simple.parser.ParseException
	{
		WeatherDAO dao = new WeatherDAO();
		this.weatherService = new WeatherService(dao);
		this.weeklyWeatherService = new WeeklyWeatherService(dao);
	}
	
	@GetMapping("weather")
	public String hello(Model model) throws IOException, org.json.simple.parser.ParseException
	{
		Date date = new Date();
		
		weather = new Weather();
		weather.setDate(date);
		// x = 127.04955555555556, y =	37.514575
		
		weatherService.weatherUpdate(weather);
		weeklyWeatherService.update();

		SimpleDateFormat dateInfo = new SimpleDateFormat("yyyy'년' MM'월' dd'일' E'요일' HH:mm:ss", Locale.KOREA);

		model.addAttribute("date", dateInfo.format(weather.getDate()));
		model.addAttribute("loc", weather.getName());
		model.addAttribute("temperature", weather.getT1h());

		return "weather"; // weather.html과 연동!
	}
}
