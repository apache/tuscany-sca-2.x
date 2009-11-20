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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.compiler.BpelC;
import org.apache.ode.bpel.evt.BpelEvent.TYPE;
import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.EndpointReference;
import org.apache.ode.bpel.iapi.ProcessConf;
import org.apache.ode.bpel.iapi.ProcessState;
import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.databinding.SimpleTypeMapper;
import org.apache.tuscany.sca.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A Tuscany implementation of the ODE Process Conf
 * 
 * @version $Rev$ $Date$
 */
public class TuscanyProcessConfImpl implements ProcessConf {
    private final Log __log = LogFactory.getLog(getClass());

    private BPELImplementation implementation;
    private RuntimeComponent component;
    private Map<String, Endpoint> invokeEndpoints = null;
    private Map<String, Endpoint> provideEndpoints = null;
    private Map<QName, Node> properties = null;
    private ProcessState processState;
    private Date deployDate;
    
    private File theBPELFile;
    // Marks whether the BPEL file was rewritten (eg for initializer statements)
    private boolean rewritten = false;

    private final SimpleTypeMapper mapper = new SimpleTypeMapperImpl();
    private final String TUSCANY_NAMESPACE = Base.SCA11_TUSCANY_NS;

    /**
     * Constructor for the ProcessConf implementation
     * @param theImplementation the BPEL implementation for which this is the ProcessConf
     * @param component - the SCA component which uses the implementation
     */
    public TuscanyProcessConfImpl( BPELImplementation theImplementation, RuntimeComponent component ) {
        //System.out.println("New TuscanyProcessConfImpl...");
        this.implementation = theImplementation;
        this.component = component;

        processState = ProcessState.ACTIVE;
        deployDate = new Date();

        // Compile the process
        compile( getBPELFile() );
    } // end TuscanyProcessConfImpl constructor
    
    public void stop() {
    	// If the BPEL file was rewritten, destroy the rewritten version of it so that
    	// it is not used again. Also delete the related compiled cbp file
    	if( rewritten ) {
    		try {
    			String cbpName = theBPELFile.getCanonicalPath();
    			// Remove the "bpel_tmp" suffix and add "cbp"
    			if ( cbpName.endsWith("bpel_tmp") ) {
    				cbpName = cbpName.substring( 0, cbpName.length() - 8) + "cbp";
    				File cbpFile = new File( cbpName );
    				if ( cbpFile.exists() ) cbpFile.delete();
    			} // end if
    		} catch (Exception e ) {
    			// Do nothing with an exception
    		} // end try
    		theBPELFile.delete();
    	} // end if
    	
    } // end method stop

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
            String location = this.implementation.getProcessDefinition().getLocation();
            URI locationURI = new URI(null, location, null);
            File processFile = new File(locationURI);
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

