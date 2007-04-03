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
package org.apache.tuscany.tools.java2wsdl.generate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tuscany.sdo.util.DataObjectUtil;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaForm;
import org.apache.ws.commons.schema.XmlSchemaImport;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.utils.NamespaceMap;
import org.apache.ws.java2wsdl.Java2WSDLConstants;
import org.apache.ws.java2wsdl.SchemaGenerator;
import org.apache.ws.java2wsdl.bytecode.MethodTable;
import org.codehaus.jam.JClass;
import org.codehaus.jam.JMethod;
import org.codehaus.jam.JParameter;
import org.codehaus.jam.JamClassIterator;
import org.codehaus.jam.JamService;
import org.codehaus.jam.JamServiceFactory;
import org.codehaus.jam.JamServiceParams;

public class TuscanyWSDLTypesGenerator implements TuscanyJava2WSDLConstants {
    public static final String NAME_SPACE_PREFIX = "stn_";

    public static final String PERIOD_SEPARATOR = ".";

    private static int prefixCount = 1;

    protected GenerationParameters generationParams;

    protected Hashtable targetNamespacePrefixMap = new Hashtable();

    protected Hashtable schemaMap = new Hashtable();

    protected XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();

    private TuscanyTypeTable typeTable = new TuscanyTypeTable();

    protected SchemaBuilder schemaBuilder = null;

    protected Map schemaLocationMap = null;

    private static final Log log = LogFactory.getLog(SchemaGenerator.class);

    // to keep loadded method using JAM
    private JMethod methods[];

    //to store byte code method using Axis 1.x codes
    private MethodTable methodTable;

    private Class clazz;

    private ArrayList excludeMethods = new ArrayList();

    public TuscanyWSDLTypesGenerator(GenerationParameters genParams) throws Exception {
        DataObjectUtil.initRuntime();
        this.generationParams = genParams;

        clazz = Class.forName(generationParams.getSourceClassName(),
                              true,
                              generationParams.getClassLoader());
        methodTable = new MethodTable(clazz);

        initializeSchemaMap(generationParams.getSchemaTargetNamespace(),
                            generationParams.getSchemaTargetNamespacePrefix());
        schemaBuilder = new SchemaBuilder(xmlSchemaCollection,
                                          schemaMap,
                                          targetNamespacePrefixMap,
                                          typeTable,
                                          generationParams.getAttrFormDefault(),
                                          generationParams.getElementFormDefault(),
                                          generationParams.getSchemaLocationMap(),
                                          generationParams.getClassLoader());
    }

