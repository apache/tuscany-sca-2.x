/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tuscany.sca.implementation.bpel.ode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.Reference;

import java.io.File;
import java.io.PrintStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.net.URL;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * A class that handles the deploy.xml file required for each BPEL process by the ODE runtime
 * @author Mike Edwards
 * 
 * An explanation of the structure of the ODE deploy file:
 * 
 * <deploy xmlns="http://www.apache.org/ode/schemas/dd/2007/03"
 *  xmlns:tns="http://helloworld"
 *  xmlns:tus="http://tuscany.apache.org">
 *
 *  <process name="tns:HelloWorld">
 *      <active>true</active>
 *      <provide partnerLink="helloPartnerLink">
 *          <service name="tus:helloPartnerLink" port="HelloWorld"/>
 *      </provide>
 *      <invoke partnerLink="greetingsPartnerLink">
 *          <service name="tus:greetingsPartnerLink" port="Greetings"/>
 *      </invoke>
 *  </process>
 * </deploy> 
 * 
 * For SCA purposes:
 * 
 * a) Each partner link in the BPEL process is declared using either a <provide.../> 
 * (for a service) or using a <invoke.../> (for a reference).
 * 
 * b) Each <provide/> and <invoke/> must use the partnerLink name, as declared in the
 * BPEL process.
 * 
 * c) The <provide/> and <invoke/> elements each have a single child <service/> element.  
 * The <service/> elements have name and port attributes.  The NAME attribute MUST be set 
 * to the same name as the partnerLink and MUST be prefixed by a prefix which references 
 * the namespace "http://tuscany.apache.org" ("tus" in the example above).  
 * The port attribute can be set to any name (it must be present but it is not actually 
 * used for anything significant).
 * 
 * When SCA loads a BPEL process to the ODE server, this file is read by the ODE server to
 * characterize the process.  When SCA interacts with ODE at later points - either when a
 * service is being invoked or the process invokes a reference - it is the service @name
 * attribute that identifies the service or reference involved.
 *
 * @version 
 */
public class BPELODEDeployFile {
	private final Log __log = LogFactory.getLog(getClass());
	
	static final String DEPLOY_ELEMENT_START = "<deploy xmlns=\"http://www.apache.org/ode/schemas/dd/2007/03\"";
	static final String DEPLOY_ENDELEMENT = "</deploy>";
	static final String PROCESS_NAMESPACE_DECL = "xmlns:tns=";
	static final String SERVICE_NAMESPACE = "xmlns:tus=\"http://tuscany.apache.org\"";
	static final String PROCESS_ELEMENT_START = "<process name=\"tns:";
	static final String PROCESS_ELEMENT_END = "\">";
	static final String PROCESS_ENDELEMENT = "</process>";
	static final String ACTIVE_ELEMENT = "<active>true</active>";
	static final String PROVIDE_ELEMENT_START = "<provide partnerLink=\"";
	static final String PROVIDE_ELEMENT_END = "\">";
	static final String PROVIDE_ENDELEMENT = "</provide>";
	static final String SERVICE_ELEMENT_START = "<service name=\"tus:";
	static final String SERVICE_ELEMENT_PORT = "\" port=\"";
	static final String SERVICE_ELEMENT_END = "Port\"/>";
	static final String INVOKE_ELEMENT_START = "<invoke partnerLink=\"";
	static final String INVOKE_ELEMENT_END = "\">";
	static final String INVOKE_ENDELEMENT = "</invoke>";
	
	static final String DEPLOY_FILENAME = "deploy.xml";
	
	private BPELImplementation implementation;
	
	/**
	 * Constructor - requires a BPELImplementation as a parameter
	 * The ODE deploy.xml file is for this supplied BPELImplementation
	 * @param theImplementation
	 */
	public BPELODEDeployFile( BPELImplementation theImplementation ) {
		
		implementation = theImplementation;
		
	} // end BPELODEDeployFile constructor
	
