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
package org.apache.tuscany.sca.assembly.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;

/**
 * A simple print utility class to help print model instances.
 * 
 * @version $Rev$ $Date$
 */
public class PrintUtil {
    private boolean useGetters = false;
    private PrintWriter out;
    private int indent;

    public PrintUtil(PrintWriter out, boolean useGetters) {
        this.out = out;
        this.useGetters = useGetters;
    }

    public PrintUtil(OutputStream out) {
        this.out = new PrintWriter(new OutputStreamWriter(out), true);
    }

    void indent() {
        for (int i = 0; i < indent; i++) {
            out.print("  ");
        }
    }

    /**
     * Print an object.
     * 
     * @param object
     */
    public void print(Object object) {
        Set<Integer> objects = new HashSet<Integer>();
        print(object, objects);
    }

    /**
     * Print an object.
     * 
     * @param object
     */
    private void print(Object object, Set<Integer> printed) {
        if (object == null) {
            return;
        }
        int id = System.identityHashCode(object);
        if (printed.contains(id)) {

            // If we've already printed an object, print just it's hashcode
            indent();
            out.println(object.getClass().getName() + "@" + id);
        } else {
            printed.add(id);
            try {

                // Print the object class name
                indent();
                out.println(object.getClass().getSimpleName() + " {");

                // Get the object's properties
                ValueAccessor accessor = useGetters ? new PropertyAccessor(object) : new FieldAccessor(object);
                for (int i = 0; i < accessor.size(); i++) {
                    try {

                        // Get the value of each property
                        Object value = accessor.getValue(i);
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
                                    out.println(accessor.getName(i) + "= [");

                                    // Print each element, recursively
                                    for (Object element : (List)value) {
                                        indent++;
                                        print(element, printed);
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
                                    if (!accessor.getName(i).equals("class")) {
                                        if (!(Boolean.FALSE.equals(value))) {
                                            indent++;
                                            indent();
                                            out.println(accessor.getName(i) + "=" + value.toString());
                                            indent--;
                                        }
                                    }
                                } else if (value instanceof Node) {
                                    indent++;
                                    indent();
                                    out.println(accessor.getName(i) + "=" + value.toString());
                                    indent--;
                                } else {

                                    // Print an object, recursively
                                    indent++;
                                    indent();
                                    out.println(accessor.getName(i) + "= {");
                                    indent++;
                                    print(value, printed);
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
            } catch (Exception e) {
                indent();
                out.println(e);
            }
        }
    }

    public static interface ValueAccessor {
        int size();

        String getName(int i);

        Object getValue(int i) throws Exception;
    }

    /**
     * Java field reflection based value accessor
     */
    private static class FieldAccessor implements ValueAccessor {

        private Object object;
        private List<Field> fields;

        public FieldAccessor(Object object) {
            this.fields = getAllFields(object.getClass());
            this.object = object;
        }

        public String getName(int i) {
            return fields.get(i).getName();
        }

        public Object getValue(int i) throws Exception {
            return fields.get(i).get(object);
        }

        public int size() {
            return fields.size();
        }

    }

    /**
     * JavaBean-based value accessor
     */
    private static class PropertyAccessor implements ValueAccessor {

        private Object object;
        private PropertyDescriptor[] fields;

        public PropertyAccessor(Object object) throws IntrospectionException {
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
            this.fields = beanInfo.getPropertyDescriptors();
            this.object = object;
        }

        public String getName(int i) {
            return fields[i].getName();
        }

        public Object getValue(int i) throws Exception {
            Method getter = fields[i].getReadMethod();
            if (getter != null) {
                return getter.invoke(object);
            }
            return null;
        }

        public int size() {
            return fields.length;
        }

    }

    /**
     * Returns a collection of fields declared by a class
     * or one of its supertypes
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        return getAllFields(clazz, new ArrayList<Field>());
    }

    /**
     * Recursively evaluates the type hierachy to return all fields 
     */
    private static List<Field> getAllFields(Class<?> clazz, List<Field> fields) {
        if (clazz == null || clazz.isArray() || Object.class.equals(clazz)) {
            return fields;
        }
        fields = getAllFields(clazz.getSuperclass(), fields);
        Field[] declaredFields = clazz.getDeclaredFields();
        for (final Field field : declaredFields) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    field.setAccessible(true); // ignore Java accessibility
                    return null;
                }
            });
            fields.add(field);
        }
        return fields;
    }

}
