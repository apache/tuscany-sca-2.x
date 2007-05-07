/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.test.spec.impl;

import java.util.Date;

import org.apache.tuscany.sca.test.spec.MyListService;
import org.apache.tuscany.sca.test.spec.MyListServiceByYear;
import org.apache.tuscany.sca.test.spec.MyService;
import org.apache.tuscany.sca.test.spec.MyServiceByDate;
import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Service;


@Service(interfaces = {MyService.class, MyServiceByDate.class, MyListService.class, MyListServiceByYear.class})
public class MyServiceImpl implements MyService, MyServiceByDate, MyListService, MyListServiceByYear {
    static String[][] holidays =
        {
         {"2006/01/02", "2006/05/29", "2006/07/03", "2006/07/04", "2006/09/04", "2006/11/23", "2006/11/23",
          "2006/11/24", "2006/12/25"},
         {"2007/01/01", "2007/05/28", "2007/07/04", "2007/09/03", "2007/11/22", "2007/11/23", "2007/12/25"}};

    @Property(name = "location")
    protected String location = "RTP";

    @Property(name = "year")
    protected String year = "2006";

    @ComponentName
    private String componentName;

    public MyServiceImpl() {
        System.out.println("creating service instance...");
    }

    public Date nextHoliday() {

        return nextHolidayByDate(new Date());
    }

    @SuppressWarnings("deprecation")
    public Date nextHolidayByDate(Date today) {
        Date d1;
        String[] days = getHolidays();
        for (int j = 0; j < days.length; j++) {
            d1 = new Date(days[j]);
            if (d1.after(today))
                return d1;
        }
        return null;
    }

    public String[] getHolidaysByYear(int year) {
        int index = year - 2006;
        if (index >= 0 && index < holidays.length)
            return holidays[index];
        return null;
    }

    public String[] getHolidays() {

        Integer theYear;
        if (year == null || year.length() == 0)
            theYear = new Integer("2006");
        else
            theYear = new Integer(year);

        return getHolidaysByYear(theYear.intValue());
    }

    @Init
    public void start() {
        System.out.println("Start service..");
    }

    @Destroy
    public void stop() {
        System.out.println("Stop service..");

    }

    public String getComponentName() {
        return componentName;
    }

    public String getLocation() {
        return location;
    }

    public String getYear() {
        return year;
    }

}
