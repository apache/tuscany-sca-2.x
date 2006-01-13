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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import commonj.sdo.Type;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.ecore.impl.EOperationImpl;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.sdo.impl.DynamicEDataObjectImpl;
import org.eclipse.emf.ecore.sdo.util.SDOUtil;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.apache.tuscany.model.config.ModelConfiguration;
import org.apache.tuscany.model.util.ConfiguredResourceSet;
import org.apache.tuscany.model.assembly.AssemblyConstants;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.impl.AssemblyModelVisitorHelperImpl;
import org.apache.tuscany.model.types.OperationType;
import org.apache.tuscany.model.types.java.JavaInterfaceType;
import org.apache.tuscany.model.types.java.JavaMessageType;
import org.apache.tuscany.model.types.java.JavaOperationType;


/**
 *         <p/>
 *         An EMF package that represents a Java package
 */
public class JavaPackageImpl extends EPackageImpl {
    private AssemblyModelContext modelContext;
    private JavaReflector reflector;

    /**
     * An EMF EClass that represents a Java interface
     */
    private class JavaInterfaceTypeImpl extends EClassImpl implements JavaInterfaceType {

        private Map<String, OperationType> operationTypesMap;

        private Class javaClass;

        /**
         * Constructor
         */
        public JavaInterfaceTypeImpl(String className) {
            setName(className);
        }

        /**
         * @see org.apache.tuscany.model.types.java.JavaInterfaceType#getJavaClass()
         */
        public Class getJavaClass() {
            return javaClass;
        }

        /**
         * @see org.apache.tuscany.model.types.InterfaceType#getURI()
         */
        public String getURI() {
            // TODO Auto-generated method stub
            return null;
        }

        public List<OperationType> getOperationTypes() {
            return getEOperations();
        }

        public OperationType getOperationType(String name) {
            return operationTypesMap.get(name);
        }

        /**
         * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
         */
        public void initialize(AssemblyModelContext modelContext) {

            // Get the the Java class name and save it as an extended metadata annotation
            String className = getName();
            String javaClassName = className.substring(AssemblyConstants.JAVA_INTERFACE_NAME_PREFIX.length());
            ConfiguredResourceSet resourceSet = (ConfiguredResourceSet) JavaPackageImpl.this.eResource().getResourceSet();
            ExtendedMetaData extendedMetaData = resourceSet.getExtendedMetaData();
            extendedMetaData.setName(this, javaClassName);

            // Prefix with the Java package name
            if (JavaPackageImpl.this.getName() != null) {
                className = JavaPackageImpl.this.getName() + '.' + className.substring(AssemblyConstants.JAVA_INTERFACE_NAME_PREFIX.length());
            }

            // Load the class and set the instance class
            try {
                javaClass = reflector.getClassForName(className);
            } catch (Exception e) {
                throw new WrappedException(e);
            }
            Class instanceClass = javaClass;
            setInstanceClass(instanceClass);
            setInterface(true);

            // Introspect the Java interface and create the corresponding OperationTypes
            operationTypesMap = new HashMap<String, OperationType>();
            List<OperationType> operationTypes = getOperationTypes();
            Method[] methods = reflector.getMethods(javaClass);
            for (int m = 0; m < methods.length; m++) {
                OperationType operationType = new JavaOperationTypeImpl(methods[m]);
                operationTypes.add(operationType);
                operationType.initialize(modelContext);
                operationTypesMap.put(operationType.getName(), operationType);
            }
        }

        /**
         * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
         */
        public boolean accept(AssemblyModelVisitor visitor) {
            return AssemblyModelVisitorHelperImpl.accept(this, visitor);
        }

    }

    /**
     * An EMF EDataType that represents a Java type
     */
    private class JavaDataTypeImpl extends EDataTypeImpl {

        private Class javaType;

