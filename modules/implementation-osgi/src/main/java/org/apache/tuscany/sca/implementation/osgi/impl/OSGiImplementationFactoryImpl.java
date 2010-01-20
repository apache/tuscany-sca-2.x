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

package org.apache.tuscany.sca.implementation.osgi.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementationFactory;
import org.apache.tuscany.sca.implementation.osgi.OSGiProperty;
import org.osgi.framework.ServiceReference;

/**
 * 
 */
public class OSGiImplementationFactoryImpl implements OSGiImplementationFactory {
    public OSGiImplementationFactoryImpl(ExtensionPointRegistry registry) {
        super();
    }

    public OSGiImplementation createOSGiImplementation() {
        return new OSGiImplementationImpl();
    }

    public OSGiProperty createOSGiProperty() {
        return new OSGiPropertyImpl();
    }

    public OSGiProperty createOSGiProperty(String propName, String propValue, String propType) {
        OSGiProperty prop = new OSGiPropertyImpl();
        if (propType == null) {
            propType = "String";
        }
        prop.setName(propName);
        prop.setStringValue(propValue);
        prop.setType(propType);

        Object value = propValue;
        if ("Integer".equals(propType)) {
            value = Integer.valueOf(propValue);
        } else if ("Long".equals(propType)) {
            value = Long.valueOf(propValue);
        } else if ("Float".equals(propType)) {
            value = Float.valueOf(propValue);
        } else if ("Double".equals(propType)) {
            value = Double.valueOf(propValue);
        } else if ("Short".equals(propType)) {
            value = Short.valueOf(propValue);
        } else if ("Character".equals(propType)) {
            value = propValue.charAt(0);
        } else if ("Byte".equals(propType)) {
            value = Byte.valueOf(propValue);
        } else if ("Boolean".equals(propType)) {
            value = Boolean.valueOf(propValue);
        } else if ("String+".equals(propType)) {
            value = propValue.split(" ");
        } else {
            // String
            value = propValue;
        }
        prop.setValue(value);
        return prop;
    }

    public OSGiProperty createOSGiProperty(String propName, Object value) {
        OSGiProperty prop = new OSGiPropertyImpl();
        prop.setName(propName);
        prop.setValue(value);

        if (value instanceof String[]) {
            StringBuffer sb = new StringBuffer();
            for (String s : (String[])value) {
                sb.append(s).append(' ');
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            prop.setStringValue(sb.toString());
            prop.setType("String+");
        } else if (value != null) {
            prop.setStringValue(String.valueOf(value));
            prop.setType(value.getClass().getSimpleName());
        }
        return prop;
    }
    
    public Collection<OSGiProperty> createOSGiProperties(ServiceReference reference) {
        List<OSGiProperty> props = new ArrayList<OSGiProperty>();
        for(String key: reference.getPropertyKeys()) {
            Object value = reference.getProperty(key);
            OSGiProperty prop = createOSGiProperty(key, value);
            props.add(prop);
        }
        return props;
    }        

    public Collection<OSGiProperty> createOSGiProperties(Map<String, Object> properties) {
        List<OSGiProperty> props = new ArrayList<OSGiProperty>();
        for (Map.Entry<String, Object> e : properties.entrySet()) {
            OSGiProperty prop = createOSGiProperty(e.getKey(), e.getValue());
            props.add(prop);
        }
        return props;
    }
}
