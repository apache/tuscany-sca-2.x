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
package org.apache.tuscany.sca.vtest.javaapi.annotations.service;

import org.apache.tuscany.sca.vtest.javaapi.annotations.service.impl.AObject;
import org.osoa.sca.annotations.Remotable;

/**
 * Simple Remotable Service
 */
@Remotable
public interface IService {

    public String getName();

    public String setAObject1(AObject a);

    public String setAObject2(AObject a);

    public void setComponentName1(String componentName1);
    
    public String getComponentName1();
    
    public String getComponentName2();
    
    public String getAObject1String();

    public String getAObject2String();

    public String getAObject3String();
    
    public AObject getAObject3();
    
    public String getServiceName1();
    
    public String getServiceName2();

}
