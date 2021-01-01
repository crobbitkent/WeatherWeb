package crobbit.WeatherStudy;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Controller
public class WeatherController
{
	WeatherService weatherService;
	Location location;
	
	public WeatherController() throws IOException, org.json.simple.parser.ParseException
	{
		
		
		this.weatherService = new WeatherService();
		

	}
	
	@GetMapping("weather")
	public String hello(Model model) throws IOException, org.json.simple.parser.ParseException
	{
		Date date = new Date();
		
		String loc = "서울특별시 강남구";
		String nx = "37";
		String ny = "127";
		location = new Location(loc, nx, ny);
		// x = 127.04955555555556, y =	37.514575
		
		location = weatherService.weatherUpdate(location);
		location.setName(loc);
		location.setNx(nx);
		location.setNy(ny);
		location.setDate(date);

		SimpleDateFormat dateInfo = new SimpleDateFormat("yyyy'년' MM'월' dd'일' E'요일' HH:mm:ss", Locale.KOREA);

		model.addAttribute("date", dateInfo.format(location.getDate()));
		model.addAttribute("loc", location.getName());
		model.addAttribute("temperature", location.getT1h());

		return "weather"; // weather.html과 연동!
	}
}
