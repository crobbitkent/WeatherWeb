package crobbit.WeatherStudy;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.Date;

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
		
		model.addAttribute("data", location.toString());
		
		return "weather"; // weather.html과 연동!
	}
}
