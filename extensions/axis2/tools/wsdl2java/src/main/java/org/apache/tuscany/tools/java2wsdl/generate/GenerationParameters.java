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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.apache.ws.java2wsdl.Java2WSDLUtils;
import org.apache.ws.java2wsdl.utils.Java2WSDLCommandLineOption;

/**
 * This class encapsulates the parameters that effect the generation of the 
 * WSDL.  For example they contain all user settings such as source class,
 * target location etc.
 *
 */
public class GenerationParameters implements TuscanyJava2WSDLConstants
{
	public static final String WSDL_FILENAME_SUFFIX = ".wsdl";
    public static final String XSD_IMPORT_DELIMITER = "[,]";
    
	private Map cmdLineOptions = null; 
	private FileOutputStream outputFileStream = null;
	private String sourceClassName = null;
	
    private ArrayList extraClasses;
    private String attrFormDefault = null;
    private String elementFormDefault = null;
	private String targetNamespace = null;
	private String targetNamespacePrefix = null;
	private String schemaTargetNamespace = null;
	private String schemaTargetNamespacePrefix = null;
	private ClassLoader classLoader = null;
	private String serviceName = null;
	private String style = DOCUMENT;
	private String use = LITERAL;
	private String locationUri = DEFAULT_LOCATION_URL;
    private Map schemaLocationMap = null;
	
	public GenerationParameters(Map cmdLineOptions) throws Exception
	{
		this.cmdLineOptions = cmdLineOptions;
		loadParameters();
	}
	
	protected void loadParameters() throws Exception
	{
		initializeSourceClassName();
		resolveFileOutputStream();
		resolveClassLoader4InputClasspath();
        loadSchemaLocationMap();
		initializeOtherParams();
	}
	
	private Java2WSDLCommandLineOption loadOption(String shortOption, String longOption) {
        //short option gets precedence
        Java2WSDLCommandLineOption option = null;
        if (longOption != null) {
            option = (Java2WSDLCommandLineOption) cmdLineOptions.get(longOption);
            if (option != null) {
                return option;
            }
        }
        if (shortOption != null) {
            option = (Java2WSDLCommandLineOption) cmdLineOptions.get(shortOption);
        }

        return option;
    }
	
	protected void initializeSourceClassName() throws Exception
	{
		Java2WSDLCommandLineOption option = 
			loadOption(CLASSNAME_OPTION, CLASSNAME_OPTION_LONG);
        sourceClassName = option == null ? null : option.getOptionValue();

        if (sourceClassName == null || sourceClassName.equals("")) {
            throw new Exception("class name must be present!");
        }
	}
	
	/**
	 * @throws Exception
	 */
	protected void resolveFileOutputStream() throws Exception
	{
		File outputFolder;
		Java2WSDLCommandLineOption  option = loadOption(OUTPUT_LOCATION_OPTION,
	            OUTPUT_LOCATION_OPTION_LONG);
	    String outputFolderName = option == null ? System.getProperty("user.dir") : option.getOptionValue();

	    outputFolder = new File(outputFolderName);
	    if (!outputFolder.exists()) {
	        outputFolder.mkdirs();
	    } else if (!outputFolder.isDirectory()) {
	        throw new Exception("The specified location " + outputFolderName + "is not a folder");
	    }
	    
	    option = loadOption(OUTPUT_FILENAME_OPTION,
                OUTPUT_FILENAME_OPTION_LONG);
        String outputFileName = option == null ? null : option.getOptionValue();
        //derive a file name from the class name if the filename is not specified
        if (outputFileName == null) 
        {
            outputFileName = Java2WSDLUtils.getSimpleClassName(sourceClassName) + WSDL_FILENAME_SUFFIX;
        }

        //first create a file in the given location
        File outputFile = new File(outputFolder, outputFileName);
        try 
        {
            if (!outputFile.exists()) 
            {
                outputFile.createNewFile();
            }
            outputFileStream = new FileOutputStream(outputFile);
        } 
        catch (IOException e) 
        {
            throw new Exception(e);
        }
	}
    
