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

package org.apache.tuscany.sca.itest.databindings.jaxb.impl;

import org.apache.tuscany.sca.itest.databindings.jaxb.StandardTypesService;
import org.apache.tuscany.sca.itest.databindings.jaxb.StandardTypesServiceClient;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * An implementation of StandardTypesServiceClient.
 * The client forwards the request to the service component and returns the response from the service component.
 */
@Service(StandardTypesServiceClient.class)
public class StandardTypesServiceClientImpl extends StandardTypesLocalServiceClientImpl {

    @Reference
    public void setStandardTypesService(StandardTypesService service) {
        super.setStandardTypesLocalService(service);
    }
}
