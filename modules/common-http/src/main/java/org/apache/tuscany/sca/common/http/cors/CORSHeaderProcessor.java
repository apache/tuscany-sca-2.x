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

package org.apache.tuscany.sca.common.http.cors;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CORSHeaderProcessor {
    public static void processCORS(CORSConfiguration config, HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        if(config == null) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
            if (request.getMethod().equals("OPTIONS")) {
                response.setHeader("Access-Control-Allow-Methods", "OPTIONS, HEAD, GET, POST, PUT, DELETE");
                response.setHeader("Access-Control-Max-Age", "1728000");
            }
            return;
        }
        
        if(config.allowCredentials) {
            response.setHeader("Access-Control-Allow-Credentials", Boolean.toString(config.isAllowCredentials()));
        }

        if(config.getMaxAge() > 0) {
            response.setHeader("Access-Control-Max-Age", Integer.toString(config.getMaxAge()));
        }
        
        response.setHeader("Access-Control-Allow-Origin", getAllowOrigins(config));
        response.setHeader("Access-Control-Allow-Methods", getAllowMethods(config));
        response.setHeader("Access-Control-Allow-Headers", getAllowHeaders(config));
        response.setHeader("Access-Control-Expose-Headers", getExposeHeaders(config));
    }
    
    private static String getAllowOrigins(CORSConfiguration config) {
        return getListValues(config.getAllowOrigins(), "*");
    }

    private static String getAllowMethods(CORSConfiguration config) {
        return getListValues(config.getAllowMethods(), "OPTIONS, HEAD, GET, POST, PUT, DELETE");
    }

    private static String getAllowHeaders(CORSConfiguration config) {
        return getListValues(config.getAllowHeaders(), "X-Requested-With, Content-Type");
    }
    
    private static String getExposeHeaders(CORSConfiguration config) {
        return getListValues(config.getExposeHeaders(), "X-Requested-With, Content-Type");
    }
    
    private static String getListValues(List<String> list, String defaultValue) {
        StringBuffer values = new StringBuffer();
        if(list != null && list.isEmpty() == false) {
            for(String value : list) {
                values.append(value).append(",");
            }
            values.deleteCharAt(values.length());
        } else {
            values.append(defaultValue);
        }
        
        return values.toString();
    }
}
