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
package org.apache.tuscany.tools.java2wsdl.generate;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.ws.java2wsdl.Java2WSDLConstants;
import org.apache.ws.java2wsdl.Java2WSDLUtils;

public class TuscanyJava2WSDLBuilder implements Java2WSDLConstants
{

    private OutputStream out;
    private String className;
    private ClassLoader classLoader;
    private String wsdlPrefix = "wsdl";

    private String serviceName = null;

    //these apply for the WSDL
    private GenerationParameters genParams = null;
    private String targetNamespace = null;
    private String targetNamespacePrefix = null;

    private String attrFormDefault = null;
    private String elementFormDefault = null;
    private String schemaTargetNamespace = null;
    private String schemaTargetNamespacePrefix = null;
    private String style = Java2WSDLConstants.DOCUMENT;
    private String use = Java2WSDLConstants.LITERAL;
    private String locationUri = Java2WSDLConstants.DEFAULT_LOCATION_URL;
    private Map schemaLocationMap = null;
    
    private OMElement wsdlDocument = null;

    public String getSchemaTargetNamespace() throws Exception
    {
        if (schemaTargetNamespace == null
                || schemaTargetNamespace.trim().equals("")) 
        {
            this.schemaTargetNamespace = Java2WSDLUtils
                    .schemaNamespaceFromClassName(className,classLoader).toString();
        }
        
        return schemaTargetNamespace;
    }

    public String getStyle() {
        return style;
    }

    public String getLocationUri() {
        return locationUri;
    }

    public void setLocationUri(String locationUri) {
        this.locationUri = locationUri;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public void setSchemaTargetNamespace(String schemaTargetNamespace) 
    {
        this.schemaTargetNamespace = schemaTargetNamespace;
    }

    public String getSchemaTargetNamespacePrefix() 
    {
        if (schemaTargetNamespacePrefix == null
                || schemaTargetNamespacePrefix.trim().equals("")) 
        {
            this.schemaTargetNamespacePrefix = SCHEMA_NAMESPACE_PRFIX;
        }
        
        return schemaTargetNamespacePrefix;
    }

    public void setSchemaTargetNamespacePrefix(String schemaTargetNamespacePrefix) {
        this.schemaTargetNamespacePrefix = schemaTargetNamespacePrefix;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public String getTargetNamespacePrefix() {
        return targetNamespacePrefix;
    }

    public void setTargetNamespacePrefix(String targetNamespacePrefix) {
        this.targetNamespacePrefix = targetNamespacePrefix;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


    public String getWsdlPrefix() {
        return wsdlPrefix;
    }

    public void setWsdlPrefix(String wsdlPrefix) {
        this.wsdlPrefix = wsdlPrefix;
    }

    /**
     * @param out
     * @param className
     * @param classLoader
     */
    public TuscanyJava2WSDLBuilder(OutputStream out, String className, ClassLoader classLoader) {
        this.out = out;
        this.className = className;
        this.classLoader = classLoader;
    }


    /**
     * Externally visible generator method
     *
     * @throws Exception
     */
    public void buildWSDL() throws Exception 
    {
        ArrayList excludeOpeartion = new ArrayList();
        excludeOpeartion.add("init");
        excludeOpeartion.add("setOperationContext");
        excludeOpeartion.add("destroy");
        
        TuscanySchemaGenerator typesGenerator = 
            new TuscanySchemaGenerator(classLoader, 
                                        className,
                                        getSchemaTargetNamespace(), 
                                        getSchemaTargetNamespacePrefix(),
                                        getSchemaLocationMap());
        typesGenerator.setExcludeMethods(excludeOpeartion);
        typesGenerator.setAttrFormDefault(getAttrFormDefault());
        typesGenerator.setElementFormDefault(getElementFormDefault());
        
        Collection schemaCollection = typesGenerator.buildWSDLTypes();
        
        TuscanyJava2OMBuilder java2OMBuilder = new TuscanyJava2OMBuilder(typesGenerator.getMethods(),
        		schemaCollection,
                getSchemaTargetNamespace(),
                getSchemaTargetNamespacePrefix(),
                typesGenerator.getTypeTable(),
                typesGenerator.getSdoAnnoMap(),
                serviceName == null ? Java2WSDLUtils.getSimpleClassName(className) : serviceName,
                targetNamespace == null ? Java2WSDLUtils.namespaceFromClassName(className, classLoader).toString():targetNamespace,
                targetNamespacePrefix,
                style,
                use,
                locationUri);
        wsdlDocument = java2OMBuilder.generateOM();
    }

    public OMElement getWsdlDocument()
	{
		return wsdlDocument;
	}

	public void setWsdlDocument(OMElement wsdlDocument)
	{
		this.wsdlDocument = wsdlDocument;
	}

    public Map getSchemaLocationMap() 
    {
        if ( schemaLocationMap == null )
        {
            schemaLocationMap = new Hashtable();
            
        }
        return schemaLocationMap;
    }

    public void setSchemaLocationMap(Map schemaLocationMap) {
        this.schemaLocationMap = schemaLocationMap;
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