        /**
         * Constructor
         *
         * @param dataTypeName
         */
        private JavaDataTypeImpl(String dataTypeName) {

            try {
                if (JavaPackageImpl.this.getName() == null)
                    javaType = reflector.getClassForName(dataTypeName);
                else
                    javaType = reflector.getClassForName(JavaPackageImpl.this.getName() + '.' + dataTypeName);
            } catch (Exception e) {
                throw new WrappedException(e);
            }
            Class instanceClass = javaType;
            setInstanceClass(instanceClass);
            setName(dataTypeName);
            setSerializable(true);

            // Set the base type to our AnyJavaObjectType dataType
            // This will allow us to control the serialization of this type
            EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
            eAnnotation.setSource("http:///org/eclipse/emf/ecore/util/ExtendedMetaData");
            EMap theDetails = eAnnotation.getDetails();
            theDetails.put("baseType", "http://www.ibm.com/xmlns/prod/websphere/sca/message/any/6.0.0#AnyObjectType");
            getEAnnotations().add(eAnnotation);
        }
    }

    /**
     * An EMF EClass that represents a message
     */
    private class JavaMessageTypeImpl extends EClassImpl implements JavaMessageType {

        private Class[] parameterTypes;

        /**
         * Constructor
         *
         * @param messageName
         * @param parameterTypes
         */
        private JavaMessageTypeImpl(String messageName, Class[] parameterTypes) {
            setName(messageName);
            this.parameterTypes = parameterTypes;

            // Save the message name in an extended metadata annotation
            ConfiguredResourceSet resourceSet = (ConfiguredResourceSet) JavaPackageImpl.this.eResource().getResourceSet();
            ExtendedMetaData extendedMetaData = resourceSet.getExtendedMetaData();
            extendedMetaData.setName(this, messageName);

            // Create features for the given parameters
            for (int i = 0; i < this.parameterTypes.length; i++) {
                EClassifier classifier = getEClassifier(parameterTypes[i]);

                EStructuralFeature feature;
                if (classifier instanceof EDataType) {
                    EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
                    feature = attribute;
                    attribute.setName(AssemblyConstants.JAVA_ARG_NAME_PREFIX + i);
                    attribute.setEType(classifier);
                    super.getEStructuralFeatures().add(attribute);

                } else {
                    EReference reference = EcoreFactory.eINSTANCE.createEReference();
                    feature = reference;
                    reference.setContainment(true);
                    reference.setEType(classifier);
                    reference.setName(AssemblyConstants.JAVA_ARG_NAME_PREFIX + i);
                    super.getEStructuralFeatures().add(reference);
                }

                extendedMetaData.setFeatureKind(feature, ExtendedMetaData.ELEMENT_FEATURE);
            }
        }

        /**
         * @see org.apache.tuscany.model.types.java.JavaMessageType#getJavaParameterTypes()
         */
        public Class[] getJavaParameterTypes() {
            return parameterTypes;
        }

        /**
         * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
         */
        public void initialize(AssemblyModelContext modelContext) {
        }

        /**
         * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
         */
        public boolean accept(AssemblyModelVisitor visitor) {
            return AssemblyModelVisitorHelperImpl.accept(this, visitor);
        }
    }

    /**
     * An EOperation that represents a Java method
     */
    private class JavaOperationTypeImpl extends EOperationImpl implements JavaOperationType {

        private Method method;
        private Type inputType;
        private Type outputType;
        private List<Type> exceptionTypes = new ArrayList<Type>();

        /**
         * Constructor
         *
         * @param method
         */
        private JavaOperationTypeImpl(Method method) {
            this.method = method;
        }

        public Method getJavaMethod() {
            return method;
        }

        /**
         * @see org.apache.tuscany.model.types.OperationType#getInputType()
         */
        public Type getInputType() {
            return inputType;
        }

        /**
         * @see org.apache.tuscany.model.types.OperationType#getExceptionTypes()
         */
        public List<Type> getExceptionTypes() {
            return exceptionTypes;
        }

        /**
         * @see org.apache.tuscany.model.types.OperationType#getOutputType()
         */
        public Type getOutputType() {
            return outputType;
        }

