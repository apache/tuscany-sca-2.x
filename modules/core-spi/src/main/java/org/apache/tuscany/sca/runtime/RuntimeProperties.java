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

package org.apache.tuscany.sca.runtime;

import java.util.Properties;
/**
 * 
 * @tuscany.spi.extension.asclient
 *
 */
public interface RuntimeProperties {
    
    /**
     *  When true log with Level.FINE instead of Level.INFO
     */
    public static final String QUIET_LOGGING = "org.apache.tuscany.sca.quietLogging";

    /**
     *  When true attempt to release the ClassLoader used by a Contribution when its unloaded
     */
    public static final String RELEASE_ON_UNLOAD = "org.apache.tuscany.sca.releaseOnUnload";
    
    /**
     *  The name of the binding type to use be default for the remote SCA binding
     */
    public static final String SCA_BINDING_TYPE = "org.apache.tuscany.sca.scaBindingType";

    /**
     *  Use AXIOM OMElement instead of DOM as the XML object representation
     */
    public static final String USE_AXIOM = "org.apache.tuscany.sca.useAxiom";

    Properties getProperties();
    void setProperties(Properties properties);
}
