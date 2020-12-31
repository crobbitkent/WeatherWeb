package crobbit.WeatherStudy;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySource(value = "classpath:weather.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "api")
public class WeatherYmlRead
{
	private static String serviceKey;
	
	
	public static String getServiceKey()
	{
		return serviceKey;
	}
	
	public static void setServiceKey(String serviceKey)
	{
		this.serviceKey = serviceKey;
	}
}