	/**
	 * Writes the deploy file into the same directory as the BPEL process file, with the name
	 * "deploy.xml"
	 */
	public void writeDeployfile() throws IOException {
		
		File theDirectory = getDirectory();
		
		File deployFile = new File( theDirectory, DEPLOY_FILENAME );
		new FileOutputStream( deployFile );
		//if( !deployFile.canWrite() ) throw new IOException( "Unable to write to deploy file" +
		//		                                             deployFile.getPath() );
		
		// Create a stream for the data and write the data to the file
		PrintStream theStream = new PrintStream( new FileOutputStream( deployFile ) );
		try {
		    constructDeployXML( theStream );
		    if( theStream.checkError() ) throw new IOException();
		} catch (Exception e) {
			throw new IOException( "Unable to write data to deploy file" +
					                deployFile.getPath() );
		} finally {
			theStream.close();
		} // end try
		
	} // end writeDeployFile
	
	/**
	 * Creates the deploy.xml data and writes it to a supplied PrintStream
	 * @param stream
	 */
	public void constructDeployXML( PrintStream stream ) {
		
		// <deploy + namespace...
		stream.println( DEPLOY_ELEMENT_START );
		// namespace of the BPEL process
		QName process = implementation.getProcess();
		String processNamespace = process.getNamespaceURI();
		stream.println( PROCESS_NAMESPACE_DECL + "\"" + processNamespace + "\"" );
		// namespace for the service name elements
		stream.println( SERVICE_NAMESPACE + ">" );
		
		// <process> element
		stream.println( PROCESS_ELEMENT_START + process.getLocalPart() + 
						PROCESS_ELEMENT_END );
		
		// <active/> element
		stream.println( ACTIVE_ELEMENT );
		
		ComponentType componentType = implementation.getComponentType();
		List<Service> theServices = componentType.getServices();
		// Loop over the <provide/> elements - one per service
		for ( Service service : theServices ) {
			String serviceName = service.getName();
			// Provide element...
			stream.println( PROVIDE_ELEMENT_START + serviceName + PROVIDE_ELEMENT_END );
			// Child service element...
			stream.println( SERVICE_ELEMENT_START + serviceName + 
					SERVICE_ELEMENT_PORT + serviceName + SERVICE_ELEMENT_END );
			stream.println( PROVIDE_ENDELEMENT );
		} // end for
		
		// Loop over the <invoke/> elements - one per reference
		List<Reference> theReferences = componentType.getReferences();
		for ( Reference reference : theReferences ) {
			String referenceName = reference.getName();
			stream.println( INVOKE_ELEMENT_START + referenceName + INVOKE_ELEMENT_END );
			// Child service element...
			stream.println( SERVICE_ELEMENT_START + referenceName + 
					SERVICE_ELEMENT_PORT + referenceName + SERVICE_ELEMENT_END );
			stream.println( INVOKE_ENDELEMENT );

		} // end for
		
		// </process> element
		stream.println( PROCESS_ENDELEMENT );
		
		// </deploy>
		stream.println( DEPLOY_ENDELEMENT );
		
	} // end constructDeployXML
	
    /** 
     * Gets the directory containing the BPEL process
     * @return
     */
    private File getDirectory() {
        File theDir = getBPELFile().getParentFile();
        return theDir;
    } // end getDirectory

    /**
     * Gets the File containing the BPEL process definition
     * @return - the File object containing the BPEL process
     */
    private File getBPELFile() {
        URL fileURL = implementation.getProcessDefinition().getLocation();
        try {
            File theProcess = new File( fileURL.toURI());
            return theProcess;
        } catch( Exception e ) {
            if(__log.isDebugEnabled()) {
                __log.debug("Exception converting BPEL file URL to an URI: " + e );
            }
        } // end try
        return null;
    } // end getBPELFile



} // end class BPELODEDeployFile
