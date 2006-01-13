/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.types.java.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.sdo.SDOPackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.apache.tuscany.model.assembly.AssemblyConstants;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.types.java.JavaInterfaceType;
import org.apache.tuscany.model.types.java.JavaTypeHelper;

/**
 */
public class JavaTypeHelperImpl implements JavaTypeHelper {

    private static Map javaToSDOBuiltinTypes;

    static {
        javaToSDOBuiltinTypes = new HashMap();
        javaToSDOBuiltinTypes.put(byte[].class, XMLTypePackage.eINSTANCE.getBase64Binary());
        javaToSDOBuiltinTypes.put(boolean.class, XMLTypePackage.eINSTANCE.getBoolean());
        javaToSDOBuiltinTypes.put(Boolean.class, XMLTypePackage.eINSTANCE.getBooleanObject());
        javaToSDOBuiltinTypes.put(byte.class, XMLTypePackage.eINSTANCE.getByte());
        javaToSDOBuiltinTypes.put(Byte.class, XMLTypePackage.eINSTANCE.getByteObject());
        javaToSDOBuiltinTypes.put(Date.class, XMLTypePackage.eINSTANCE.getDateTime());
        javaToSDOBuiltinTypes.put(BigDecimal.class, XMLTypePackage.eINSTANCE.getDecimal());
        javaToSDOBuiltinTypes.put(double.class, XMLTypePackage.eINSTANCE.getDouble());
        javaToSDOBuiltinTypes.put(Double.class, XMLTypePackage.eINSTANCE.getDoubleObject());
        javaToSDOBuiltinTypes.put(float.class, XMLTypePackage.eINSTANCE.getFloat());
        javaToSDOBuiltinTypes.put(Float.class, XMLTypePackage.eINSTANCE.getFloatObject());
        javaToSDOBuiltinTypes.put(int.class, XMLTypePackage.eINSTANCE.getInt());
        javaToSDOBuiltinTypes.put(Integer.class, XMLTypePackage.eINSTANCE.getIntObject());
        javaToSDOBuiltinTypes.put(BigInteger.class, XMLTypePackage.eINSTANCE.getInteger());
        javaToSDOBuiltinTypes.put(long.class, XMLTypePackage.eINSTANCE.getLong());
        javaToSDOBuiltinTypes.put(Long.class, XMLTypePackage.eINSTANCE.getLongObject());
        javaToSDOBuiltinTypes.put(String.class, XMLTypePackage.eINSTANCE.getString());
        javaToSDOBuiltinTypes.put(short.class, XMLTypePackage.eINSTANCE.getShort());
        javaToSDOBuiltinTypes.put(Short.class, XMLTypePackage.eINSTANCE.getShortObject());
        javaToSDOBuiltinTypes.put(char.class, SDOPackage.eINSTANCE.getEDataObjectSimpleAnyType());
        javaToSDOBuiltinTypes.put(Character.class, SDOPackage.eINSTANCE.getEDataObjectSimpleAnyType());
    }

    private static Map sdoToJavaBuiltinTypes;

    static {
        sdoToJavaBuiltinTypes = new HashMap();
        for (Iterator i = XMLTypePackage.eINSTANCE.getEClassifiers().iterator(); i.hasNext();) {
            EClassifier classifier = (EClassifier) i.next();
            sdoToJavaBuiltinTypes.put(classifier, classifier.getInstanceClass());
        }
    }

    private AssemblyModelContext modelContext;

    /**
     * Constructor
     */
    public JavaTypeHelperImpl(AssemblyModelContext modelContext) {
        super();
        this.modelContext = modelContext;
    }

    /**
     * Returns an EClassifier for a Java interface
     *
     * @param fullyQualifiedClassName
     * @return
     */
    public JavaInterfaceType getJavaInterfaceType(String fullyQualifiedClassName) {

        // Split package name and class name
        int index = fullyQualifiedClassName.lastIndexOf('.');
        String className;
        String packageName;
        if (index == -1) {
            packageName = AssemblyConstants.DEFAULT_JAVA_PACKAGE_NAME;
            className = fullyQualifiedClassName;
        } else {
            packageName = fullyQualifiedClassName.substring(0, index);
            className = fullyQualifiedClassName.substring(index + 1);
        }

        // Lookup the specified EPackage
        String namespace = AssemblyConstants.JAVA_PROTOCOL + packageName;
        ResourceSet resourceSet = (ResourceSet) modelContext.getAssemblyLoader();
        Resource resource = resourceSet.getResource(URI.createURI(namespace), true);
        EPackage ePackage = (EPackage) resource.getContents().get(0);

        // Lookup the specified classifier
        JavaInterfaceType interfaceType = (JavaInterfaceType) ePackage.getEClassifier(AssemblyConstants.JAVA_INTERFACE_NAME_PREFIX + className);
        return interfaceType;
    }

    /**
     * Returns an EDataType for a Java type
     *
     * @param fullyQualifiedClassName
     * @return
     */
    public EDataType getDataType(String fullyQualifiedClassName) {

        // Split package name and class name
        int index = fullyQualifiedClassName.lastIndexOf('.');
        String className;
        String packageName;
        if (index == -1) {
            packageName = "default";
            className = fullyQualifiedClassName;
        } else {
            packageName = fullyQualifiedClassName.substring(0, index);
            className = fullyQualifiedClassName.substring(index + 1);
        }

        // Lookup the specified EPackage
        String namespace = AssemblyConstants.JAVA_PROTOCOL + packageName;
        ResourceSet resourceSet = (ResourceSet) modelContext.getAssemblyLoader();
        Resource resource = resourceSet.getResource(URI.createURI(namespace), true);
        EPackage ePackage = (EPackage) resource.getContents().get(0);

        // Lookup the specified data type
        EDataType eDataType = (EDataType) ePackage.getEClassifier(className);
        return eDataType;
    }

    /**
     * Returns the SDO Type from a Java type.
     *
     * @return
     */
    public EDataType getBuiltinDataType(Class javaType) {
        return (EDataType) javaToSDOBuiltinTypes.get(javaType);
    }

    /**
     * Returns the Java type from an SDO type.
     * @return
     */
    public Class getBuiltinJavaType(EDataType dataType) {
        return (Class) sdoToJavaBuiltinTypes.get(dataType);
	}

}