        /**
         * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
         */
        public void initialize(AssemblyModelContext modelContext) {

            // Set the EOperation name
            setName(reflector.getMethodName(this.method));

            // Initialize the input type
            Class[] parameterTypes = reflector.getMethodParameterTypes(method);
            EParameter eParameter = EcoreFactory.eINSTANCE.createEParameter();
            eParameter.setName(getName());
            EClassifier inputClassifier = new JavaMessageTypeImpl(AssemblyConstants.JAVA_MESSAGE_NAME_PREFIX + getEContainingClass().getName() + '.' + getName(), parameterTypes);
            EPackage containingEPackage = getEContainingClass().getEPackage();
            containingEPackage.getEClassifiers().add(inputClassifier);
            eParameter.setEType(inputClassifier);
            super.getEParameters().add(eParameter);
            inputType = SDOUtil.adaptType(inputClassifier);

            // Initialize the output type
            Class returnType = reflector.getMethodReturnType(method);
            if (returnType != null && returnType != void.class && !"void".equals(returnType)) {
                EClassifier outputClassifier = new JavaMessageTypeImpl(AssemblyConstants.JAVA_MESSAGE_NAME_PREFIX + getEContainingClass().getName() + '.' + getName() + ".return", new Class[]{returnType});
                containingEPackage.getEClassifiers().add(outputClassifier);
                super.setEType(outputClassifier);
                outputType = SDOUtil.adaptType(outputClassifier);
            }

            // Initialize the exception types
            Class[] excTypes = reflector.getMethodExceptionTypes(method);
            for (int i = 0; i < excTypes.length; i++) {
                EClassifier exceptionClassifier = getEClassifier(excTypes[i]);
                super.getEExceptions().add(exceptionClassifier);
                exceptionTypes.add(SDOUtil.adaptType(exceptionClassifier));
            }
        }

        /**
         * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
         */
        public boolean accept(AssemblyModelVisitor visitor) {
            return AssemblyModelVisitorHelperImpl.accept(this, visitor);
        }

    }

    private class JavaDataObjectFactoryImpl extends DynamicEDataObjectImpl.FactoryImpl {

