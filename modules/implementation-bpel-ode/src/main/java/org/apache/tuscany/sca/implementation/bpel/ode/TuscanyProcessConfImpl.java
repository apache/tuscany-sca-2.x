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
package org.apache.tuscany.sca.implementation.bpel.ode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.compiler.BpelC;
import org.apache.ode.bpel.evt.BpelEvent.TYPE;
import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.ProcessConf;
import org.apache.ode.bpel.iapi.ProcessState;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A Tuscany implementation of the ODE Process Conf
 * 
 * @version $Rev$ $Date$
 */
public class TuscanyProcessConfImpl implements ProcessConf {
    private final Log __log = LogFactory.getLog(getClass());

    private BPELImplementation implementation;
    private Map<String, Endpoint> invokeEndpoints = null;
    private Map<String, Endpoint> provideEndpoints = null;
    private Map<QName, Node> properties = null;
    private ProcessState processState;
    private Date deployDate;

    private final String TUSCANY_NAMESPACE = "http://tuscany.apache.org";

    /**
     * Constructor for the ProcessConf implementation
     * @param theImplementation the BPEL implementation for which this is the ProcessConf
     */
    public TuscanyProcessConfImpl( BPELImplementation theImplementation ) {
        //System.out.println("New TuscanyProcessConfImpl...");
        this.implementation = theImplementation;

        processState = ProcessState.ACTIVE;
        deployDate = new Date();

        // Compile the process
        compile( getBPELFile() );
    } // end TuscanyProcessConfImpl constructor

    /**
     * Returns the URI for the directory containing the BPEL process
     */
    public URI getBaseURI() {
        //System.out.println("getBaseURI called");
        File theDir = getDirectory();
        return theDir.toURI();
    }

    /**
     * Returns a String containing the (local) name of the file containing the BPEL process
     */
    public String getBpelDocument() {
        //System.out.println("getBPELDocument called");
        try {
            File processFile = new File(URI.create(implementation.getProcessDefinition().getLocation()));
            return getRelativePath( getDirectory(), processFile);
        } catch (Exception e) {
            if(__log.isWarnEnabled()) {
                __log.warn("Unable to resolve relative path of BPEL process" + implementation.getProcessDefinition().getLocation(), e );
            }
            return null;
        } // end try
    } // end getBpelDocument

    /**
     * Returns an InputStream containing the Compiled BPEL Process (CBP)
     */
    public InputStream getCBPInputStream() {
        //System.out.println("getCBPInputStream called");
        // Find the CBP file - it has the same name as the BPEL process and lives in the same
        // directory as the process file
        String cbpFileName = null;
        try {
            String fileName = getRelativePath( getDirectory(), getBPELFile() );
            cbpFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".cbp";
        } catch (Exception e ) {
        	// IOException trying to fetch the BPEL file name
            if(__log.isDebugEnabled()) {
                __log.debug("Unable to calculate the file name for BPEL process: " +
                                   implementation.getProcessDefinition().getName(), e);
                return null;
            } // end if
        } // end try
        File cbpFile = new File( getDirectory(), cbpFileName );
        if( cbpFile.exists() ) {
            // Create an InputStream from the cbp file...
            try {
                return new FileInputStream( cbpFile );
            } catch ( Exception e ) {
                if(__log.isDebugEnabled()) {
                    __log.debug("Unable to open the cbp file for BPEL process: " +
                                       implementation.getProcessDefinition().getName(), e);
                }
            } // end try
        } else {
            // Cannot find the cbp file
            if(__log.isWarnEnabled()){
                __log.warn("Cannot find the cbp file for process: " + 
                                   implementation.getProcessDefinition().getName());
            }
        } // end if
        // TODO - need better exception handling if we can't open the cbp file for any reason
        return null;
    } // end getCBPInputStream

    /**
     * Return the WSDL Definition for a given PortType
     * @param portTypeName - the QName of the PortType
     */
    public Definition getDefinitionForPortType( QName portTypeName ) {
        //System.out.println("getDefinitionForPortType called for portType: " + portTypeName );
        // Find the named PortType in the list of WSDL interfaces associated with this BPEL Process
        Collection<WSDLInterface> theInterfaces = implementation.getProcessDefinition().getInterfaces();
        for( WSDLInterface wsdlInterface : theInterfaces ) {
            if ( wsdlInterface.getPortType().getQName().equals( portTypeName ) ) {
                // Extract and return the Definition associated with the WSDLDefinition...
                return wsdlInterface.getWsdlDefinition().getDefinition();
            } // end if
        } // end for
        return null;
    } // end getDefinitionforPortType