        File cbpFile = getCBPFile();
        if( cbpFile == null ) return null;

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
     * Gets the File object for the CBP file for this BPEL Process
     * @return - the File object for the CBP file
     */
    private File getCBPFile() {
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
    	return cbpFile;
    } // end getCBPFile

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
     * 0..1 multiplicity references are not included in the returned Map (it is as if the reference is not there...)
     * TODO deal with multiplicity 0..n and 1..n
     * TODO deal with service callbacks on bidirectional services
     */
    public Map<String, Endpoint> getInvokeEndpoints() {
        if( invokeEndpoints == null ) {
            invokeEndpoints = new HashMap<String, Endpoint>();
            // Get a collection of the component references - note that this includes "pseudo-references" for any
            // services that have a callback interface
            List<ComponentReference> theReferences = component.getReferences();
            //List<Reference> theReferences = implementation.getReferences();
            // Create an endpoint for each reference, using the reference name combined with 
            // http://tuscany.apache.org to make a QName
            // Note that the key used for this put operation MUST be the name of one of the partnerLinks of the
            // BPEL process.  The SCA reference MAY have an alias for the name (can be given using the sca-bpel:reference
            // element, if present) and this alias must not be used
            for( Reference reference : theReferences ) {
            	String partnerlinkName = implementation.getReferencePartnerlinkName( reference.getName() );
            	// Check that there is at least 1 configured SCA endpointReference for the reference, since it is
            	// possible for 0..1 multiplicity references to have no SCA endpointReferences configured
            	List<org.apache.tuscany.sca.assembly.EndpointReference> eprs = reference.getEndpointReferences();
            	String eprCount =  Integer.toString( eprs.size() );
                invokeEndpoints.put( partnerlinkName, 
                                     new Endpoint( new QName( TUSCANY_NAMESPACE, reference.getName() ), eprCount)); 
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
            String componentURI = component.getURI();
            // Get a collection of the services - note that the Component services include additional
            // "pseudo-services" for each reference that has a callback...
            
            List<ComponentService> theServices = component.getServices();
            // Create an endpoint for each service, using the service name combined with 
            // http://tuscany.apache.org to make a QName
            // Note that the key used for this put operation MUST be the name of one of the partnerLinks of the
            // BPEL process.  The SCA service MAY have an alias for the name (can be given using the sca-bpel:service
            // element, if present) and this alias must not be used
            for( ComponentService service : theServices ) {
            	String partnerlinkName = implementation.getServicePartnerlinkName( service.getName() );
            	// MJE 14/07/2009 - added componentURI to the service name to get unique service name
                provideEndpoints.put( partnerlinkName, 
                                      new Endpoint( new QName( TUSCANY_NAMESPACE, componentURI + service.getName() ), 
                                    		        "ServicePort"));
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
     * Tuscany currently uses:
     * - instanceLifecycle events in order to establish the relationship of MessageExchange objects
     *   to the BPEL Process instances 
     * @param scopeNames - list of BPEL process Scope names
     * @param type - the event type
     */
    public boolean isEventEnabled(List<String> scopeNames, TYPE type) {
        if( type == TYPE.dataHandling ) return false;
    	if( type == TYPE.activityLifecycle ) return false;
    	if( type == TYPE.scopeHandling ) return true;
    	if( type == TYPE.instanceLifecycle ) return true;
    	if( type == TYPE.correlation ) return true;
    	return false;
    } // end isEventEnabled

    /**
     * Returns whether the process is persisted in the store
     * 
     * Returns false for SCA configuration 
     * - returning true causes problems in communicating with the BPEL process
     */
    public boolean isTransient() {
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
        
        // Inject any property values
        bpelFile = injectPropertyValues( bpelFile );

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
     * Adds the values for SCA declared properties to the BPEL process.
     * The values for the properties are held in the SCA RuntimeComponent supplied to this
     * TuscanyProcessConfImpl.
     * The properties map to <variable/> declarations in the BPEL process that are specifically
     * marked with @sca-bpel:property="yes"
     * @param bpelFile the file containing the BPEL process
     * @return the (updated) file containing the BPEL process
     */
    private File injectPropertyValues( File bpelFile ) {
    	// Get the properties
    	List<ComponentProperty> properties = component.getProperties();
    	
    	// If there are no properties, we're done!
    	if( properties.size() == 0 ) return bpelFile;
    	
    	Document bpelDOM = readDOMFromProcess( bpelFile );
    	
    	for( ComponentProperty property : properties ) {
    		//System.out.println("BPEL: Property - name = " + property.getName() );
    		insertSCAPropertyInitializer( bpelDOM, property );
    	} // end for
    	
    	File bpelFile2 = writeProcessFromDOM( bpelDOM, 
    			                              getTransformedBPELFile( bpelFile) );
    	if( bpelFile2 != null ) {
    		theBPELFile = bpelFile2;
    		rewritten = true;
    		return bpelFile2;
    	} // end if 
    	
    	return bpelFile;
    } // end injectPropertyValues
    
    /**
     * Insert an initializer which supplies the value of an SCA property as specified by the
     * SCA Component using the BPEL process
     * @param bpelDOM - a DOM model representation of the BPEL process
     * @param property - an SCA ComponentProperty element for the property
     * This DOM model is updated, with an initializer being added for the BPEL variable
     * corresponding to the SCA property
     */
    private void insertSCAPropertyInitializer( Document bpelDOM, ComponentProperty property ) {
    	// Only insert a Property initializer where there is a value for the Property
    	if( property.getValue() == null ) return;
    	
    	Element insertionElement = findInitializerInsertionPoint( bpelDOM );
    	if( insertionElement == null ) return;
    	
    	Element initializer = getInitializerSequence( bpelDOM, property );
    	if( initializer == null ) return;
    	
    	// Insert the initializer sequence as the next sibling element of the insertion point
    	Element parent = (Element)insertionElement.getParentNode();
    	// Get the next sibling element, if there is one
    	Node sibling = insertionElement.getNextSibling();
    	while( sibling != null && sibling.getNodeType() != Node.ELEMENT_NODE ) {
    		sibling = sibling.getNextSibling();
    	} // end while
    	// Either insert at the end or before the next element
    	if ( sibling == null ) {
    		parent.appendChild( initializer );
    	} else {
    		parent.insertBefore( initializer, sibling );
    	} // end if
    	
    } // end insertSCAPropertyInitializer
    
    /**
     * Gets the variable initializer DOM sequence for a given property, in the context of a supplied
     * DOM model of the BPEL process
     * @param bpelDOM - DOM representation of the BPEL process
     * @param property - SCA Property which relates to one of the variables in the BPEL process
     * @return - a DOM model representation of the XML statements required to initialize the
     * BPEL variable with the value of the SCA property.
     */
    private Element getInitializerSequence( Document bpelDOM, ComponentProperty property ) {
    	// For an XML simple type (string, int, etc), the BPEL initializer sequence is:
    	// <assign><copy><from><literal>value</literal></from><to variable="variableName"/></copy></assign>
    	QName type = property.getXSDType();
    	if( type != null ) {
    		if( mapper.isSimpleXSDType( type ) ) {
    			// Simple types
    			String NS_URI = bpelDOM.getDocumentElement().getNamespaceURI();
    			String valueText = getPropertyValueText( property.getValue() );
    			Element literalElement = bpelDOM.createElementNS(NS_URI, "literal");
    			literalElement.setTextContent(valueText);
    			Element fromElement = bpelDOM.createElementNS(NS_URI, "from");
    			fromElement.appendChild(literalElement);
    			Element toElement = bpelDOM.createElementNS(NS_URI, "to");
    			Attr variableAttribute = bpelDOM.createAttributeNS(NS_URI, "variable");
    			variableAttribute.setValue( property.getName() );
    			toElement.setAttributeNode( variableAttribute );
    			Element copyElement = bpelDOM.createElementNS(NS_URI, "copy");
    			copyElement.appendChild(fromElement);
    			copyElement.appendChild(toElement);
    			Element assignElement = bpelDOM.createElementNS(NS_URI, "assign");
    			assignElement.appendChild(copyElement);
    			return assignElement;
    		} // end if
    		// TODO Deal with Properties which have a non-simple type
    	} else {
    		// TODO Deal with Properties which have an element as the type
    	} // end if
	
    	return null;
    } // end method getInitializerSequence
    
    /**
     * Gets the text value of a property that is a simple type
     * @param propValue - the SCA Property value
     * @return - the text content of the Property value, as a String
     */
    private String getPropertyValueText( Object propValue ) {
    	String text = null;
    	if( propValue instanceof Document ) {
    		Element docElement = ((Document)propValue).getDocumentElement();
    		if( docElement != null ){
    			Element valueElement = (Element)docElement.getFirstChild();
    			if( valueElement != null ) {
    				text = valueElement.getTextContent();
    			} // end if
    		} // end if
    	} // end if
    	
    	return text;
    } // end method getPropertyValueText
     
    private Element findInitializerInsertionPoint( Document bpelDOM ) {
    	// The concept is to find the first Activity child element of the BPEL process document
    	Element docElement = bpelDOM.getDocumentElement();
    	NodeList elements = docElement.getElementsByTagName("*");
    	
    	Element element;
    	for ( int i = 0 ; i < elements.getLength() ; i++ ) {
    		element = (Element)elements.item(i);
    		if( isInsertableActivityElement( element ) ) {
    			return element;
    		} // end if
    	} // end for
    	
    	return null;
    } // end method findInitializerInsertionPoint
    
    /**
     * A WS-BPEL activity can be any of the following:
     *  <receive>
     *  <reply>
     *  <invoke>
     *  <assign>
     *  <throw>
     *  <exit>
     *  <wait>
     *  <empty>
     *  <sequence>
     *  <if>
     *  <while>
     *  <repeatUntil>
     *  <forEach>
     *  <pick>
     *  <flow>
     *  <scope>
     *  <compensate>
     *  <compensateScope>
     *  <rethrow>
     *  <validate>
     *  <extensionActivity>
     *  A WS-BPEL start activity is a <receive> or <pick> with @create_instance="yes"
     */
    private static String	SEQUENCE_ELEMENT 	= "sequence";
    private static String	REPLY_ELEMENT 		= "reply";
    private static String	INVOKE_ELEMENT 		= "invoke";
    private static String	ASSIGN_ELEMENT 		= "assign";
    private static String	PICK_ELEMENT 		= "pick";
    private static String	RECEIVE_ELEMENT 	= "receive";
    private static String	FLOW_ELEMENT 		= "flow";
    private static String	SCOPE_ELEMENT 		= "scope";
    /**
     * Determine if an Element is a BPEL start activity element which can have an Assign
     * inserted following it
     * @param element - a DOM Element containing the BPEL activity
     * @return - true if the Element is a BPEL Activity element, false otherwise
     */
    private boolean isInsertableActivityElement( Element element ) {
    	String name = element.getTagName();
    	// For the present, only <receive/> and <pick/> elements with create_instance="yes" count 
    	// if( SEQUENCE_ELEMENT.equalsIgnoreCase(name) ) return true;
    	String start = element.getAttribute("createInstance");
    	if( start == null ) return false;
    	if( !"yes".equals(start) ) return false;
    	if( RECEIVE_ELEMENT.equalsIgnoreCase(name) ) return true;
    	if( PICK_ELEMENT.equalsIgnoreCase(name) ) return true;
    	return false;
    } // end method isActivityElement
    
    /**
     * Reads a BPEL Process file into a DOM Document structure
     * @param bpelFile - a File object referencing the BPEL process document
     * @return - a DOM Document structure representing the same BPEL process
     */
    private Document readDOMFromProcess( File bpelFile ) {
    	try {
	    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    	docFactory.setNamespaceAware(true);
	    	docFactory.setXIncludeAware(true);
	    	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	    	
	    	Document bpelDOM = docBuilder.parse( bpelFile );
	    	return bpelDOM;
    	} catch (Exception e) {
    		return null;
    	} // end try
    } // end method
    
    /**
     * Writes a BPEL Process file from a DOM Document structure representing the Process
     * @param bpelDOM - the DOM Document representation of the BPEL process
     * @param file - a File object to which the BPEL Process is to be written
     * @return
     */
    private File writeProcessFromDOM( Document bpelDOM, File file ) {
    	try {
	        // Prepare the DOM document for writing
	        Source source = new DOMSource( bpelDOM );
	
	        // Prepare the output file
	        Result result = new StreamResult(file);
	
	        // Write the DOM document to the file
	        Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.transform(source, result);
	    } catch (TransformerConfigurationException e) {
	    } catch (TransformerException e) {
	    	return null;
	    }
    	return file;
    } // end writeProcessFromDOM
    
    private File getTransformedBPELFile( File bpelFile ) {
    	String name = bpelFile.getName();
    	File parent = bpelFile.getParentFile();
    	File bpelFile2 = null;
    	try {
    		bpelFile2 = File.createTempFile(name, ".bpel_tmp", parent);
    	} catch (Exception e ){
    		
    	} // end try
    	return bpelFile2;
    } // end getTransformedBPELFile

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
    	if( theBPELFile != null ) return theBPELFile;
        try {
            String location = this.implementation.getProcessDefinition().getLocation();
            URI locationURI;
            if (location.indexOf('%') != -1) {
                locationURI = URI.create(location);
             } else {
                 locationURI = new URI(null, location, null);
             }
            File theProcess = new File(locationURI);
            theBPELFile = theProcess;
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
//        System.out.println("getMexInterceptors for processID: " + processId );
        return null;
    }

    public void setTransient(boolean t) {
//        System.out.println("setTransient called with boolean: " + t );
    }

    public List<Element> getExtensionElement(QName arg0) {
        return Collections.emptyList();
    }
    // end of other public APIs
    //-----------------------------------------------------------------------------

    /**
     * Get the size in bytes of the CBP file
     * @return - this size in bytes of the CBP file, 0 if the file cannot be found
     */
	public long getCBPFileSize() {
        File cbpFile = getCBPFile();
        if( cbpFile == null ) return 0;
        
		return cbpFile.length();
	} // end getCBPFileSize
	
	private final Set<CLEANUP_CATEGORY> successCategories = EnumSet.noneOf(CLEANUP_CATEGORY.class);
	private final Set<CLEANUP_CATEGORY> failureCategories = EnumSet.noneOf(CLEANUP_CATEGORY.class);

	public Set<CLEANUP_CATEGORY> getCleanupCategories(boolean instanceSucceeded) {
		if( instanceSucceeded ) return successCategories;
		else return failureCategories;
	}

	private final Map<String, String> emptyPropertyMap = new Hashtable<String, String>();
	public Map<String, String> getEndpointProperties(EndpointReference epr) {
		return emptyPropertyMap;
	}
	
	private final Map<QName, Node> emptyProcessProperties = new Hashtable<QName, Node>();
	public Map<QName, Node> getProcessProperties() {
		return emptyProcessProperties;
	}

	public boolean isCleanupCategoryEnabled(boolean instanceSucceeded,
			CLEANUP_CATEGORY category) {
		// TODO Currently returns false - should this be changed for some categories?
		return false;
	}

	public boolean isSharedService(QName serviceName) {
		// Tuscany does not share the service
		return false;
	}

} // end class TuscanyProcessConfImpl
