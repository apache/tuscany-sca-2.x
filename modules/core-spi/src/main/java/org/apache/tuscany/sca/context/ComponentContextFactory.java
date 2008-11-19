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
package org.apache.tuscany.sca.context;

import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.osoa.sca.ComponentContext;

/**
 * Interface implemented by the provider of the ComponentContext.
 * 
 * @version $Rev$ $Date$
 */
public interface ComponentContextFactory {
    /**
     * Create an instance of ComponentContext
     * 
     * @param component The runtime component
     * @param requestContextFactory The factory to create RequestContext
     * @return An instance of ComponentContext for the component
     */
    ComponentContext createComponentContext(RuntimeComponent component, RequestContextFactory requestContextFactory);
}
