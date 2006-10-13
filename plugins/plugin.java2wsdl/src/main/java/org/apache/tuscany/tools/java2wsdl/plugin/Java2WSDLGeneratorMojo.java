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
package org.apache.tuscany.tools.java2wsdl.plugin;

import java.util.Hashtable;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tuscany.tools.java2wsdl.generate.Java2WSDLGeneratorFactory;
import org.apache.ws.java2wsdl.Java2WSDLConstants;
import org.apache.ws.java2wsdl.utils.Java2WSDLCommandLineOption;

/**
 * @version $Rev$ $Date$
 * @goal generate
 * @phase generate-sources
 * @description Generate WSDL from a given Java class / interface
 */
public class Java2WSDLGeneratorMojo extends AbstractMojo 
{
	
     /**
     * The name of the class for which the WSDL must be generated
     * @parameter 
     * 
     */
    private String sourceClassName;

    /**
     * The location where the wsdls should be generated into
     * @parameter expression="${project.build.directory}\\java2wsdl-wsdl"
    */
    private String targetLocation;
    
    /**
     * The name of the wsdl file 
     * @parameter
    */
    private String wsdlFilename;


    /**
     * Classpaths to be included
     * @parameter 
     * 
     */
    String[] classpaths;

    /**
     * The name of the service
     * @parameter    
     */
    private String serviceName;

    /**
     * The binding style for the service
     * @parameter 
     */
    private String bindingStyle;

    /**
     * The binding use option
     * @parameter 
     */
    private String bindingUse;

    /**
     * The soap address
     * @parameter 
     */
    private String soapAddress;

    public void execute() throws MojoExecutionException 
    {
    	try
    	{
    		Java2WSDLGeneratorFactory.getInstance().createGenerator().generateWSDL(createOptionsMap ());
    	}
    	catch ( Exception e )
    	{
    		throw new MojoExecutionException("Exception in Java2WSDL Maven Plugin ", e);
    	}
    }
    
    protected Map createOptionsMap()
    {
		Map optionsMap = new Hashtable();
	
	        optionsMap.put(Java2WSDLConstants.CLASSNAME_OPTION, 
	    			new Java2WSDLCommandLineOption(Java2WSDLConstants.CLASSNAME_OPTION, new String[]{sourceClassName}));
	    	
		if ( targetLocation != null )
		{
		    optionsMap.put(Java2WSDLConstants.OUTPUT_LOCATION_OPTION, 
	    			new Java2WSDLCommandLineOption(Java2WSDLConstants.OUTPUT_LOCATION_OPTION, new String[]{targetLocation}));
		}
		
		if ( wsdlFilename != null )
		{
		    optionsMap.put(Java2WSDLConstants.OUTPUT_FILENAME_OPTION, 
	    			new Java2WSDLCommandLineOption(Java2WSDLConstants.OUTPUT_FILENAME_OPTION, new String[]{wsdlFilename}));
		}
	
		if ( classpaths != null && classpaths.length > 0 )
		{
		    optionsMap.put(Java2WSDLConstants.CLASSPATH_OPTION, 
	    			new Java2WSDLCommandLineOption(Java2WSDLConstants.CLASSPATH_OPTION, classpaths));
		}
	
		if ( serviceName != null  )
		{
		    optionsMap.put(Java2WSDLConstants.SERVICE_NAME_OPTION, 
	    			new Java2WSDLCommandLineOption(Java2WSDLConstants.SERVICE_NAME_OPTION, new String[]{serviceName}));
		}
		
		 if ( bindingStyle != null  )
		{
		    optionsMap.put(Java2WSDLConstants.STYLE_OPTION, 
	    			new Java2WSDLCommandLineOption(Java2WSDLConstants.STYLE_OPTION, new String[]{bindingStyle}));
		}
	
		if ( bindingUse != null  )
		{
		    optionsMap.put(Java2WSDLConstants.USE_OPTION, 
	    			new Java2WSDLCommandLineOption(Java2WSDLConstants.USE_OPTION, new String[]{bindingUse}));
		}
	
		if ( soapAddress != null  )
		{
		    optionsMap.put(Java2WSDLConstants.LOCATION_OPTION, 
	    			new Java2WSDLCommandLineOption(Java2WSDLConstants.LOCATION_OPTION, new String[]{soapAddress}));
		}

   	return optionsMap;
    }
}
