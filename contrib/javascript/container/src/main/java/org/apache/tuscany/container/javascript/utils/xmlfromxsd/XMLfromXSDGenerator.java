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
package org.apache.tuscany.container.javascript.utils.xmlfromxsd;

/**
 * This class provides a command line tooling functinality for creating an XML
 * instance from an input XSD.
 */
public class XMLfromXSDGenerator {
    public static int generatorType = XMLGenerator.XMLBEANS_BASED;

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            new XMLfromXSDGenerator().generateXMLInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(getPrintUsageMessage());
        }
    }

    public void generateXMLInstance(String args[]) throws Exception {
        // create a configuration object to hold settings for this generation
        XMLfromXSDConfiguration config = new XMLfromXSDConfiguration();

        // create an argument processor to process the input arguments
        CmdLineArgsProcessor argsProcessor = new CmdLineArgsProcessor();

        // configure the args processor with the 'config' object that must be populated
        // with values from the proecessed input arguments
        argsProcessor.setArgsHandler(config);

        // set the usage message to be output by the args processor if input arguments are not proper
        argsProcessor.setPrintUsageMessage(getPrintUsageMessage());

        // start processing the arguments
        argsProcessor.processArgs(args);

        // now that the configuration settings are populated from the input arguments
        // instantiate the xmlfromsdogenerator with this config object

        XMLGenerator generator = new XMLGeneratorFactory().createGenerator(generatorType);
        // XBbasedXMLGenerator generator = new XBbasedXMLGenerator(config);

        // generate the xml instance
        generator.generateXML(config);
    }

    protected static String getPrintUsageMessage() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("Usage arguments:\n");
        strBuffer.append("  [ -rns <root element namespace URI> ]\n");
        strBuffer.append("  [ -rn <root element name> ]\n");
        strBuffer.append("  [ -xsd <xsd file> ]\n");
        strBuffer.append("  [ -of <output xml filename> ]\n");
        strBuffer.append("  [ -o <output location> ]\n");
        strBuffer.append("  [ -st <name of the schema type to be instantiated as xml> ]\n");
        strBuffer.append("  [ -stn <namespace URI of the schema type> ]\n");
        strBuffer.append("  [ -sd (provide this flag if sample data is to be generated)\n");

        return strBuffer.toString();
    }

}
