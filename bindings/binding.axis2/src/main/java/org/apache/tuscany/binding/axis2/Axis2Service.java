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
package org.apache.tuscany.binding.axis2;


import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.wire.WireService;
import org.osoa.sca.annotations.Destroy;

/**
 * An implementation of a {@link ServiceExtension} configured with the Axis2 binding
 *
 * @version $Rev$ $Date$
 */
public class Axis2Service<T> extends ServiceExtension<T> {
    
    
    public static Axis2Service currentAxis2Service;
    private WebServiceBinding wsBinding;

    public Axis2Service(String theName,
                        Class<T> interfaze,
                        CompositeComponent parent,
                        WireService wireService,
                        WebServiceBinding binding,
                        ServletHost servletHost) {
        super(theName, interfaze, parent, wireService);
        wsBinding = binding;
    
    }

    public void start() {
        super.start();
//TODO This is a big hack ... need to replace with ServletHost api ASAP
            
            currentAxis2Service= this;
        
    }

    @Destroy
    public void stop() throws CoreRuntimeException {
        super.stop();
    }


    WebServiceBinding getWsBinding() {
        return wsBinding;
    }
}