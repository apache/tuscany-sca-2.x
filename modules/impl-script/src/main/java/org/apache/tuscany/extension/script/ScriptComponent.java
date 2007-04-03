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

package org.apache.tuscany.extension.script;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.wire.ProxyService;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

public class ScriptComponent extends AtomicComponentExtension {

    private ScriptImplementation impl;
    protected Map<String, List<Wire>> wires = new HashMap<String, List<Wire>>();

    protected ScriptComponent(URI name,
                              ScriptImplementation implementation,
                              ProxyService proxyService,
                              WorkContext workContext,
                              URI groupId,
                              int initLevel) {
        super(name, proxyService, workContext, groupId, initLevel);
        impl = implementation;
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation) {
        return new ScriptInvoker(operation.getName(), this, scopeContainer, workContext);
    }

    @SuppressWarnings("unchecked")
    public InstanceWrapper<?> createInstanceWrapper() throws ObjectCreationException {
        try {

            Thread.currentThread().setContextClassLoader(getClass().getClassLoader()); // TODO: classloader?

            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByExtension(impl.getScriptLanguage());
            if (engine == null) {
                throw new ObjectCreationException("no script engine found for language: " + impl.getScriptLanguage());
            }

            Reader reader;
            if (impl.getInlineSrc() == null) {
                URL url = impl.getClassLoader().getResource(impl.getScriptName());
                reader = new InputStreamReader(url.openStream());
            } else {
                reader = new StringReader(impl.getInlineSrc());
            }
            
            engine.eval(reader);

            return new InstanceWrapperBase(engine);

        } catch (ScriptException e) {
            throw new ObjectCreationException(e);
        } catch (IOException e) {
            throw new ObjectCreationException(e);
        }
    }

    // TODO: move all the rest to SPI extension 

    @Deprecated
    public Object createInstance() throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public Object getAssociatedTargetInstance() throws TargetResolutionException {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getTargetInstance() throws TargetResolutionException {
        // TODO Auto-generated method stub
        return null;
    }

    public void attachCallbackWire(Wire wire) {
        // TODO Auto-generated method stub

    }

    public void attachWire(Wire wire) {
        assert wire.getSourceUri().getFragment() != null;
        String referenceName = wire.getSourceUri().getFragment();
        List<Wire> wireList = wires.get(referenceName);
        if (wireList == null) {
            wireList = new ArrayList<Wire>();
            wires.put(referenceName, wireList);
        }
        wireList.add(wire);
//        Member member = referenceSites.get(referenceName);
//        if (member != null) {
//            injectors.add(createInjector(member, wire));
//        }
//        // cycle through constructor param names as well
//        for (int i = 0; i < constructorParamNames.size(); i++) {
//            if (referenceName.equals(constructorParamNames.get(i))) {
//                ObjectFactory[] initializerFactories = instanceFactory.getInitializerFactories();
//                initializerFactories[i] = createWireFactory(constructorParamTypes.get(i), wire);
//                break;
//            }
//        }
//        //TODO error if ref not set on constructor or ref site

    }


    public void attachWires(List<Wire> attachWires) {
        assert attachWires.size() > 0;
        assert attachWires.get(0).getSourceUri().getFragment() != null;
        String referenceName = attachWires.get(0).getSourceUri().getFragment();
        List<Wire> wireList = wires.get(referenceName);
        if (wireList == null) {
            wireList = new ArrayList<Wire>();
            wires.put(referenceName, wireList);
        }
        wireList.addAll(attachWires);
//        Member member = referenceSites.get(referenceName);
//        if (member == null) {
//            if (constructorParamNames.contains(referenceName)) {
//                // injected on the constructor
//                throw new UnsupportedOperationException();
//            } else {
//                throw new NoAccessorException(referenceName);
//            }
//        }
//
//        Class<?> type = attachWires.get(0).getSourceContract().getInterfaceClass();
//        if (type == null) {
//            throw new NoMultiplicityTypeException("Java interface must be specified for multiplicity", referenceName);
//        }
//        injectors.add(createMultiplicityInjector(member, type, wireList));

    }

    public List<Wire> getWires(String name) {
        return wires.get(name);
    }

    public TargetInvoker createTargetInvoker(String targetName, PhysicalOperationDefinition operation) {
        throw new UnsupportedOperationException();
    }

    protected <B> ObjectFactory<B> createWireFactory(Class<B> interfaze, Wire wire) {
        return new WireObjectFactory<B>(interfaze, wire, proxyService);
    }

}
