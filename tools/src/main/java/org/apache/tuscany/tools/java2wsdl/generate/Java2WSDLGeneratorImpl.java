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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.ws.java2wsdl.Java2WSDL;
import org.apache.axis2.wsdl.util.CommandLineOption;
import org.apache.axis2.wsdl.util.CommandLineOptionParser;
import org.apache.axis2.wsdl.util.Java2WSDLOptionsValidator;
import org.apache.ws.java2wsdl.Java2WSDLUtils;

/**
 * This is an implementation of the Java2WSDLGenerator facade. This
 * implementation is a decorator around the Axis2 implementation of the
 * Java2WSDL conversion. The WSDL generation is divided into phases that are
 * stringed up as a template method. The phases are - User Input Validation -
 * WSDL Java Model Generation - Serialization of WSDL Java Model The function of
 * each phase is accomplished by delegation to the appropriate classes in Axis2.
 * At the start and end of each phase an event is published to subcribers
 * denoting the start and end of the phase.
 * 
 * Such a spliting up of the Java2WSDL conversion into phases has been designed
 * to enable interceptors to modify the model or apply transformations to the
 * output. Typically the interceptors can subscribe to the start and end events
 * of these phases and hence be able to intercept.
 * 
 * Note: This class contains substantial AXIS2 Java2WSDL code refactored into
 * it. These will be removed as and when the Axis2 code is fixed.
 * 
 */
public class Java2WSDLGeneratorImpl implements Java2WSDLGenerator 
{
	private List<WSDLGenListener> genPhaseListeners = new Vector<WSDLGenListener>();
	private GenerationParameters genParams = null;
	private Map<String, CommandLineOption> commandLineOptions = null;
	private TuscanyJava2WSDLBuilder java2WsdlBuilder;
	private OutputStream outputStream = null;
	
	/*public static final String HTTP = "http://";

	public static final String WSDL_FILENAME_SUFFIX = ".wsdl";

	public static final String DEFAULT_PREFIX = "wsdl";

	public static final char PACKAGE_CLASS_DELIMITER = '.';

	public static final String DEFAULT_TARGET_NAMESPACE_PREFIX = "tns";

	public static final String DEFAULT_SCHEMA_TARGET_NAMESPACE_PREFIX = "stns";

	private String sourceClassName = null;

	

	private ClassLoader classLoader;

	private WSDLDescription wsdlDescription = null;

	private WSDLModel wsdlModel = null;*/

	public Java2WSDLGeneratorImpl()
	{
		
	}

	private void multicastGenPhaseCompletionEvent(int genPhase) {
		WSDLGenEvent event = new WSDLGenEvent(this, genPhase);
		Iterator iterator = genPhaseListeners.iterator();
		while (iterator.hasNext()) {
			((WSDLGenListener) iterator.next()).WSDLGenPhaseCompleted(event);
		}
	}

	private void initJava2WSDLBuilder() throws Exception 
	{
//		Now we are done with loading the basic values - time to create the builder
        java2WsdlBuilder = new TuscanyJava2WSDLBuilder(genParams.getOutputFileStream(),
        										genParams.getSourceClassName(),
        										genParams.getClassLoader());
        java2WsdlBuilder.setSchemaTargetNamespace(genParams.getSchemaTargetNamespace());
        java2WsdlBuilder.setSchemaTargetNamespacePrefix(genParams.getSchemaTargetNamespacePrefix());
        java2WsdlBuilder.setTargetNamespace(genParams.getTargetNamespace());
        java2WsdlBuilder.setTargetNamespacePrefix(genParams.getTargetNamespacePrefix());
        java2WsdlBuilder.setServiceName(genParams.getServiceName() == null ? 
        		Java2WSDLUtils.getSimpleClassName(genParams.getSourceClassName()) : genParams.getServiceName());

        if (genParams.getStyle() != null) 
        {
            java2WsdlBuilder.setStyle(genParams.getStyle());
        }

        if (genParams.getLocationUri() != null) {
            java2WsdlBuilder.setLocationUri(genParams.getLocationUri());
        }

        if (genParams.getUse() != null) 
        {
            java2WsdlBuilder.setUse(genParams.getUse());
        }
	}
        
