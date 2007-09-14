package org.apache.tuscany.sca.itest.admin;

import java.util.Date;

import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Service;
import org.apache.tuscany.sca.itest.admin.MyListService;
import org.apache.tuscany.sca.itest.admin.MyListServiceByYear;
import org.apache.tuscany.sca.itest.admin.MyLogger;
import org.apache.tuscany.sca.itest.admin.MyService;
import org.apache.tuscany.sca.itest.admin.MyServiceByDate;


@Service(interfaces={MyService.class, MyServiceByDate.class, MyListService.class, MyListServiceByYear.class})

public class MyServiceImpl implements MyService, MyServiceByDate, MyListService, MyListServiceByYear{
	static String[][] holidays = {{"2006/01/02","2006/05/29","2006/07/03","2006/07/04","2006/09/04","2006/11/23",
	"2006/11/23","2006/11/24", "2006/12/25"},{"2007/01/01","2007/05/28","2007/07/04","2007/09/03","2007/11/22",
        "2007/11/23","2007/12/25"},{"2006/01/02","2006/05/29","2006/07/03","2006/07/04","2006/09/04","2006/11/23",
            "2006/11/23","2006/11/24", "2006/12/25"},{"2007/01/01","2007/05/28","2007/07/04","2007/09/03","2007/11/22",
                "2007/11/23","2007/12/25"}};
	
	@Property(name="location")
	protected String location = "RTP";

    @Property(name="year")
    protected String year = "2006";
    

    private String componentName;
    

    private MyLogger logger;

	public MyServiceImpl()
	{
	    logger = new MyLogger(System.out);
	    logger.println("creating service instance...");
	}
    
	public Date nextHoliday() {
        
        return nextHoliday(new Date());
	}

    
	public Date nextHoliday(Date today)
    {
        Date d1;
        String[] days = getHolidays();
        for (int j=0; j<days.length; j++)
        {
            d1 = new Date(days[j]);
            if (d1.after(today))
               return d1;
        }
        return null;
    }

    
    public String[] getHolidays(int year)
    {
        int index = year -2006;
        if (index>=0  && index<holidays.length)
            return holidays[index];
        return null;
    }

    public String[] getHolidays()
    {
        
        Integer theYear;
        if (year ==null || year.length()==0)
            theYear = new Integer("2006");
        else
            theYear = new Integer(year);
        
        return getHolidays(theYear.intValue());
    }

    @Init
	public void start()
	{
		logger.println("Start service..");
	}

	@Destroy
	public void stop()
	{
		logger.println("Stop service..");
		
	}

    public String getComponentName()
    {
        return componentName;
    }

 
    public String getLocation()
    {
       return location;
    }

    public String getYear()
    {
        return year;
    }
	
}
