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

package org.apache.tuscany.sca.domain;

import java.lang.reflect.Constructor;

import org.osoa.sca.ServiceRuntimeException;

/**
 * A factory for SCA domains. 
 * 
 * @version $Rev: 580520 $ $Date: 2007-09-29 00:50:25 +0100 (Sat, 29 Sep 2007) $
 */
public abstract class SCADomainFactory {
    
        
    /**
     * Returns a new SCA domain factory instance.
     *  
     * @return a new SCA domain factory
     */
    public static SCADomainFactory newInstance() {
        
        SCADomainFactory scaDomainFactory = null;

        try {
            final ClassLoader classLoader = SCADomainFactory.class.getClassLoader();
            String className =  "org.apache.tuscany.sca.domain.impl.SCADomainFactoryImpl";  
                            
            Class cls = Class.forName(className, true, classLoader);
            
            Constructor<?> constructor = null;
            
            try {
                constructor = cls.getConstructor();
            } catch (NoSuchMethodException e) {
                // ignore
            }
            
            if (constructor != null) {
                scaDomainFactory = (SCADomainFactory)constructor.newInstance();
            } 
            
            return scaDomainFactory;

        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }        
    }

    /**
     * Creates a new SCA domain.
     * 
     * @param domainURI the URI of the domain, this is the endpoint
     * URI of the domain administration service
     * @return a new SCA domain
     */
    public abstract SCADomain createSCADomain(String domainURI) throws DomainException;
    
}
