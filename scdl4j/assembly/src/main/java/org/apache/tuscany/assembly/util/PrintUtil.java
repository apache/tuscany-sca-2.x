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
package org.apache.tuscany.assembly.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple print utility class to help print assembly model instances.
 * 
 * @version $Rev$ $Date$
 */
public class PrintUtil {

    PrintWriter out;
    Set<Object> objects = new HashSet<Object>();
    int indent;

    public PrintUtil(OutputStream out) {
        this.out = new PrintWriter(new OutputStreamWriter(out), true);
    }

    void indent() {
        for (int i = 0; i < indent; i++) {
            out.print("  ");
        }
    }

    /**
     * Print a model object.
     * 
     * @param object
     */
    public void print(Object object) {
        if (objects.contains(object)) {

            // If we've already printed an object, print just it's hashcode
            indent();
            out.println(object.getClass().getName() + "@" + System.identityHashCode(object));
        } else {
            objects.add(object);
            try {

                // Print the object class name
                indent();
                out.println(object.getClass().getSimpleName() + " {");

                // Get the object's properties
                BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
                for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                    try {

                        // Get the value of each property
                        Object value = propertyDescriptor.getReadMethod().invoke(object);
                        if (value != null) {

                            // Convert array value into a list
                            if (value.getClass().isArray()) {
                                value = Arrays.asList((Object[])value);
                            }

                            // Print elements in a list
                            if (value instanceof List) {
                                if (!((List)value).isEmpty()) {
                                    indent++;
                                    indent();
                                    out.println(propertyDescriptor.getName() + "= [");

                                    // Print each element, recursively
                                    for (Object element : (List)value) {
                                        indent++;
                                        print(element);
                                        indent--;
                                    }
                                    indent();
                                    out.println(" ]");
                                    indent--;
                                }
                            } else {
                                Class<?> valueClass = value.getClass();

                                // Print a primitive, java built in type or
                                // enum, using toString()
                                if (valueClass.isPrimitive() || valueClass.getName().startsWith("java.")
                                    || valueClass.getName().startsWith("javax.")
                                    || valueClass.isEnum()) {
                                    if (!propertyDescriptor.getName().equals("class")) {
                                        if (!(Boolean.FALSE.equals(value))) {
                                            indent++;
                                            indent();
                                            out.println(propertyDescriptor.getName() + "=" + value.toString());
                                            indent--;
                                        }
                                    }
                                } else {

                                    // Print an object, recursively
                                    indent++;
                                    indent();
                                    out.println(propertyDescriptor.getName() + "= {");
                                    indent++;
                                    print(value);
                                    indent--;
                                    indent();
                                    out.println("}");
                                    indent--;
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                indent();
                out.println("}");
            } catch (IntrospectionException e) {
                indent();
                out.println(e);
            }
        }
    }

}