    /**
     * Returns a WSDL Definition for a given Service QName
     * 
     * 22/05/2008 - it is very unclear what this service QName is really meant to be.
     * From the handling of the deploy.xml file by the current ODE code, it seems that the key link
     * is from the Service QName to the PartnerLink name (done in the deploy.xml file).
     * 
     * The curious part about this is that the QName for the service is ONLY defined in deploy.xml file
     * and does not appear to relate to anything else, except for the QName of the PartnerLink
     * 
     * The PartnerLink name is the same as the name of the SCA service (or reference) which in turn points
     * at the PartnerLinkType which in turn points at an (WSDL) interface definition.
     */
    public Definition getDefinitionForService(QName serviceQName ) {
    	//System.out.println("getDefinitionForService called for Service: " + serviceQName );
        if(__log.isDebugEnabled()){
            __log.debug("getDefinitionforService called for service: " + serviceQName );
        }
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns the date of deployment of the process
     * - for SCA returns the date at which this object was created
     */
    public Date getDeployDate() {
        //System.out.println("getDeployDate called");
        return deployDate;
    }

    /**
     * Returns userid of deployer 
     * - always "SCA Tuscany" for Tuscany...
     */
    public String getDeployer() {
        //System.out.println("getDeployer called");
        return "SCA Tuscany";
    } // end getDeployer

    /**
     * Returns a list of the files in the directory containing the BPEL Process
     */
    public List<File> getFiles() {
        //System.out.println("getFiles called");
        File theDir = getDirectory();
        List<File> theFiles = Arrays.asList( (File[]) theDir.listFiles() );
        // TODO recurse into subdirectories
        return theFiles;
    } // end getFiles

    /**
     * Returns a Map containing all the "invoke endpoints" - for which read "SCA references"
     * The map is keyed by partnerLink name and holds Endpoint objects 
     * 
     * TODO deal with service callbacks on bidirectional services
     */
    public Map<String, Endpoint> getInvokeEndpoints() {
        //System.out.println("getInvokeEndpoints called");
        if( invokeEndpoints == null ) {
            invokeEndpoints = new HashMap<String, Endpoint>();
            // Get a collection of the references
            List<Reference> theReferences = implementation.getReferences();
            // Create an endpoint for each reference, using the reference name as the "service"
            // name, combined with http://tuscany.apache.org to make a QName
            for( Reference reference : theReferences ) {
                invokeEndpoints.put( reference.getName(), 
                                     new Endpoint( new QName( TUSCANY_NAMESPACE, reference.getName() ), "ReferencePort"));
            } // end for
        } // end if
        return invokeEndpoints;
    } // end getInvokeEndpoints

    /**
     * Returns the name of the directory containing the BPEL files
     */
    public String getPackage() {
        //System.out.println("getPackage called");
        File theDir = getDirectory();
        return theDir.getName();
    } // end getPackage

    /**
     * Return the BPEL Process ID - which is the Process QName appended "-versionnumber"
     */
    public QName getProcessId() {
        //System.out.println("getProcessId called");
        QName processType = getType();
        QName processID = new QName( processType.getNamespaceURI(), 
                                     processType.getLocalPart() + "-" + getVersion() );
        return processID;
    } // end getProcessID

    /**
     * TODO - What are properties?
     */
    public Map<QName, Node> getProperties() {
        //System.out.println("getProperties called");
        if ( properties == null ) {
            properties = new HashMap<QName, Node>();
        } // end if
        return properties;
    } // end getProperties

    /**
     * Returns a Map containing all the "provide endpoints" - for which read "SCA services"
     * The map is keyed by partnerLink name and holds Endpoint objects 
     * 
     * TODO deal with reference callbacks on bidirectional references
     */
    public Map<String, Endpoint> getProvideEndpoints() {
        //System.out.println("getProvideEndpoints called");
        if( provideEndpoints == null ) {
            provideEndpoints = new HashMap<String, Endpoint>();
            // Get a collection of the references
            List<Service> theServices = implementation.getServices();
            // Create an endpoint for each reference, using the reference name as the "service"
            // name, combined with http://tuscany.apache.org to make a QName
            for( Service service : theServices ) {
                provideEndpoints.put( service.getName(), 
                                      new Endpoint( new QName( TUSCANY_NAMESPACE, service.getName() ), "ServicePort"));
            } // end for
        } // end if
        return provideEndpoints;
    } // end getProvideEndpoints

    /**
     * Return the process state
     */
    public ProcessState getState() {
        //System.out.println("getState called");
        return processState;
    }

    /**
     * Returns the QName of the BPEL process
     */
    public QName getType() {
        //System.out.println("getType called");
        return implementation.getProcess();
    }

    /**
     * Gets the process Version number
     * - current code does not have versions for BPEL processes and always returns "1"
     */
    public long getVersion() {
        //System.out.println("getVersion called");
        return 1;
    }

    /**
     * Returns true if the supplied event type is enabled for any of the scopes in the provided
     * List.  These events are "ODE Execution Events" and there is a definition of them on this
     * page:  http://ode.apache.org/user-guide.html#UserGuide-ProcessDeployment
     * 
     * For the present Tuscany does not support manipulating the event enablement and always
     * returns that the event is not enabled
     * @param scopeNames - list of BPEL process Scope names
     * @param type - the event type
     */
    public boolean isEventEnabled(List<String> scopeNames, TYPE type) {
        //System.out.println("isEventEnabled called with scopeNames: " + 
        //		            scopeNames + " and type: " + type );
        return false;
    } // end isEventEnabled

    /**
     * Returns whether the process is persisted in the store
     * 
     * Returns false for SCA configuration 
     * - returning true causes problems in communicating with the BPEL process
     */
    public boolean isTransient() {
        //System.out.println("isTransient called");
        return false;
    } // end isTransient

    /**
     * Compiles a BPEL process file into a compiled form CBP file in the main directory 
     * (ie same directory as the BPEL process file)
     * @param bpelFile - the BPEL process file
     */
    private void compile( File bpelFile ) {
        // Set up the compiler
        BpelC compiler = BpelC.newBpelCompiler();
        // Provide a null set of initial properties for now
        Map<QName, Node> processProps = new HashMap<QName, Node>(); 
        Map<String, Object> compileProps = new HashMap<String, Object>();
        compileProps.put( BpelC.PROCESS_CUSTOM_PROPERTIES, processProps );
        compiler.setCompileProperties( compileProps );
        compiler.setBaseDirectory( getDirectory() );

        // Run the compiler and generate the CBP file into the given directory
        try {
            compiler.compile( bpelFile );
        } catch (IOException e) {
            if(__log.isDebugEnabled()) {
                __log.debug("Compile error in " + bpelFile, e);
            }
            // TODO - need better exception handling here
        } // end try
    } // end compile

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
        try {
            File theProcess = new File(URI.create(implementation.getProcessDefinition().getLocation()));
            return theProcess;
        } catch( Exception e ) {
            if(__log.isDebugEnabled()) {
                __log.debug("Exception converting BPEL file URL to an URI: " + e );
            }
        } // end try
        return null;
    } // end getBPELFile

    /**
     * Gets the relative path of a file against a directory in its hierarchy
     * @param base - the base directory
     * @param path - the file
     * @return
     * @throws IOException
     */
    private String getRelativePath(File base, File path) throws IOException {
        String basePath = base.getCanonicalPath();
        String filePath = path.getCanonicalPath();
        if (!filePath.startsWith(basePath)) {
            throw new IOException("Invalid relative path: base=" + base + " path=" + path);
        }
        String relative = filePath.substring(basePath.length());
        if (relative.startsWith(File.separator)) {
            relative = relative.substring(1);
        }
        return relative;
    } // end getRelativePath

    //-----------------------------------------------------------------------------
    // other public APIs which ProcessConfImpl displays which are not in ProcessConf interface

    public List<String> getMexInterceptors(QName processId) {
        System.out.println("getMexInterceptors for processID: " + processId );
        return null;
    }

    public void setTransient(boolean t) {
        System.out.println("setTransient called with boolean: " + t );
    }

    public List<Element> getExtensionElement(QName arg0) {
        return Collections.emptyList();
    }
    // end of other public APIs
    //-----------------------------------------------------------------------------

} // end class TuscanyProcessConfImpl
