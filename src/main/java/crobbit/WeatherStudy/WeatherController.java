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
	
	public WeatherController() throws IOException, org.json.simple.parser.ParseException
	{
		WeatherDAO dao = new WeatherDAO();
		this.weatherService = new WeatherService(dao);
		this.weeklyWeatherService = new WeeklyWeatherService(dao);
	}
	
	@GetMapping("weather")
	public String weatherUpdate(Model model) throws IOException, org.json.simple.parser.ParseException
	{
		this.weatherService.update();
		this.weeklyWeatherService.update();

		SimpleDateFormat dateInfo = new SimpleDateFormat("yyyy'년' MM'월' dd'일' E'요일' HH:mm:ss", Locale.KOREA);

		Weather today = weatherService.getTodayWeather();
		Date date = new Date();
		model.addAttribute("date", dateInfo.format(date));
		model.addAttribute("loc", today.getName());
		model.addAttribute("temperature", today.getT1h());

		return "weather"; // weather.html과 연동!
	}
}