        /**
         * @see org.eclipse.emf.ecore.EFactory#convertToString(org.eclipse.emf.ecore.EDataType, java.lang.Object)
         */
        public String convertToString(EDataType eDataType, Object objectValue) {
            if (!(eDataType instanceof JavaDataTypeImpl))
                return super.convertToString(eDataType, objectValue);

            if (objectValue != null) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(objectValue);
                    byte[] bytes = bos.toByteArray();

                    return EcoreUtil.convertToString(XMLTypePackage.eINSTANCE.getBase64Binary(), bytes);
                } catch (IOException e) {
                    // FIXME throw better exceptions
                    throw new WrappedException(e);
                }
            } else {
                return null;
            }
        }

        /**
         * @see org.eclipse.emf.ecore.EFactory#createFromString(org.eclipse.emf.ecore.EDataType, java.lang.String)
         */
        public Object createFromString(EDataType eDataType, String stringValue) {
            if (!(eDataType instanceof JavaDataTypeImpl))
                return super.createFromString(eDataType, stringValue);

            if (stringValue != null) {
                byte[] bytes = (byte[]) EcoreUtil.createFromString(XMLTypePackage.eINSTANCE.getBase64Binary(), stringValue);
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes)) {
                        protected Class resolveClass(java.io.ObjectStreamClass desc) {
                            try {
                                return reflector.getClassForName(desc.getName());
                            } catch (Exception e) {
                                // FIXME throw better exceptions
                                throw new WrappedException(e);
                            }
                        }
                    };

                    return ois.readObject();

                } catch (Exception e) {
                    // FIXME throw better exceptions
                    throw new WrappedException(e);
                }
            } else {
                return null;
            }
        }
    }

    /**
     * Constructor
     */
    public JavaPackageImpl(AssemblyModelContext modelContext, String packageName) {
        super();
        this.modelContext = modelContext;

        // Initialize the package
        String namespace = AssemblyConstants.JAVA_PROTOCOL + packageName;
        setNsURI(namespace);
        setName(packageName);
        setNsPrefix(getNSPrefixFromPackageName(packageName));

        // Set the SDO factory so that we can create SDO types from the EClasses in
        // this package
        setEFactoryInstance(new JavaDataObjectFactoryImpl());
    }

    /**
     * Returns a namespace prefix for the given package name.
     *
     * @param packageName
     */
    private static String getNSPrefixFromPackageName(String packageName) {
        if (packageName != null) {
            int index = packageName.lastIndexOf('.');
            return index == -1 ? packageName : packageName.substring(index + 1);
        } else {
            return null;
        }
    }

    /**
     * @see org.eclipse.emf.ecore.impl.EPackageImpl#getEClassifier(java.lang.String)
     */
    public EClassifier getEClassifier(String name) {
        EClassifier eClassifier = super.getEClassifier(name);
        if (eClassifier == null) {

            // Create a reflector for the current bundle context
            if (reflector == null) {
                reflector = new JavaReflectorImpl(modelContext.getResourceLoader());
            }

            // Get a classifier for a component interface
            if (name.startsWith(AssemblyConstants.JAVA_INTERFACE_NAME_PREFIX)) {
                eClassifier = new JavaInterfaceTypeImpl(name);

            } else if (name.startsWith(AssemblyConstants.JAVA_MESSAGE_NAME_PREFIX)) {

                // Get a classifier for a message
                String[] segments = name.split(".");
                String className = segments[2];
                String operationName = segments[3];
                EClass eClass = (EClass) getEClassifier(AssemblyConstants.JAVA_INTERFACE_NAME_PREFIX + className);
                if (eClass != null) {
                    for (Iterator i = eClass.getEOperations().iterator(); i.hasNext();) {
                        EOperation eOperation = (EOperation) i.next();
                        if (operationName.equals(eOperation.getName())) {
                            eOperation.getEParameters();
                            eOperation.getEType();
                            eOperation.getEExceptions();
                            break;
                        }
                    }
                }

            } else {

                // Get a classifier for a data type
                eClassifier = new JavaDataTypeImpl(name);
            }

            // Add the classifier to the package
            if (eClassifier != null) {
                super.getEClassifiers().add(eClassifier);

                if (eClassifier instanceof AssemblyModelObject) {
                    ((AssemblyModelObject) eClassifier).initialize(modelContext);
                }


                return eClassifier;
            }
        }

        return super.getEClassifier(name);
    }

    /**
     * Get an EDataType for the given Java type
     *
     * @param type
     */
    private EClassifier getEClassifier(Class type) {

        // First look for a builtin type matching the java type
        EDataType dataType = modelContext.getJavaTypeHelper().getBuiltinDataType(type);

        if (dataType == null) {

            // If the given type extends commonj.sdo.DataObject then look for the corresponding SDO
            // Type
            if (reflector.isDataObjectClass(type)) {
                    ConfiguredResourceSet resourceSet = (ConfiguredResourceSet) eResource().getResourceSet();
                    ModelConfiguration modelConfiguration = resourceSet.getModelConfiguration();
                    EClass generatedEClass = modelConfiguration.getEClass(type);
                    if (generatedEClass != null)
                        return generatedEClass;
                return XMLTypePackage.eINSTANCE.getAnyType();
            }

            // This is not DataObject type, we need to reflect the java type

            // Look for the package containing this java type
            String packageName = reflector.getPackageName(type);
            String namespace = AssemblyConstants.JAVA_PROTOCOL + packageName;
            EPackage ePackage = eResource().getResourceSet().getPackageRegistry().getEPackage(namespace);
            if (ePackage == null) {

                // Create a new EPackage if necessary
                ePackage = new JavaPackageImpl(modelContext, packageName);
            }

            // Look up the datatype in the ePackage
            String dataTypeName = reflector.getClassName(type);
            dataType = (EDataType) ePackage.getEClassifier(dataTypeName);
        }
        return dataType;
    }

    /**
     * @see org.eclipse.emf.ecore.impl.EPackageImpl#freeze()
     */
    public void freeze() {
		super.freeze();
	}
	
}