   	protected boolean validateInputArgs(String[] args) 
	{
		boolean isValid = true;
		CommandLineOptionParser parser = new CommandLineOptionParser(args);
		if (parser.getAllOptions().size() == 0) {
			Java2WSDL.printUsage();
			isValid = false;
		} else if (parser.getInvalidOptions(new Java2WSDLOptionsValidator())
				.size() > 0) {
			Java2WSDL.printUsage();
			isValid = false;
		}

		if (isValid) 
		{
			commandLineOptions = parser.getAllOptions();
		}

		return isValid;
	}

	public boolean buildWSDLDocument() throws Exception 
	{
		boolean isComplete = true;
		initJava2WSDLBuilder();
		java2WsdlBuilder.buildWSDL();

		return isComplete;
	}

	public boolean serializeWSDLDocument() throws Exception {
		boolean isComplete = true;

		if ( getOutputStream() == null )
		{
			setOutputStream(genParams.getOutputFileStream());
		}
		
		java2WsdlBuilder.getWsdlDocument().serialize(getOutputStream());
		getOutputStream().flush();
		getOutputStream().close();;

		return isComplete;
	}

	/*
	 * This is the template method that splits the Java2WSDL generation cycle
	 * into phase / steps.
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#generateWSDL(java.lang.String[])
	 */
	public void generateWSDL(Map commandLineOptions) 
	{
		try 
		{
			// load the user options into an easy to access abstraction
			genParams = new GenerationParameters(commandLineOptions);
			
			// if the WSDL Model generation was successul
			if ( buildWSDLDocument() ) 
			{
				// multicast event for generation of wsdl model
				multicastGenPhaseCompletionEvent(WSDLGenListener.WSDL_MODEL_CREATION);
				// if the serialization of the generated (and fixed) model
				// is successful
				if (serializeWSDLDocument()) {
					// multicast event for writing of the WSDL Model to
					// supplied output stream
					multicastGenPhaseCompletionEvent(WSDLGenListener.WSDL_MODEL_WRITING);
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		// TODO Auto-generated method stub

	}

	public void generateWSDL(String[] args) 
	{
		// if the argument input are found to be valid
		if (validateInputArgs(args)) 
		{
			//multicast event for input args validation complete
			multicastGenPhaseCompletionEvent(WSDLGenListener.INPUT_ARGS_VALIDATION);
			generateWSDL(commandLineOptions);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#addWSDLGenListener(tuscany.tools.WSDLGenListener)
	 */
	public void addWSDLGenListener(WSDLGenListener l) {
		genPhaseListeners.add(l);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#removeWSDLGenListener(tuscany.tools.WSDLGenListener)
	 */
	public void removeWSDLGenListener(WSDLGenListener l) {
		genPhaseListeners.remove(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#getCommandLineOptions()
	 */
	public Map getCommandLineOptions() {
		return commandLineOptions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#setCommandLineOptoins(java.util.Map)
	 */
	public void setCommandLineOptoins(Map cmdLineOpts) {
		commandLineOptions = cmdLineOpts;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#getOutputStream()
	 */
	public OutputStream getOutputStream() {
		// TODO Auto-generated method stub
		return outputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#setOutputStream(java.io.OutputStream)
	 */
	public void setOutputStream(OutputStream outStream) {
		outputStream = outStream;
	}

	
	public TuscanyJava2WSDLBuilder getJava2WsdlBuilder()
	{
		return java2WsdlBuilder;
	}

	public void setJava2WsdlBuilder(TuscanyJava2WSDLBuilder java2WsdlBuilder)
	{
		this.java2WsdlBuilder = java2WsdlBuilder;
	}

	public WSDLModel getWSDLModel()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
