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

package org.apache.tuscany.sca.implementation.python.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.implementation.python.PythonEval;
import org.apache.tuscany.sca.implementation.python.PythonImplementation;
import org.apache.tuscany.sca.implementation.python.PythonProperty;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.core.PyTuple;
import org.python.util.PythonInterpreter;

/**
 * Implementation provider for Python component implementations.
 *
 * @version $Rev$ $Date$
 */
class PythonImplementationProvider implements ImplementationProvider {
    final RuntimeComponent component;
    final PythonImplementation implementation;
    PythonInterpreter python;
    PyObject callable;
    ProxyFactory pxFactory;
    
    PythonImplementationProvider(final RuntimeComponent comp, final PythonImplementation impl, ProxyFactory pxf) {
        component = comp;
        implementation = impl;
        pxFactory = pxf;
    }

    public void start() {
    	final PySystemState pss = new PySystemState();
    	pss.path.insert(0, new PyString(implementation.getLocation()));
    	pss.path.insert(0, new PyString(getClass().getProtectionDomain().getCodeSource().getLocation().getFile()));
    	python = new PythonInterpreter(null, pss);
    	python.exec("from invoker import *");
    	
    	final List<PyObject> px = new ArrayList<PyObject>();
    	for (final ComponentReference r: component.getReferences()) {
    		final PythonEval pe = pxFactory.createProxy(PythonEval.class, (RuntimeEndpointReference)r.getEndpointReferences().get(0));
            px.add(Py.java2py(new PythonEval() {
            	@Override
            	public String eval(final String args) throws Exception {
            		final String v = pe.eval(args); 
            		return v;
            	}
            }));
    	}
    	final List<PyObject> pr = new ArrayList<PyObject>();
    	for (final ComponentProperty p: component.getProperties()) {
    		final String v = String.valueOf(p.getValue()); 
            pr.add(Py.java2py(new PythonProperty() {
				@Override
				public String eval() {
            		return v;
				}
			}));
    	}

    	PyObject mkc = python.get("mkcomponent");
    	callable = mkc.__call__(new PyString(component.getName()), new PyString(implementation.getScript()), new PyTuple(px.toArray(new PyObject[0])), new PyTuple(pr.toArray(new PyObject[0])));
    }

    public void stop() {
    	python.cleanup();
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public Invoker createInvoker(final RuntimeComponentService s, final Operation op) {
        return new PythonInvoker(python, callable, op);
    }
}
