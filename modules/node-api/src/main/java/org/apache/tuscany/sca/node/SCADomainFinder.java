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

package org.apache.tuscany.sca.node;

import java.lang.reflect.Constructor;

import org.apache.tuscany.sca.domain.DomainException;
import org.apache.tuscany.sca.domain.SCADomain;
import org.osoa.sca.ServiceRuntimeException;

/**
 * A finder for SCA domains.
 * 
 * @version $Rev: 580520 $ $Date: 2007-09-29 00:50:25 +0100 (Sat, 29 Sep 2007) $
 */
public abstract class SCADomainFinder {
    
        
    /**
     * Returns a new SCA domain finder instance.
     *  
     * @return a new SCA domain finder
     */
    public static SCADomainFinder newInstance() {
        SCADomainFinder scaDomainFinder = null;

        try {
            final ClassLoader classLoader = SCADomainFinder.class.getClassLoader();
            String className =  "org.apache.tuscany.sca.node.impl.SCADomainFinderImpl";  
                            
            Class cls = Class.forName(className, true, classLoader);
            
            Constructor<?> constructor = null;
            
            try {
                constructor = cls.getConstructor();
            } catch (NoSuchMethodException e) {
                // ignore
            }
            
            if (constructor != null) {
                scaDomainFinder = (SCADomainFinder)constructor.newInstance();
            } 
            
            return scaDomainFinder;

        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }  
    }

    /**
     * Finds an existing SCA domain.
     * 
     * @param domainURI the URI of the domain, this is the endpoint
     * URI of the domain administration service
     * @return the SCA domain
     */
    public abstract SCADomain getSCADomain(String domainURI) throws DomainException;
    
}
