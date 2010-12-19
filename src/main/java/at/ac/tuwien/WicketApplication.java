package at.ac.tuwien;

import org.apache.wicket.protocol.http.WebApplication;

public class WicketApplication extends WebApplication
{    
	public WicketApplication()
	{
	}
	
	public Class<BasePage> getHomePage()
	{
		return BasePage.class;
	}

}
