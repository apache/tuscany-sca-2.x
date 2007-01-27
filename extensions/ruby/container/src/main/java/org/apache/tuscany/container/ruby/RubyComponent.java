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
package org.apache.tuscany.container.ruby;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.extension.ExecutionMonitor;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.container.ruby.rubyscript.RubyScript;
import org.apache.tuscany.container.ruby.rubyscript.RubyScriptInstance;

/**
 * The Ruby component implementation.
 */
public class RubyComponent extends AtomicComponentExtension {

    private final Map<String, Object> properties;

    private RubyScript rubyScript;

    private String rubyClassName;

    public RubyComponent(String name,
                         RubyScript rubyScript,
                         String rubyClassName,
                         Map<String, Object> propValues,
                         CompositeComponent parent,
                         WireService wireService,
                         WorkContext workContext,
                         ExecutionMonitor monitor) {
        super(name, parent, wireService, workContext, null, monitor, 0);

        this.rubyScript = rubyScript;
        this.rubyClassName = rubyClassName;
        //this.properties = new HashMap<String, Object>();
        this.properties = propValues;
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
                RubyReferenceProxy interceptingProxy = new RubyReferenceProxy(businessInterface,
                    wireProxy,
                    rubyScript.getRubyEngine());
                context.put(wire.getReferenceName(), interceptingProxy.createProxy());
            }
        }

        Object instance = rubyScript.createScriptInstance(context, rubyClassName);

        return instance;
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire) {
        /*Method[] methods = operation.getServiceContract().getInterfaceClass().getMethods();
        Method method = findMethod(operation,
                                   methods);*/
        return new RubyInvoker(operation.getName(),
            this,
            operation.getOutputType().getPhysical().getClass(),
            callbackWire,
            workContext,
            monitor);
    }

    // TODO: move all the following up to AtomicComponentExtension?

    public Map<String, Object> getProperties() {
        return properties;
    }

    public RubyScriptInstance getTargetInstance() throws TargetResolutionException {
        return (RubyScriptInstance) scopeContainer.getInstance(this);
    }

}
