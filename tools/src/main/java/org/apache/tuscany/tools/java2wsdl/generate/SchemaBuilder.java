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

import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.tuscany.sdo.util.SDOUtil;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaForm;
import org.apache.ws.commons.schema.XmlSchemaGroupBase;
import org.apache.ws.commons.schema.XmlSchemaImport;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.utils.NamespaceMap;
import org.apache.ws.java2wsdl.Java2WSDLUtils;
import org.codehaus.jam.JClass;
import org.codehaus.jam.JProperty;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XSDHelper;

public class SchemaBuilder implements TuscanyJava2WSDLConstants {
    public static final String NAME_SPACE_PREFIX = "stn_";

    private static int prefixCount = 1;

    public static final String MIXED = "mixed";

    public static final String GROUP = "group";

    protected String attrFormDefault = null;

    protected String elementFormDefault = null;

    protected XmlSchemaCollection xmlSchemaCollection = new XmlSchemaCollection();

    private Hashtable schemaMap = new Hashtable();

    protected Hashtable targetNamespacePrefixMap = new Hashtable();

    protected TuscanyTypeTable typeTable = new TuscanyTypeTable();

    protected Map schemaLocationMap = null;

    private ClassLoader classLoader;

    protected SchemaBuilder(XmlSchemaCollection schemaCollection,
                            Hashtable schemaMap,
                            Hashtable nsPrefixMap,
                            TuscanyTypeTable typeTable,
                            String attrFormDef,
                            String eleFormDef,
                            Map schemaLocMap,
                            ClassLoader classLoader) {
        this.schemaMap = schemaMap;
        this.xmlSchemaCollection = schemaCollection;
        this.targetNamespacePrefixMap = nsPrefixMap;
        this.typeTable = typeTable;
        this.schemaLocationMap = schemaLocMap;
        this.classLoader = classLoader;
        this.attrFormDefault = attrFormDef;
        this.elementFormDefault = eleFormDef;
    }

    private boolean isSDO(JClass javaType) throws Exception {
        Class sdoClass = Class.forName(javaType.getQualifiedName(),
                                       true,
                                       classLoader);
        return DataObject.class.isAssignableFrom(sdoClass);
    }

    private void buildComplexTypeContents_JavaType(JClass javaType,
                                                   XmlSchemaComplexType complexType,
                                                   XmlSchema xmlSchema) throws Exception {
        JProperty[] properties = javaType.getDeclaredProperties();

        for (int i = 0; i < properties.length; i++) {
            JProperty property = properties[i];
            String propertyName = property.getType().getQualifiedName();
            boolean isArryType = property.getType().isArrayType();
            if (isArryType) {
                propertyName = property.getType().getArrayComponentType().getQualifiedName();
            }

            if (typeTable.isSimpleType(propertyName)) {
                XmlSchemaElement elt1 = new XmlSchemaElement();
                elt1.setName(getCorrectName(property.getSimpleName()));
                elt1.setSchemaTypeName(typeTable.getSimpleSchemaTypeName(propertyName));
                ((XmlSchemaGroupBase) complexType.getParticle()).getItems().add(elt1);
                if (isArryType) {
                    elt1.setMaxOccurs(Long.MAX_VALUE);
                    elt1.setMinOccurs(0);
                }
            } else {
                QName schemaTypeName = null;
                if (isArryType) {
                    schemaTypeName = generateSchema(property.getType().getArrayComponentType());
                } else {
                    schemaTypeName = generateSchema(property.getType());
                }

                XmlSchemaElement elt1 = new XmlSchemaElement();
                elt1.setName(getCorrectName(property.getSimpleName()));
                elt1.setSchemaTypeName(schemaTypeName);
                ((XmlSchemaGroupBase) complexType.getParticle()).getItems().add(elt1);

                if (isArryType) {
                    elt1.setMaxOccurs(Long.MAX_VALUE);
                    elt1.setMinOccurs(0);
                }

                addImports(xmlSchema,
                           schemaTypeName);
            }
        }
    }

