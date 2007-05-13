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
 package org.apache.tuscany.sca.binding.jsonrpc.util;

import java.lang.reflect.Method;


public class JavaToSmd {
    
    public static String interfaceToSmd(Class klazz, String serviceUrl) {
        String name = klazz.getSimpleName();
        Method methods[] = klazz.getMethods();
        
        StringBuffer smdSb = new StringBuffer();
        smdSb.append("{\"SMDVersion\":\".1\",\"objectName\":\"" + name + "\",\"serviceType\":\"JSON-RPC\",\"serviceURL\":\""+ serviceUrl + "\",\"methods\":[");
        for (int i = 0; i < methods.length; i++) {
            if (i != 0) smdSb.append(",");
            Class<?> params[] = methods[i].getParameterTypes();            
            smdSb.append("{\"name\":\""+methods[i].getName() + "\",\"parameters\":[");
            for (int j = 0; j < params.length; j++) {
                if (j != 0) smdSb.append(",");
                // right now Dojo doesn't look at the type value, so we'll default it to STRING
                // also, since we can't introspect the method paramater names we'll just create an incrementing parameter name
                smdSb.append("{\"name\":\"param" + j + "\",\"type\":\"STRING\"}");  
            }
            smdSb.append("]}");
        }
        smdSb.append("]}");
        
        return smdSb.toString();        
    }

}
