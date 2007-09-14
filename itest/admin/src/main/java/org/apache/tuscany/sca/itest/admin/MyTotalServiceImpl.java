package org.apache.tuscany.sca.itest.admin;

import java.util.Date;


import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.ComponentName;
import org.apache.tuscany.sca.itest.admin.MyListService;
import org.apache.tuscany.sca.itest.admin.MyListServiceByYear;
import org.apache.tuscany.sca.itest.admin.MyService;
import org.apache.tuscany.sca.itest.admin.MyServiceByDate;
import org.apache.tuscany.sca.itest.admin.MyTotalService;

@Service(MyTotalService.class)

public class MyTotalServiceImpl implements MyTotalService
{

    // This is multiplicity=1:1
    @Reference(required=true)
    public MyListService myListService;
    
    // default required==true so it is 1:1
    @Reference (required=false)
    public MyListServiceByYear myListServiceByYear = new MyServiceImpl();
    
    // default required==true so it is 1:1
    @Reference
    public MyService myService;
    
    // This is multiplicity=0:1 
    @Reference(required=false)
    public MyServiceByDate myServiceByDate = new MyServiceImpl();
	
	   @ComponentName
    private String componentName;

    public String[] getHolidays()
    {
        return myListService.getHolidays();
    }

    public String[] getHolidays(int year)
    {
        return myListServiceByYear.getHolidays(year);
    }

    
    public Date nextHoliday(Date date)
    {
        return myServiceByDate.nextHoliday(date);
    }

    public String getLocation()
    {
        return myService.getLocation();
    }

    public String getYear()
    {
        return myService.getYear();
    }
	
	  public String getComponentName()
    {
        return componentName;
    }

    public Date nextHoliday()
    {
        return myService.nextHoliday();
    }
}
