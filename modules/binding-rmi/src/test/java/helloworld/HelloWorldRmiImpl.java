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

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * This class implements the HelloWorld service.
 */
@Service(HelloWorldRmiService.class)
public class HelloWorldRmiImpl implements HelloWorldRmiService {
    private HelloWorldService extService;

    public HelloWorldService getExtService() {
        return extService;
    }

    @Reference
    public void setExtService(HelloWorldService extService) {
        this.extService = extService;
    }

    public String sayRmiHello(String name) {
        return extService.sayHello(name) + " thro the RMI Reference";
    }
    
    public String sayRmiHi(String name, String greeter) {
        return extService.sayHi(name, greeter) + " thro the RMI Reference";
    }

}