    protected void addToSchemaLocationMap(String optionValue) throws Exception
    {
        //option value will be of the form [namespace, schemalocation]
        //hence we take the two substrings starting after '[' and upto ',' and
        //starting after ',' and upto ']'
        getSchemaLocationMap().put(optionValue.substring(1, optionValue.indexOf(COMMA)),
                                optionValue.substring(optionValue.indexOf(COMMA) + 1, optionValue.length() - 1)); 
        
        
    }
    
    protected void loadSchemaLocationMap() throws Exception
    {
        Java2WSDLCommandLineOption option = loadOption(IMPORT_XSD_OPTION, IMPORT_XSD_OPTION_LONG);
                
        if (option != null) 
        {
            ArrayList optionValues = option.getOptionValues();
            
            for ( int count = 0 ; count < optionValues.size() ; ++count )
            {
                addToSchemaLocationMap(((String)optionValues.get(count)).trim());
            }
        }
    }
	
	protected void resolveClassLoader4InputClasspath() throws Exception 
	{
        Java2WSDLCommandLineOption option = 
            loadOption(CLASSPATH_OPTION, CLASSPATH_OPTION_LONG);
        		
		if (option != null) {
            ArrayList optionValues = option.getOptionValues();
            URL[] urls = new URL[optionValues.size()];
            String[] classPathEntries = (String[]) optionValues.toArray(new String[optionValues.size()]);

            try {
                for (int i = 0; i < classPathEntries.length; i++) {
                    String classPathEntry = classPathEntries[i];
                    //this should be a file(or a URL)
                    if (Java2WSDLUtils.isURL(classPathEntry)) {
                        urls[i] = new URL(classPathEntry);
                    } else {
                        urls[i] = new File(classPathEntry).toURL();
                    }
                }
            } catch (MalformedURLException e) {
                throw new Exception(e);
            }

            classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());

        } else {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
	}
	
	protected void initializeOtherParams()
	{
//		set the other parameters to the builder
		Java2WSDLCommandLineOption option = loadOption(SCHEMA_TARGET_NAMESPACE_OPTION,
                SCHEMA_TARGET_NAMESPACE_OPTION_LONG);
   		schemaTargetNamespace = (option == null) ? null : option.getOptionValue();
		         
		option = loadOption(SCHEMA_TARGET_NAMESPACE_PREFIX_OPTION,
                SCHEMA_TARGET_NAMESPACE_PREFIX_OPTION_LONG);
		schemaTargetNamespacePrefix = (option == null) ? null : option.getOptionValue();

		option = loadOption(TARGET_NAMESPACE_OPTION,
                TARGET_NAMESPACE_OPTION_LONG);
		targetNamespace = (option == null) ? null : option.getOptionValue();

		option  = loadOption(TARGET_NAMESPACE_PREFIX_OPTION,
                TARGET_NAMESPACE_PREFIX_OPTION_LONG);
		targetNamespacePrefix = (option == null) ? null : option.getOptionValue();
		
		option = loadOption(SERVICE_NAME_OPTION,
                SERVICE_NAME_OPTION_LONG);
		serviceName = (option == null) ? Java2WSDLUtils.getSimpleClassName(sourceClassName) : option.getOptionValue();

		option = loadOption(STYLE_OPTION,STYLE_OPTION);
		style = (option == null) ? null : option.getOptionValue();
        
        
        option  = loadOption(LOCATION_OPTION,
        		LOCATION_OPTION);
        locationUri = (option == null) ? null : option.getOptionValue();
        
        option = loadOption(USE_OPTION,USE_OPTION);
        use = (option == null) ? null : option.getOptionValue();
        
        option = loadOption(ATTR_FORM_DEFAULT_OPTION, ATTR_FORM_DEFAULT_OPTION_LONG);
        attrFormDefault = (option == null) ? null : option.getOptionValue();
        
        option = loadOption(ELEMENT_FORM_DEFAULT_OPTION,ELEMENT_FORM_DEFAULT_OPTION_LONG);
        elementFormDefault = option == null ? null : option.getOptionValue();
        
        option = loadOption(TuscanyJava2WSDLConstants.EXTRA_CLASSES_DEFAULT_OPTION,
                            TuscanyJava2WSDLConstants.EXTRA_CLASSES_DEFAULT_OPTION_LONG);
        extraClasses = option == null ? new ArrayList() : option.getOptionValues();
	}
	
