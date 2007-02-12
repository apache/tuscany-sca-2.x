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
import org.apache.tuscany.sca.test.spec.MyTotalService;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;


@Service(MyTotalService.class)
public class MyTotalServiceImpl implements MyTotalService {

    // This is multiplicity=1:1
    @Reference(required = true)
    public MyListService myListService;

    // default required==true so it is 1:1
    @Reference
    public MyListServiceByYear myListServiceByYear = new MyServiceImpl();

    // default required==true so it is 1:1
    @Reference
    public MyService myService;

    // This is multiplicity=0:1
    @Reference(required = false)
    public MyServiceByDate myServiceByDate = new MyServiceImpl();

    public String[] getHolidays() {
        return myListService.getHolidays();
    }

    public String[] getHolidays(int year) {
        return myListServiceByYear.getHolidays(year);
    }

    public String getComponentName() {
        return myService.getComponentName();
    }

    public ComponentContext getContext() {
        return myService.getContext();
    }

    public Date nextHoliday(Date date) {
        return myServiceByDate.nextHoliday(date);
    }

    public String getLocation() {
        return myService.getLocation();
    }

    public String getYear() {
        return myService.getYear();
    }

    public Date nextHoliday() {
        return myService.nextHoliday();
    }
}
