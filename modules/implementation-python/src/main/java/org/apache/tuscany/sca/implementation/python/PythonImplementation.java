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
package org.apache.tuscany.sca.implementation.python;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.impl.ImplementationImpl;
import org.apache.tuscany.sca.assembly.impl.PropertyImpl;
import org.apache.tuscany.sca.assembly.impl.ReferenceImpl;
import org.apache.tuscany.sca.assembly.impl.ServiceImpl;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * The model representing a Python implementation in an SCA assembly.
 * 
 * @version $Rev$ $Date$
 */
public class PythonImplementation extends ImplementationImpl {
    final String script;
    final String location;
    final InterfaceContract contract;
    final Service service;

    PythonImplementation(final QName qn, final String scr, final String loc, final InterfaceContract c) {
        super(qn);
        script = scr;
        location = loc;
        contract = c;

        class DynService extends ServiceImpl {
        	public DynService() {
        		setName("default");
        		setInterfaceContract(contract);
			}
        };
        service = new DynService();
        getServices().add(service);
    }

    public String getScript() {
        return script;
    }
    
    public String getLocation() {
		return location;
	}

    public Service getService(final String n) {
    	return service;
    }  

    public Reference getReference(final String n) {
    	final Reference r = super.getReference(n);
    	if (r != null)
    		return r;
    	class DynReference extends ReferenceImpl {
    		public DynReference() {
    	    	setName(n);
    	    	setInterfaceContract(contract);
			}
    	}
    	final Reference nr = new DynReference();
    	getReferences().add(nr);
    	return nr;
    }
    
    public Property getProperty(final String n) {
    	final Property p = super.getProperty(n);
    	if (p != null)
    		return p;
    	class DynProperty extends PropertyImpl {
    		public DynProperty() {
    	    	setName(n);
    	    	setDataType(new DataTypeImpl<XMLType>(null, String.class, String.class, XMLType.UNKNOWN));
    	    	setXSDType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
			}
    	}
    	final Property np = new DynProperty();
    	getProperties().add(np);
    	return np;
    }
}