	public ClassLoader getClassLoader()
	{
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader)
	{
		this.classLoader = classLoader;
	}

	public String getLocationUri()
	{
        if ( locationUri == null )
        {
            locationUri = DEFAULT_LOCATION_URL;
        }
		return locationUri;
	}

	public void setLocationUri(String locationUri)
	{
		this.locationUri = locationUri;
	}

	public FileOutputStream getOutputFileStream()
	{
		return outputFileStream;
	}

	public void setOutputFileStream(FileOutputStream outputFileStream)
	{
		this.outputFileStream = outputFileStream;
	}

	public String getSchemaTargetNamespace() throws Exception
    {
        if (schemaTargetNamespace == null
                || schemaTargetNamespace.trim().equals("")) 
        {
            this.schemaTargetNamespace = Java2WSDLUtils
                    .schemaNamespaceFromClassName(getSourceClassName(), getClassLoader()).toString();
        }
        return schemaTargetNamespace;
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

	public void setSchemaTargetNamespacePrefix(String schemaTargetNamespacePrefix)
	{
		this.schemaTargetNamespacePrefix = schemaTargetNamespacePrefix;
	}

	public String getServiceName()
	{
        if ( serviceName == null )
        {
            serviceName = Java2WSDLUtils.getSimpleClassName(getSourceClassName());
        }
		return serviceName;
	}

	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	public String getSourceClassName()
	{
		return sourceClassName;
	}

	public void setSourceClassName(String sourceClassName)
	{
		this.sourceClassName = sourceClassName;
	}

	public String getStyle()
	{
        if ( style == null )
        {
            style = DOCUMENT;
        }
		return style;
	}

	public void setStyle(String style)
	{
		this.style = style;
	}

	public String getTargetNamespace() throws Exception
	{
        if ( targetNamespace == null ) {
            targetNamespace = Java2WSDLUtils.namespaceFromClassName(this.sourceClassName, this.classLoader).toString();
        }
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace)
	{
		this.targetNamespace = targetNamespace;
	}

	public String getTargetNamespacePrefix()
	{
		return targetNamespacePrefix;
	}

	public void setTargetNamespacePrefix(String targetNamespacePrefix)
	{
		this.targetNamespacePrefix = targetNamespacePrefix;
	}

	public String getUse()
	{
        if ( use == null )
        {
            use = LITERAL;
        }
		return use;
	}

	public void setUse(String use)
	{
		this.use = use;
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
        if ( attrFormDefault == null )
        {
            attrFormDefault = FORM_DEFAULT_QUALIFIED;
        }
        return attrFormDefault;
    }

    public void setAttrFormDefault(String attrFormDefault) {
        this.attrFormDefault = attrFormDefault;
    }

    public String getElementFormDefault() {
        if ( elementFormDefault == null )
        {
            elementFormDefault = FORM_DEFAULT_QUALIFIED;
        }
        return elementFormDefault;
    }

    public void setElementFormDefault(String elementFormDefault) {
        this.elementFormDefault = elementFormDefault;
    }

    public ArrayList getExtraClasses() {
        return extraClasses;
    }

    public void setExtraClasses(ArrayList extraClasses) {
        this.extraClasses = extraClasses;
    }
}
	