    /**
     * Generates schema for all the parameters in method. First generates schema
     * for all different parameter type and later refers to them.
     *
     * @return Returns XmlSchema.
     * @throws Exception
     */
    public Collection buildWSDLTypes() throws Exception {
        JamServiceFactory factory = JamServiceFactory.getInstance();
        JamServiceParams jam_service_parms = factory.createServiceParams();
        //setting the classLoder
//        jam_service_parms.setParentClassLoader(factory.createJamClassLoader(classLoader));
        //it can posible to add the classLoader as well
        jam_service_parms.addClassLoader(generationParams.getClassLoader());
        jam_service_parms.includeClass(generationParams.getSourceClassName());
        
        for ( int count = 0 ; count < generationParams.getExtraClasses().size() ; ++count )
        {
            jam_service_parms.includeClass((String)generationParams.getExtraClasses().get(count));
        }
        
        JamService service = factory.createService(jam_service_parms);
        QName extraSchemaTypeName = null;
        JamClassIterator jClassIter = service.getClasses();
        //all most all the time the ittr will have only one class in it
        while (jClassIter.hasNext()) {
            JClass jclass = (JClass) jClassIter.next();
            // serviceName = jclass.getSimpleName();
            //todo in the future , when we support annotation we can use this
            //JAnnotation[] annotations = jclass.getAnnotations();
            
            if ( jclass.getQualifiedName().equals(generationParams.getSourceClassName()) )
            {
                /**
                 * Schema genertaion done in two stage 1. Load all the methods and
                 * create type for methods parameters (if the parameters are Bean
                 * then it will create Complex types for those , and if the
                 * parameters are simple type which decribe in SimpleTypeTable
                 * nothing will happen) 2. In the next stage for all the methods
                 * messages and port types will be creteated
                 */
                methods = jclass.getDeclaredMethods();
                //short the elements in the array
                Arrays.sort(methods);
    
                // since we do not support overload
                HashMap uniqueMethods = new HashMap();
                XmlSchemaComplexType methodSchemaType;
                XmlSchemaSequence sequence = null;
    
                for (int i = 0; i < methods.length; i++) {
                    JMethod jMethod = methods[i];
                    
                    String methodName = methods[i].getSimpleName();
                    // no need to think abt this method , since that is system
                    // config method
                    if (excludeMethods.contains(jMethod.getSimpleName())) {
                        continue;
                    }
    
                    if (uniqueMethods.get(jMethod.getSimpleName()) != null) {
                        throw new Exception(
                                " Sorry we don't support methods overloading !!!! ");
                    }
    
                    if (!jMethod.isPublic()) {
                        // no need to generate Schema for non public methods
                        continue;
                    }
                    uniqueMethods.put(jMethod.getSimpleName(), jMethod);
                    //create the schema type for the method wrapper
    
                    uniqueMethods.put(jMethod.getSimpleName(), jMethod);
                    JParameter [] paras = jMethod.getParameters();
                    String parameterNames [] = null;
                    if (paras.length > 0) {
                        parameterNames = methodTable.getParameterNames(methodName);
                        sequence = new XmlSchemaSequence();
    
                        methodSchemaType = createSchemaTypeForMethodPart(jMethod.getSimpleName());
                        methodSchemaType.setParticle(sequence);
                    }
    
                    for (int j = 0; j < paras.length; j++) {
                        JParameter methodParameter = paras[j];
                        JClass paraType = methodParameter.getType();
                        generateSchemaForType(sequence, paraType,
                                (parameterNames != null && parameterNames[j] != null) ? parameterNames[j] : methodParameter.getSimpleName());
                    }
                    // for its return type
                    JClass returnType = jMethod.getReturnType();
    
                    if (!returnType.isVoidType()) {
                        methodSchemaType = createSchemaTypeForMethodPart(jMethod.getSimpleName() + RESPONSE);
                        sequence = new XmlSchemaSequence();
                        methodSchemaType.setParticle(sequence);
                        generateSchemaForType(sequence, returnType, "return");
                    }
                }
            }
            else
            {
                //generate the schema type for extra classes
                extraSchemaTypeName = typeTable.getSimpleSchemaTypeName(jclass.getQualifiedName());
                if (extraSchemaTypeName == null) 
                {
                    extraSchemaTypeName = schemaBuilder.generateSchema(jclass);
                }
            }
        }
        return schemaMap.values();
    }

    private QName generateSchemaForType(XmlSchemaSequence sequence, JClass type, String partName) throws Exception {
        boolean isArrayType = type.isArrayType();
        if (isArrayType) {
            type = type.getArrayComponentType();
        }

        String classTypeName = type.getQualifiedName();

        QName schemaTypeName = typeTable.getSimpleSchemaTypeName(classTypeName);
        if (schemaTypeName == null) {
            schemaTypeName = schemaBuilder.generateSchema(type);
            addContentToMethodSchemaType(sequence,
                                         schemaTypeName,
                                         partName,
                                         type.isArrayType());
            addImportORInclude((XmlSchema) schemaMap.get(generationParams.getSchemaTargetNamespace()),
                               schemaTypeName);

        } else {
            addContentToMethodSchemaType(sequence,
                                         schemaTypeName,
                                         partName,
                                         type.isArrayType());
        }

        return schemaTypeName;
    }

    private void addContentToMethodSchemaType(XmlSchemaSequence sequence,
                                              QName schemaTypeName,
                                              String paraName,
                                              boolean isArray) {
        XmlSchemaElement elt1 = new XmlSchemaElement();
        elt1.setName(paraName);
        elt1.setSchemaTypeName(schemaTypeName);
        sequence.getItems().add(elt1);

        if (isArray) {
            elt1.setMaxOccurs(Long.MAX_VALUE);
            elt1.setMinOccurs(0);
        }
    }

