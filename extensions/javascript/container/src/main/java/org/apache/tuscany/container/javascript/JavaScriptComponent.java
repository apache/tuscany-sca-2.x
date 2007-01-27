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
package org.apache.tuscany.container.javascript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.extension.ExecutionMonitor;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.container.javascript.rhino.RhinoScript;
import org.apache.tuscany.container.javascript.rhino.RhinoScriptInstance;

/**
 * The JavaScript component implementation.
 */
public class JavaScriptComponent extends AtomicComponentExtension {

    private final Map<String, Object> properties;

    private RhinoScript rhinoScript;

    public JavaScriptComponent(String name, RhinoScript rhinoScript, Map<String, Object> properties,
                               CompositeComponent parent, WireService wireService,
                               WorkContext workContext,
                               ExecutionMonitor monitor) {
        super(name, parent, wireService, workContext, null, monitor, 0);

        this.rhinoScript = rhinoScript;
        this.properties = properties;
    }

    public Object createInstance() throws ObjectCreationException {

        Map<String, Object> context = new HashMap<String, Object>(getProperties());

        for (List<OutboundWire> referenceWires : getOutboundWires().values()) {
            for (OutboundWire wire : referenceWires) {
                Class<?> clazz = wire.getServiceContract().getInterfaceClass();
                Object wireProxy = wireService.createProxy(clazz, wire);
                //since all types that may be used in the reference interface may not be known to Rhino
                //using the wireProxy as is will fail result in type conversion exceptions in cases where
                //Rhino does not know enough of the tpypes used.  Hence introduce a interceptor proxy, 
                //with weak typing (java.lang.Object) so that Rhino's call to the proxy succeeds.  Then
                //within this interceptor proxy perform data mediations required to correctly call the 
                //referenced service.                
                Class<?> businessInterface = wire.getServiceContract().getInterfaceClass();
                JavaScriptReferenceProxy interceptingProxy =
                    new JavaScriptReferenceProxy(businessInterface,
                        wireProxy,
                        rhinoScript.createInstanceScope(context));
                context.put(wire.getReferenceName(), interceptingProxy.createProxy());

            }
        }

        return rhinoScript.createRhinoScriptInstance(context);
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire) {
        return new JavaScriptInvoker(operation.getName(), this, callbackWire, workContext, monitor);
    }

    // TODO: move all the following up to AtomicComponentExtension?

    public Map<String, Object> getProperties() {
        return properties;
    }

    public RhinoScriptInstance getTargetInstance() throws TargetResolutionException {
        return (RhinoScriptInstance) scopeContainer.getInstance(this);
    }

}
