package org.apache.tuscany.sca.itest.admin;


import java.util.List;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.sca.itest.admin.MyListService;
import org.apache.tuscany.sca.itest.admin.MyListServiceByYear;

@Service(interfaces={MyListService.class,MyListServiceByYear.class})

public class MyListServiceImpl implements MyListService,MyListServiceByYear
{

    // This is multiplicity=1:n
    @Reference(name="myListServiceList",required=true)
    public List<MyListService> myListServicesList;

    // This is multiplicity=0:n
    @Reference(name="myListServiceArray",required=false)
    public MyListService[] myListServicesArray;
    
    @Property(name="serviceYear")
    protected String year = "2006";
    
    public String[] getHolidays()
    {
        return getHolidays(new Integer(year).intValue());
    }

    public String[] getHolidays(int year)
    {
        MyListService myService;
        if (myListServicesList!=null)
        {
            for (int i=0; i<myListServicesList.size(); i++)
            {
                myService=myListServicesList.get(i);
                if(new Integer(myService.getYear()).intValue()==year)
                {
                    return myService.getHolidays();
                }
            }
        }
        if (myListServicesArray!=null)
        {
            for (int i=0; i<myListServicesArray.length; i++)
            {
                myService=myListServicesArray[i];
                if(new Integer(myService.getYear()).intValue()==year)
                {
                    return myService.getHolidays();
                }
            }
        }
        return null;
    }

    public String getYear()
    {
        return year;
    }
    
    
    
}
