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

import org.apache.tuscany.sca.itest.databindings.jaxb.Bean1;
import org.apache.tuscany.sca.itest.databindings.jaxb.Bean2;
import org.apache.tuscany.sca.itest.databindings.jaxb.Bean3;
import org.apache.tuscany.sca.itest.databindings.jaxb.GenericsLocalService;
import org.apache.tuscany.sca.itest.databindings.jaxb.GenericsServiceClient;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * An implementation of GenericsLocalServiceClient.
 * The client forwards the request to the service component and returns the response from the service component.
 * 
 * @version $Rev$ $Date$
 */
@Service(GenericsServiceClient.class)
public class GenericsLocalServiceClientImpl implements GenericsServiceClient {

    private GenericsLocalService service;

    @Reference(required=false)
    protected void setGenericsLocalService(GenericsLocalService service) {
        this.service = service;
    }

    public Bean1<String> getTypeExplicitForward(Bean1<String> arg) {
        return service.getTypeExplicit(arg);
    }

    public <T> Bean1<T> getTypeUnboundForward(T[] anArray) {
        return service.getTypeUnbound(anArray);
    }

    public <T extends Bean2> Bean1<T> getTypeExtendsForward(T[] anArray) {
        return service.getTypeExtends(anArray);
    }

    public <T extends Bean1<String>> Bean1<T> getRecursiveTypeBoundForward(T[] anArray) {
        return service.getRecursiveTypeBound(anArray);
    }

    public Bean1<?> getWildcardUnboundForward(Bean1<?> arg) {
        return service.getWildcardUnbound(arg);
    }
    
    public Bean1<? super Bean3> getWildcardSuperForward(Bean1<? super Bean3> arg) {
        return service.getWildcardSuper(arg);
    }

    public Bean1<? extends Bean2> getWildcardExtendsForward(Bean1<? extends Bean2> arg) {
        return service.getWildcardExtends(arg);
    }

    public Bean2 getPolymorphicForward(Bean2 arg) {
        return service.getPolymorphic(arg);
    }
}
