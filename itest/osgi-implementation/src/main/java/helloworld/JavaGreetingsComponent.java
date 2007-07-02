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

package helloworld;

import org.osoa.sca.annotations.AllowsPassByReference;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(Greetings.class)
@Scope("COMPOSITE")
public class JavaGreetingsComponent implements Greetings {

    private Greetings greetingsService;
    
    @Reference
    public void setGreetingsService(Greetings greetingsService) {
    	this.greetingsService = greetingsService;
    }
    
   
    public String[] getGreetingsFromJava(String s[]) {
        for (int i = 0; i < s.length; i++) {
            s[i] = "Hello " + s[i] + "(From Java)";
        }
            
        return greetingsService.getGreetingsFromJava(s);
    }
    
    public String[] getGreetingsFromOSGi(String s[]) {
        for (int i = 0; i < s.length; i++) {
            s[i] = s[i] + "(From Java)";
        }
            
        return s;
    }
    
    @AllowsPassByReference
    public String[] getModifiedGreetingsFromJava(String s[]) {
        for (int i = 0; i < s.length; i++) {
            s[i] = "Hello " + s[i] + "(From Java)";
        }
            
        return greetingsService.getModifiedGreetingsFromJava(s);
    }
    
    @AllowsPassByReference
    public String[] getModifiedGreetingsFromOSGi(String s[]) {
        for (int i = 0; i < s.length; i++) {
            s[i] = s[i] + "(From Java)";
        }
            
        return s;
    }

}