    private XmlSchemaComplexType createSchemaTypeForMethodPart(String localPartName) throws Exception {
        XmlSchema xmlSchema = (XmlSchema) schemaMap.get(generationParams.getSchemaTargetNamespace());
        QName elementName = new QName(generationParams.getSchemaTargetNamespace(),
                                      localPartName,
                                      generationParams.getSchemaTargetNamespacePrefix());
        XmlSchemaComplexType complexType = new XmlSchemaComplexType(xmlSchema);

        XmlSchemaElement globalElement = new XmlSchemaElement();
        globalElement.setSchemaType(complexType);
        globalElement.setName(formGlobalElementName(localPartName));
        globalElement.setQName(elementName);

        xmlSchema.getItems().add(globalElement);
        xmlSchema.getElements().add(elementName,
                                    globalElement);

        typeTable.addComplexSchemaType(generationParams.getSchemaTargetNamespace(),
                                       globalElement.getName(),
                                       elementName);

        return complexType;
    }

    private String formGlobalElementName(String typeName) {
        String firstChar = typeName.substring(0,
                                              1);
        return typeName.replaceFirst(firstChar,
                                     firstChar.toLowerCase());
    }

    public TuscanyTypeTable getTypeTable() {
        return typeTable;
    }

    public JMethod[] getMethods() {
        return methods;
    }

    private String generatePrefix() {
        return NAME_SPACE_PREFIX + prefixCount++;
    }

    public void setExcludeMethods(ArrayList excludeMethods) {
        this.excludeMethods = excludeMethods;
    }

    private void initializeSchemaMap(String targetNamespace, String targetNamespacePrefix) {
        XmlSchema xmlSchema = new XmlSchema(targetNamespace, xmlSchemaCollection);
        xmlSchema.setAttributeFormDefault(getAttrFormDefaultSetting());
        xmlSchema.setElementFormDefault(getElementFormDefaultSetting());

        targetNamespacePrefixMap.put(targetNamespace,
                                     targetNamespacePrefix);
        schemaMap.put(targetNamespace,
                      xmlSchema);

        NamespaceMap prefixmap = new NamespaceMap();
        prefixmap.put(TuscanyTypeTable.XS_URI_PREFIX,
                      TuscanyTypeTable.XML_SCHEMA_URI);
        prefixmap.put(targetNamespacePrefix,
                      targetNamespace);
        xmlSchema.setNamespaceContext(prefixmap);
    }

    
    private void addImportORInclude(XmlSchema xmlSchema, QName schemaTypeName) {
        //decide whether there must be an import or an include
        if (xmlSchema.getTargetNamespace().equals(schemaTypeName.getNamespaceURI())) {
            XmlSchema containingSchema = (XmlSchema) schemaMap.get(schemaTypeName.getNamespaceURI());
            //if the type is not defined in the Schema then include
            if (containingSchema.getTypeByName(schemaTypeName) == null) {
                String schemaLocation = null;
                if ((schemaLocation = (String) schemaLocationMap.get(schemaTypeName.getNamespaceURI())) != null) {
                    schemaLocation = DEFAULT_SCHEMA_LOCATION;
                }

                XmlSchemaInclude includeElement = new XmlSchemaInclude();
                includeElement.setSchemaLocation(schemaLocation);

                if (!xmlSchema.getIncludes().contains(includeElement)) {
                    xmlSchema.getIncludes().add(includeElement);
                }
            }
        } else {
            if (!((NamespaceMap) xmlSchema.getNamespaceContext()).values()
                                                                 .contains(schemaTypeName.getNamespaceURI())) {
                XmlSchemaImport importElement = new XmlSchemaImport();
                importElement.setNamespace(schemaTypeName.getNamespaceURI());
                xmlSchema.getItems().add(importElement);
                ((NamespaceMap) xmlSchema.getNamespaceContext()).put(generatePrefix(),
                                                                     schemaTypeName.getNamespaceURI());
            }
        }
    }

    private XmlSchemaForm getAttrFormDefaultSetting() {
        if (FORM_DEFAULT_UNQUALIFIED.equals(generationParams.getAttrFormDefault())) {
            return new XmlSchemaForm(XmlSchemaForm.UNQUALIFIED);
        } else {
            return new XmlSchemaForm(XmlSchemaForm.QUALIFIED);
        }
    }

    private XmlSchemaForm getElementFormDefaultSetting() {
        if (FORM_DEFAULT_UNQUALIFIED.equals(generationParams.getElementFormDefault())) {
            return new XmlSchemaForm(XmlSchemaForm.UNQUALIFIED);
        } else {
            return new XmlSchemaForm(XmlSchemaForm.QUALIFIED);
        }
    }
}
