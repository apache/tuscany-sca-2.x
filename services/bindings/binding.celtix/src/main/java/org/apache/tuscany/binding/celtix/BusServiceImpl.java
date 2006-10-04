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
package org.apache.tuscany.binding.celtix;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.tuscany.idl.wsdl.WSDLDefinitionRegistry;

import org.objectweb.celtix.Bus;
import org.objectweb.celtix.BusException;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;

/**
 * The default implementation of the Celtix Bus system service
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class BusServiceImpl implements BusService {

    protected WSDLDefinitionRegistry wsdlRegistry;
    private Bus bus;

    public Bus getBus() {
        return bus;
    }

    /**
     * FIXME the annotation processing framework must inject this
     * @param wsdlReg
     */
    @Property (override = "must")
    public void setWsdlRegistry(WSDLDefinitionRegistry wsdlReg) {
        wsdlRegistry = wsdlReg;
    }

    /**
     * Initializes the bus, set to be called when the runtime initializes the Celtix system composite
     * @throws BusException
     */
    @Init(eager = true)
    public void init() throws BusException {
        Map<String, Object> properties = new WeakHashMap<String, Object>();
        properties.put("celtix.WSDLManager", new TuscanyWSDLManager(wsdlRegistry));
        bus = Bus.init(new String[0], properties);
    }

    /**
     * Shuts down the bus, called when the runtime stops the Celtix system composite
     * @throws BusException
     */
    @Destroy
    public void stop() throws BusException {
        bus.shutdown(true);
    }

}