    protected QName buildSchema_JavaType(JClass javaType) throws Exception {
        QName schemaTypeName = typeTable.getComplexSchemaTypeName(javaType, this.classLoader);
        if (schemaTypeName == null) {
            String simpleName = javaType.getSimpleName(); 

            String packageName = javaType.getContainingPackage().getQualifiedName();

            String targetNameSpace = 
            	Java2WSDLUtils.schemaNamespaceFromClassName(javaType.getQualifiedName(), this.classLoader)
                                                   .toString();

            XmlSchema xmlSchema = getXmlSchema(targetNameSpace);
            String targetNamespacePrefix = (String) targetNamespacePrefixMap.get(targetNameSpace);

            schemaTypeName = new QName(targetNameSpace, simpleName, targetNamespacePrefix);
            XmlSchemaComplexType complexType = new XmlSchemaComplexType(xmlSchema);
            complexType.setName(simpleName);

            XmlSchemaSequence sequence = new XmlSchemaSequence();
            complexType.setParticle(sequence);

            createGlobalElement(xmlSchema,
                                complexType,
                                schemaTypeName);
            xmlSchema.getItems().add(complexType);
            xmlSchema.getSchemaTypes().add(schemaTypeName,
                                           complexType);

            // adding this type to the table
            // typeTable.addComplexScheam(name, complexType.getQName());
            typeTable.addComplexSchemaType(targetNameSpace,
                                           simpleName,
                                           schemaTypeName);
            buildComplexTypeContents_JavaType(javaType,
                                              complexType,
                                              xmlSchema);
        }
        return schemaTypeName;
    }

    protected QName buildSchema_SDO(Type dataType) // throws Exception
    {
        QName schemaTypeName = typeTable.getComplexSchemaTypeName(dataType.getURI(),
                                                                  dataType.getName());

        if (schemaTypeName == null) {
            // invoke XSDHelper to generate schema for this sdo type
            XSDHelper xsdHelper = SDOUtil.createXSDHelper(SDOUtil.createTypeHelper());
            // it is better to check if XSDHelper can generate the schema
            if (xsdHelper.isXSD(dataType)) {
                // if schemalocations for xsd has been specified, include them
                includeExtXSD(dataType);
            } else {
                List typeList = new Vector();
                typeList.add(dataType);

                // the xsdhelper returns a string that contains the schemas for this type
                String schemaDefns = xsdHelper.generate(typeList,
                                                        schemaLocationMap);

                // extract the schema elements and store them in the schema map
                extractSchemas(schemaDefns);
            }
            // since the XSDHelper will not return the type name, create it and store it in typetable
            schemaTypeName = new QName(dataType.getURI(), dataType.getName(), generatePrefix());
            typeTable.addComplexSchemaType(dataType.getURI(),
                                           dataType.getName(),
                                           schemaTypeName);

        }
        return schemaTypeName;
    }

    public QName generateSchema(JClass javaType) throws Exception {
        if (isSDO(javaType)) {
            Type dataType = createDataObject(javaType).getType();
            return buildSchema_SDO(dataType);
        } else {
            return buildSchema_JavaType(javaType);
        }
    }

