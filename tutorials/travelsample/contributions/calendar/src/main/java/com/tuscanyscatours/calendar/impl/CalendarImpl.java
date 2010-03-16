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
package com.tuscanyscatours.calendar.impl;

import java.text.DateFormat;
import java.util.Date;

import com.tuscanyscatours.calendar.Calendar;

/**
 * An implementation of the Calendar service
 */
public class CalendarImpl implements Calendar {

    public String getEndDate(String startDate, int duration) {
        String returnDate = "Invalid Date";

        try {
            Date date = DateFormat.getInstance().parse(startDate);
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(java.util.Calendar.DATE, duration);
            returnDate = DateFormat.getInstance().format(calendar.getTime());
        } catch (Exception ex) {
            // do nothing  
            System.out.println(ex.toString());
        }

        return returnDate;
    }
}
