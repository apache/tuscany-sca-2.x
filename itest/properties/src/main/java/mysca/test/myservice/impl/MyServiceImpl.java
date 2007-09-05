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

package mysca.test.myservice.impl;

import java.util.logging.Logger;

import org.osoa.sca.annotations.ComponentName;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Service;

@Service(MyService.class)
public class MyServiceImpl implements MyService {

    @Property(name = "location")
    protected String location = "RTP";

    @Property(name = "year")
    protected String year = "2006";

    @ComponentName
    private String componentName;


    private Logger logger;

    public MyServiceImpl() {
        logger = Logger.getAnonymousLogger();
        logger.info("creating service instance...");
    }

    @Init
    public void start() {
        logger.info("Start service..");
    }

    @Destroy
    public void stop() {
        logger.info("Stop service..");

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
