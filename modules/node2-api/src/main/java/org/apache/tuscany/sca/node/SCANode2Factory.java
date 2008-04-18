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

import org.osoa.sca.ServiceRuntimeException;

/**
 * A factory for SCA processing nodes. An SCA processing node can be loaded
 * with an SCA composite and the SCA contributions required by the composite.
 * 
 * @version $Rev$ $Date$
 */
public abstract class SCANode2Factory {
    
        
    /**
     * Returns a new SCA node factory instance.
     *  
     * @return a new SCA node factory
     */
    public static SCANode2Factory newInstance() {
        SCANode2Factory scaNodeFactory = null;
        
        try {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String className =  "org.apache.tuscany.sca.node.impl.NodeFactoryImpl";  
                            
            Class<?> cls = Class.forName(className, true, classLoader);
            
            Constructor<?> constructor = null;
            
            try {
                constructor = cls.getConstructor();
            } catch (NoSuchMethodException e) {
                // ignore
            }
            
            if (constructor != null) {
                scaNodeFactory = (SCANode2Factory)constructor.newInstance();
            } 
            
            return scaNodeFactory;

        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }  
    }

    /**
     * Creates a new SCA node.
     * 
     * @param configurationURI the URI of the node configuration 
     * @return a new SCA node.
     */
    public abstract SCANode2 createSCANode(String configurationURI);

    /**
     * Creates a new SCA node.
     * 
     * @param compositeURI the URI of the composite to use 
     * @param contributions the URI of the contributions that provides the composites and related artifacts 
     * @return a new SCA node.
     */
    public abstract SCANode2 createSCANode(String compositeURI, SCAContribution... contributions);

    /**
     * Represents an SCA contribution uri + location.
     */
    public final static class SCAContribution {
        private String uri;
        private String location;
        
        /**
         * Constructs a new SCA contribution.
         * 
         * @param uri
         * @param location
         */
        public SCAContribution(String uri, String location) {
            this.uri = uri;
            this.location = location;
        }
        
        public String getURI() {
            return uri;
        }
        
        public String getLocation() {
            return location;
        }
    }
    
}
