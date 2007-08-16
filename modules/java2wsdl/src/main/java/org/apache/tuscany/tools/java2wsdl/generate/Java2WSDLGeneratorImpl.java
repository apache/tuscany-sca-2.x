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

import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.axiom.om.OMElement;
import org.apache.ws.java2wsdl.Java2WSDL;
import org.apache.ws.java2wsdl.utils.Java2WSDLCommandLineOption;
import org.apache.ws.java2wsdl.utils.Java2WSDLCommandLineOptionParser;
import org.apache.ws.java2wsdl.utils.Java2WSDLOptionsValidator;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is an implementation of the Java2WSDLGenerator facade. This
 * implementation is a decorator around the Axis2 implementation of the
 * Java2WSDL conversion. The WSDL generation is divided into phases that are
 * stringed up as a template method. The phases are - User Input Validation -
 * WSDL Java Model Generation - Serialization of WSDL Java Model The function of
 * each phase is accomplished by delegation to the appropriate classes in Axis2.
 * At the start and end of each phase an event is published to subcribers
 * denoting the start and end of the phase. Such a spliting up of the Java2WSDL
 * conversion into phases has been designed to enable interceptors to modify the
 * model or apply transformations to the output. Typically the interceptors can
 * subscribe to the start and end events of these phases and hence be able to
 * intercept. Note: This class contains substantial AXIS2 Java2WSDL code
 * refactored into it. These will be removed as and when the Axis2 code is
 * fixed.
 */
public class Java2WSDLGeneratorImpl implements Java2WSDLGenerator, TuscanyJava2WSDLConstants {
    private List<WSDLGenListener> genPhaseListeners = new Vector<WSDLGenListener>();
    private GenerationParameters genParams = null;
    private Map<String, Java2WSDLCommandLineOption> commandLineOptions = null;
    private TuscanyJava2WSDLBuilder java2WsdlBuilder;
    private OutputStream outputStream = null;

    public Java2WSDLGeneratorImpl() {

    }

    private void multicastGenPhaseCompletionEvent(int genPhase) {
        WSDLGenEvent event = new WSDLGenEvent(this, genPhase);
        Iterator iterator = genPhaseListeners.iterator();
        while (iterator.hasNext()) {
            ((WSDLGenListener)iterator.next()).WSDLGenPhaseCompleted(event);
        }
    }

    private void initJava2WSDLBuilder() throws Exception {
        // Now we are done with loading the basic values - time to create the
        // builder
        java2WsdlBuilder = new TuscanyJava2WSDLBuilder(genParams);
    }

    protected boolean validateInputArgs(String[] args) {
        boolean isValid = true;
        Java2WSDLCommandLineOptionParser parser = new Java2WSDLCommandLineOptionParser(args);
        if (parser.getAllOptions().size() == 0) {
            Java2WSDL.printUsage();
            isValid = false;
        } else if (parser.getInvalidOptions(new Java2WSDLOptionsValidator()).size() > 0) {
            Java2WSDL.printUsage();
            isValid = false;
        }

        if (isValid) {
            commandLineOptions = parser.getAllOptions();
        }

        return isValid;
    }

    public boolean buildWSDLDocument() throws Exception {
        boolean isComplete = true;
        initJava2WSDLBuilder();
        java2WsdlBuilder.buildWSDL();

        return isComplete;
    }

    public boolean serializeWSDLDocument() throws Exception {
        boolean isComplete = true;

        if (getOutputStream() == null) {
            setOutputStream(genParams.getOutputFileStream());
        }

        // transform the OMElement
        OMElement om = java2WsdlBuilder.getWsdlDocument();
        javax.xml.stream.XMLStreamReader stream = om.getXMLStreamReader();

        org.apache.tuscany.sca.databinding.xml.XMLStreamReader2Node xform =
            new org.apache.tuscany.sca.databinding.xml.XMLStreamReader2Node();

        Node node = xform.transform(stream, null);

        Document doc = null;
        try {
            doc = (Document)node;
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Didn't get back a Document DOM object");
        }

        // pretty-print wsdl document
        OutputFormat format = new OutputFormat(doc);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        XMLSerializer serializer = new XMLSerializer(getOutputStream(), format);
        serializer.serialize(doc);

        return isComplete;

    }

    /*
     * This is the template method that splits the Java2WSDL generation cycle
     * into phase / steps.
     * 
     * @see tuscany.tools.Java2WSDLGeneratorIfc#generateWSDL(java.lang.String[])
     */
    public void generateWSDL(Map commandLineOptions) {
        try {
            // load the user options into an easy to access abstraction
            genParams = new GenerationParameters(commandLineOptions);

            // if the WSDL Model generation was successul
            if (buildWSDLDocument()) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateWSDL(String[] args) {
        // if the argument input are found to be valid
        if (validateInputArgs(args)) {
            // multicast event for input args validation complete
            multicastGenPhaseCompletionEvent(WSDLGenListener.INPUT_ARGS_VALIDATION);
            generateWSDL(commandLineOptions);
        }
    }

    public void addWSDLGenListener(WSDLGenListener l) {
        genPhaseListeners.add(l);

    }

    public void removeWSDLGenListener(WSDLGenListener l) {
        genPhaseListeners.remove(l);
    }

    public Map getCommandLineOptions() {
        return commandLineOptions;
    }

    public void setCommandLineOptoins(Map cmdLineOpts) {
        commandLineOptions = cmdLineOpts;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outStream) {
        outputStream = outStream;
    }

    public TuscanyJava2WSDLBuilder getJava2WsdlBuilder() {
        return java2WsdlBuilder;
    }

    public void setJava2WsdlBuilder(TuscanyJava2WSDLBuilder java2WsdlBuilder) {
        this.java2WsdlBuilder = java2WsdlBuilder;
    }

    // 
    // Works recursively with node's entire subtree
    // There's no tie to fields in this object so I made this public.
    // 
    public static void removeTextNodes(Node node) {

        if (node == null)
            return;

        if (node.getNodeType() == Node.TEXT_NODE) {
            node.getParentNode().removeChild(node);
        } else {
            int origNumNodes;
            NodeList children = null;
            do {
                children = node.getChildNodes();
                origNumNodes = children.getLength();

                for (int i = 0; i < origNumNodes; i++) {
                    removeTextNodes(children.item(i));
                }
            } while (node.getChildNodes().getLength() != origNumNodes);
        }
    }

    protected void printGenerationMessage() {
        System.out.println("");
        System.out.println("Generating " + genParams.getOutputFile()
            + " from Java class "
            + genParams.getSourceClassName()
            + ".");
        System.out.println("");
    }
}
