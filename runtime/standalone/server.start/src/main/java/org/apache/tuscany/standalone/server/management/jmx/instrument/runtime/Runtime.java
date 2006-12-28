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
package org.apache.tuscany.standalone.server.management.jmx.instrument.runtime;

import org.apache.tuscany.host.runtime.InitializationException;
import org.apache.tuscany.host.runtime.ShutdownException;
import org.apache.tuscany.host.runtime.TuscanyRuntime;

/**
 * @version $Revision$ $Date$
 *
 */
public class Runtime implements RuntimeMbean {
    
    /**
     * Delegate runtime.
     */
    private TuscanyRuntime delegate;
    
    /**
     * Initializes the delegate.
     * @param delegate Delegate MBean.
     */
    public Runtime(TuscanyRuntime delegate) {
        this.delegate = delegate;
    }

    /**
     * @see org.apache.tuscany.host.runtime.TuscanyRuntime#destroy()
     */
    public void destroy() throws ShutdownException {
        delegate.destroy();
    }

    /**
     * @see org.apache.tuscany.host.runtime.TuscanyRuntime#initialize()
     */
    public void initialize() throws InitializationException {
        delegate.initialize();
    }

}
