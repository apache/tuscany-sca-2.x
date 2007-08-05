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

import java.util.List;

import org.apache.tuscany.sca.test.spec.MyListService;
import org.apache.tuscany.sca.test.spec.MyListServiceByYear;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;


@Service(interfaces = {MyListService.class, MyListServiceByYear.class})
public class MyListServiceImpl implements MyListService, MyListServiceByYear {

    // This is multiplicity=1:n
    @Reference(name = "myListServiceList", required = false)
    public List<MyListService> myListServicesList;

    // This is multiplicity=0:n
    @Reference(name = "myListServiceArray", required = false)
    public MyListService[] myListServicesArray;

    @Property(name = "serviceYear")
    protected String year = "2006";

    public String[] getHolidays() {
        return getHolidaysByYear(new Integer(year).intValue());
    }

    public String[] getHolidaysByYear(int year) {
        MyListService myService;
        if (myListServicesList != null) {
            for (int i = 0; i < myListServicesList.size(); i++) {
                myService = myListServicesList.get(i);
                if (new Integer(myService.getYear()).intValue() == year) {
                    return myService.getHolidays();
                }
            }
        }
        if (myListServicesArray != null) {
            for (int i = 0; i < myListServicesArray.length; i++) {
                myService = myListServicesArray[i];
                if (new Integer(myService.getYear()).intValue() == year) {
                    return myService.getHolidays();
                }
            }
        }
        return null;
    }

    public String getYear() {
        return year;
    }

}