    private XmlSchema getXmlSchema(String targetNamespace) {
        XmlSchema xmlSchema;

        if ((xmlSchema = (XmlSchema) schemaMap.get(targetNamespace)) == null) {
            String targetNamespacePrefix = generatePrefix();

            xmlSchema = new XmlSchema(targetNamespace, xmlSchemaCollection);
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
        return xmlSchema;
    }

    /**
     * JAM convert first name of an attribute into UpperCase as an example if there is a instance variable called foo in a bean , then Jam give that
     * as Foo so this method is to correct that error
     * 
     * @param wrongName
     * @return the right name, using english as the locale for case conversion
     */
    public static String getCorrectName(String wrongName) {
        if (wrongName.length() > 1) {
            return wrongName.substring(0,
                                       1).toLowerCase(Locale.ENGLISH)
                    + wrongName.substring(1,
                                          wrongName.length());
        } else {
            return wrongName.substring(0,
                                       1).toLowerCase(Locale.ENGLISH);
        }
    }

    private String addImports(XmlSchema xmlSchema, QName schemaTypeName) {
        String prefix = null;
        String[] prefixes = xmlSchema.getNamespaceContext().getDeclaredPrefixes();
        for (int count = 0; count < prefixes.length; ++count) {
            if (schemaTypeName.getNamespaceURI().
                    equals(xmlSchema.getNamespaceContext().getNamespaceURI(prefixes[count])) ) {
                return prefixes[count];
            }
        }

        // the following lines are executed only if a prefix was not found which implies that the
        // schemaTypeName was not imported earlier and also it does not belong to the targetnamespace
        String schemaLocation = null;
        XmlSchemaImport importElement = new XmlSchemaImport();
        importElement.setNamespace(schemaTypeName.getNamespaceURI());
        xmlSchema.getItems().add(importElement);
        prefix = generatePrefix();
        //it is safe to cast like this since it was this class that instantiated the
        //NamespaceContext and assigned it to an instance of a NamespaceMap (see method getXmlSchema)
        ((NamespaceMap)xmlSchema.getNamespaceContext()).put(prefix,
                                                schemaTypeName.getNamespaceURI());

        return prefix;
    }

    private String formGlobalElementName(String typeName) {
        String firstChar = typeName.substring(0,
                                              1);
        return typeName.replaceFirst(firstChar,
                                     firstChar.toLowerCase());
    }

    private void createGlobalElement(XmlSchema xmlSchema,
                                     XmlSchemaComplexType complexType,
                                     QName elementName) {
        XmlSchemaElement globalElement = new XmlSchemaElement();
        globalElement.setSchemaTypeName(complexType.getQName());
        globalElement.setName(formGlobalElementName(complexType.getName()));
        globalElement.setQName(elementName);

        xmlSchema.getItems().add(globalElement);
        xmlSchema.getElements().add(elementName,
                                    globalElement);
    }

    private DataObject createDataObject(JClass sdoClass) throws Exception {
        Class sdoType = Class.forName(sdoClass.getQualifiedName(),
                                      true,
                                      classLoader);

        Constructor constructor = sdoType.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
        Object instance = constructor.newInstance(new Object[0]);
        return (DataObject) instance;
    }

    private String generatePrefix() {
        return NAME_SPACE_PREFIX + prefixCount++;
    }

    private void includeExtXSD(Type dataType) {
        // now we know there is a type for which the xsd must come from outside
        // create a schema for the namespace of this type and add an include in it for
        // the xsd that is defined externally
        XmlSchema xmlSchema = getXmlSchema(dataType.getURI());

        // ideally there could be more than one external schema defintions for a namespace
        // and hence schemalocations will be a list of locations
        // List schemaLocations = (List)schemaLocationMap.get(dataType.getURI());

        // since as per the specs the input to XSDHelper is a map of <String, String> allowing
        // only one schemalocation for a namespace. So for now this single location will be
        // picked up and put into a list
        List schemaLocations = new Vector();

        if (schemaLocationMap.get(dataType.getURI()) != null) {
            schemaLocations.add(schemaLocationMap.get(dataType.getURI()));
        }

        if (schemaLocations.size() <= 0) {
            schemaLocations.add(DEFAULT_SCHEMA_LOCATION);
        }

        Iterator includesIterator = xmlSchema.getIncludes().getIterator();
        Iterator schemaLocIterator = schemaLocations.iterator();
        String aSchemaLocation = null;
        boolean includeExists = false;
        // include all external schema locations
        while (schemaLocIterator.hasNext()) {
            aSchemaLocation = (String) schemaLocIterator.next();
            while (includesIterator.hasNext()) {
                if (!includeExists
                        && aSchemaLocation.equals(((XmlSchemaInclude) includesIterator.next()).getSchemaLocation())) {
                    includeExists = true;
                }
            }

            if (!includeExists) {
                XmlSchemaInclude includeElement = new XmlSchemaInclude();
                includeElement.setSchemaLocation(aSchemaLocation);
                xmlSchema.getIncludes().add(includeElement);
                xmlSchema.getItems().add(includeElement);
            }
        }

    }

    private void extractSchemas(String schemaDefns) {
        // load each schema element and add it to the schema map

        String token = getToken(schemaDefns);
        int curIndex = schemaDefns.indexOf(token);
        int nextIndex = schemaDefns.indexOf(token,
                                            curIndex + token.length());

        while (curIndex != -1) {
            StringReader sr = null;
            if (nextIndex != -1)
                sr = new StringReader(schemaDefns.substring(curIndex,
                                                            nextIndex));
            else
                sr = new StringReader(schemaDefns.substring(curIndex));

            XmlSchemaCollection collection = new XmlSchemaCollection();
            XmlSchema aSchema = collection.read(sr,
                                                null);
            addSchemaToMap(aSchema);

            curIndex = nextIndex;
            nextIndex = schemaDefns.indexOf(token,
                                            curIndex + token.length());
        }
    }

    private void addSchemaToMap(XmlSchema extractedSchema) {
        // check if a Schema object already exists in schema map for targetNamespace of this schema element
        // if it does then copy the contents of this schema element to the existing one, ensuring that
        // duplicate elements are not created. i.e. before adding some child element like 'include' or 'import'
        // check if it already exists, if it does don't add this
        XmlSchema existingSchema = (XmlSchema) schemaMap.get(extractedSchema.getTargetNamespace());

        if (existingSchema == null) {
            extractedSchema.setAttributeFormDefault(getAttrFormDefaultSetting());
            extractedSchema.setElementFormDefault(getElementFormDefaultSetting());
            schemaMap.put(extractedSchema.getTargetNamespace(),
                          extractedSchema);

        } else {
            copySchemaItems(existingSchema,
                            extractedSchema);
        }
    }

    private void copySchemaItems(XmlSchema existingSchema, XmlSchema aSchema) {
        // items to copy are imports, includes, elements, types ...
        // each item is checked if it is a duplicate entry and copied only if it isn't
        Iterator itemsIterator = aSchema.getItems().getIterator();
        Object schemaObject = null;
        XmlSchemaElement schemaElement = null;
        XmlSchemaType schemaType = null;
        XmlSchemaInclude schemaInclude = null;
        QName qName = null;
        List existingIncludes = getExistingIncludes(existingSchema);

        while (itemsIterator.hasNext()) {
            schemaObject = itemsIterator.next();
            if (schemaObject instanceof XmlSchemaElement) {
                schemaElement = (XmlSchemaElement) schemaObject;
                qName = schemaElement.getQName();
                // if the element does not exist in the existing schema
                if (existingSchema.getElementByName(qName) == null) {
                    // add it to the existing schema
                    existingSchema.getElements().add(qName,
                                                     schemaElement);
                    existingSchema.getItems().add(schemaElement);
                }
            } else if (schemaObject instanceof XmlSchemaType) {
                schemaType = (XmlSchemaType) itemsIterator.next();
                qName = schemaType.getQName();
                // if the element does not exist in the existing schema
                if (existingSchema.getElementByName(qName) == null) {
                    // add it to the existing schema
                    existingSchema.getSchemaTypes().add(qName,
                                                        schemaType);
                    existingSchema.getItems().add(schemaType);
                    // add imports
                    addImports(existingSchema,
                               qName);
                }
            } else if (schemaObject instanceof XmlSchemaInclude) {
                schemaInclude = (XmlSchemaInclude) itemsIterator.next();
                if (!existingIncludes.contains(schemaInclude.getSchemaLocation())) {
                    existingSchema.getIncludes().add(schemaInclude);
                    existingSchema.getItems().add(schemaInclude);
                }
            }
        }
    }

    private List getExistingIncludes(XmlSchema xmlSchema) {
        List includeSchemaLocations = new Vector();
        Iterator iterator = xmlSchema.getIncludes().getIterator();

        while (iterator.hasNext()) {
            includeSchemaLocations.add(((XmlSchemaInclude) iterator.next()).getSchemaLocation());
        }
        return includeSchemaLocations;
    }

    private XmlSchemaForm getAttrFormDefaultSetting() {
        if (FORM_DEFAULT_UNQUALIFIED.equals(getAttrFormDefault())) {
            return new XmlSchemaForm(XmlSchemaForm.UNQUALIFIED);
        } else {
            return new XmlSchemaForm(XmlSchemaForm.QUALIFIED);
        }
    }

    private XmlSchemaForm getElementFormDefaultSetting() {
        if (FORM_DEFAULT_UNQUALIFIED.equals(getElementFormDefault())) {
            return new XmlSchemaForm(XmlSchemaForm.UNQUALIFIED);
        } else {
            return new XmlSchemaForm(XmlSchemaForm.QUALIFIED);
        }
    }

    private String getToken(String s) {
        // get the schema element name eg. <xs:schema or <xsd:schema. We only know that 'schema' will be used
        // but not sure what suffix is used. Hence this method to get the actual element name used
        int i = s.indexOf(SCHEMA_ELEMENT_NAME);
        int j = s.substring(0,
                            i).lastIndexOf("<");
        return s.substring(j,
                           i + SCHEMA_ELEMENT_NAME.length());
    }

    public String getAttrFormDefault() {
        return attrFormDefault;
    }

    public void setAttrFormDefault(String attrFormDefault) {
        this.attrFormDefault = attrFormDefault;
    }

    public String getElementFormDefault() {
        return elementFormDefault;
    }

    public void setElementFormDefault(String elementFormDefault) {
        this.elementFormDefault = elementFormDefault;
    }
}
