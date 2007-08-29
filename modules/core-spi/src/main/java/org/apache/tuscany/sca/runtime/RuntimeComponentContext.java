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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.tuscany.sca.assembly.ComponentService;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

/**
 * @version $Rev$ $Date$
 */
public interface RuntimeComponentContext extends ComponentContext {
    /**
     * Activate the reference (creating runtime wires)
     * @param reference
     */
    void activate(RuntimeComponentReference reference);
    
    /**
     * Deserialize the component reference
     * @param reader
     * @return A component that contains the reference
     * @throws IOException
     */
    RuntimeComponent read(Reader reader) throws IOException;
    /**
     * Serialize the component reference
     * @param reference
     * @param writer
     * @throws IOException
     */
    void write(RuntimeComponentReference reference, Writer writer) throws IOException;

    /**
     * Get the callable reference for a given component reference
     * @param <B>
     * @param businessInterface The business interface
     * @param reference The reference to be wired
     * @return A service reference representing the wire
     */
    <B> ServiceReference<B> getServiceReference(Class<B> businessInterface,
                                                RuntimeComponentReference reference);    
    /**
     * Bind the reference to a target component/componentService
     * @param <B>
     * @param businessInterface The business interface
     * @param reference The reference to be wired
     * @param component The target component
     * @param service The target component service
     * @return A service reference representing the wire
     */
    <B> ServiceReference<B> getServiceReference(Class<B> businessInterface,
                                                RuntimeComponentReference reference,
                                                RuntimeComponent component,
                                                RuntimeComponentService service);
    
    /**
     * Create a callable reference for the given component service
     * @param <B>
     * @param businessInterface
     * @param component
     * @param service
     * @return
     */
    <B> CallableReference<B> getCallableReference(Class<B> businessInterface,
                                                         RuntimeComponent component,
                                                         RuntimeComponentService service);
    
    /**
     * @param <B>
     * @param businessInterface
     * @param service
     * @return
     */
    <B> ServiceReference<B> createSelfReference(Class<B> businessInterface, ComponentService service);
}
