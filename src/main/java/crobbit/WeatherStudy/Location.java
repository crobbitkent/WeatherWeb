package crobbit.WeatherStudy;

import java.util.Date;

public class Location
{
	private String name; // 주소지 이름
	private String nx; // 격자 위도
	private String ny; // 격자 경도
	private String pty; // 강수 형태
	private String sky; // 하늘 상태
	private String t1h; // 기온
	private Date date;
	
	//===============================CONSTRUCTOR===================================//
	protected Location() {};
	
	public Location(String name, String nx, String ny) {
		this.name = name;
		this.nx = nx;
		this.ny = ny;
		this.pty = "";
		this.sky = "";
		this.t1h = "";
	};
	
	public Location(String name, String nx, String ny, String pty, String sky, String t1h, Date date)
	{
		this.name = name;
		this.nx = nx;
		this.ny = ny;
		this.pty = pty;
		this.sky = sky;
		this.t1h = t1h;
		this.date = date;
	}
	
	//===============================TO STRING===================================//
	
	
	@Override
	public String toString()
	{
		return "Location{" +
					   "name='" + name + '\'' +
					   ", nx='" + nx + '\'' +
					   ", ny='" + ny + '\'' +
					   ", pty='" + pty + '\'' +
					   ", sky='" + sky + '\'' +
					   ", t1h='" + t1h + '\'' +
					   ", date=" + date +
					   '}';
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getNx()
	{
		return nx;
	}
	
	public void setNx(String nx)
	{
		this.nx = nx;
	}
	
	public String getNy()
	{
		return ny;
	}
	
	public void setNy(String ny)
	{
		this.ny = ny;
	}
	
	public String getPty()
	{
		return pty;
	}
	
	public void setPty(String pty)
	{
		this.pty = pty;
	}
	
	public String getSky()
	{
		return sky;
	}
	
	public void setSky(String sky)
	{
		this.sky = sky;
	}
	
	public String getT1h()
	{
		return t1h;
	}
	
	public void setT1h(String t1h)
	{
		this.t1h = t1h;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public void setDate(Date date)
	{
		this.date = date;
	}
}
