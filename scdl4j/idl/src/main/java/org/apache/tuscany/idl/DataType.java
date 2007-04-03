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
package org.apache.tuscany.idl;


/**
 * Representation of the type of data associated with an operation. Data is
 * represented in two forms: the physical form used by the runtime and a logical
 * form used by the assembly. The physical form is a Java Type because the
 * runtime is written in Java. This may be the same form used by the application
 * but it may not; for example, an application that is performing stream
 * processing may want a physical form such as an
 * {@link java.io.InputStream InputStream} to semantially operate on application
 * data such as a purchase order. The logical description is that used by the
 * assembly model and is an identifier into some well-known type space; examples
 * may be a Java type represented by its Class or an XML type represented by its
 * QName. Every data type may also contain metadata describing the expected
 * data; for example, it could specify a preferred data binding technology or
 * the size of a typical instance.
 * 
 * @version $Rev$ $Date$
 */
public interface DataType<L> extends Cloneable {
    /**
     * Set the java type for the data
     * @param cls
     */
    void setPhysical(Class cls);

    /**
     * Returns the physical type used by the runtime.
     * 
     * @return the physical type used by the runtime
     */
    Class getPhysical();

    /**
     * Returns the logical identifier used by the assembly. The type of this
     * value identifies the logical type system in use. Known values are:
     * <ul>
     * <li>a java.lang.reflect.Type identifies a Java type by name and
     * ClassLoader; this includes Java Classes as they are specializations of
     * Type</li>
     * <li>a javax.xml.namespace.QName identifies an XML type by local name and
     * namespace</li>
     * </ul>
     * 
     * @return the logical type name
     */
    L getLogical();

    /**
     * @return
     */
    String getDataBinding();

    /**
     * @param dataBinding the dataBinding to set
     */
    void setDataBinding(String dataBinding);

    /**
     * @return
     * @throws CloneNotSupportedException
     */
    Object clone() throws CloneNotSupportedException;

    /**
     * @param logical the logical to set
     */
    void setLogical(L logical);

}
