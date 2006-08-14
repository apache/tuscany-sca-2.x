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
package org.apache.tuscany.servicemix;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

/**
 * 
 */
public class ServiceMixReference<T> extends ReferenceExtension<T> {

    private final String uri; 

    public ServiceMixReference(String name, 
                        CompositeComponent<?> parent, 
                        WireService wireService, 
                        String uri,
                        Class<T> service) 
    {
        super(name, service, parent, wireService);
        setInterface(service);
        this.uri = uri;
    }

    public TargetInvoker createTargetInvoker(Method arg0) {
        QName serviceName = null;
        ServiceMixInvoker invoker = new ServiceMixInvoker(serviceName);
        return invoker;
    }

}
