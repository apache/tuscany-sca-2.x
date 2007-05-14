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
package org.apache.tuscany.sca.itest.generate;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;

import org.apache.tuscany.generate.GenerateFactory;
import org.apache.tuscany.generate.GenerateType;
import org.apache.tuscany.generate.TemplateType;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

/**
 * Generates test files based on the information in a configuration files (generate.xml)
 * and a set of velocity templates. The process is
 *
 * for each template
 *     for each file
 *         add a line to pom for code gen
 *         include the factory into the composite
 *         for each type
 *             add client iface method
 *             add client impl method
 *             add service iface method
 *             add service impl method
 *             add test method
 *             add wsdl type and method
 *
 * @version $Rev$ $Date$
 */
public class Generate {
	
    /**
     * Does all the hard work of running the velocity templates against the 
     * the list of types to test. Both the list of templates and the list of 
     * XSD files is held in the configuration file (generate.xsd) which lives in the
     * resources/generate directory of the project being generated. 
     *
     * @param projectBuildDir the path to the target dir of the project being generated. 
     */
    public static void generate(String projectBuildDir) {
        System.out.println(">> Building project from dir: " + projectBuildDir);
        FileInputStream fis = null;

        try {
            // Load the config file into a stream
            fis = new FileInputStream(projectBuildDir + "/classes/generate/generate.xml");

            // Load the stream into SDO
            // We are just using SDO as a conveniet way to parse the XML config file
            HelperContext scope       = SDOUtil.createHelperContext();
            GenerateFactory.INSTANCE.register(scope);
            XMLDocument xmlDoc        = scope.getXMLHelper().load(fis);
            GenerateType generateType = (GenerateType)xmlDoc.getRootObject();

            // Get the file list. This is the list of XSD that is passed into the 
            // the velocity templates. Each confiured file holds a list of types
            // that the velocity templates expand into appropriate methods and method calls           
            List fileList = generateType.getInputFile();
            
            //Intialise velocity ready to generate the various files
            Properties p = new Properties();
            p.setProperty("file.resource.loader.path", projectBuildDir + "/classes/generate");            
            Velocity.init(p);
            VelocityContext context = new VelocityContext();
            context.put("fileList", fileList);
            
            List templateList = generateType.getTemplate();

            // For each velocity template in the template list pass in the XSD file list
            for ( Object item: templateList){
            	TemplateType template = (TemplateType)item;
                context.put("template", template);
            	String tmp = template.getTemplateName();
            	String filename = projectBuildDir + "/" + template.getTemplateTargetDir() + "/" + tmp.substring(0,tmp.length() - 3);
                FileWriter fw = new FileWriter(filename);
            	System.out.println(">> Processing " + template.getTemplateName() + " to " + filename);
                Velocity.mergeTemplate(template.getTemplateName(), context, fw );
                fw.flush();
                fw.close();
            } 

        } catch (Exception e) {
            System.out.println("Exception : " + e.toString());
            e.printStackTrace();
            return;
        }        
    }

    /**
     * The mainline
     * 
     * @param args the target directory where project in which files are being generated
     */
    public static void main(String[] args) {
        Generate gen = new Generate();
        gen.generate(args[0]);
    }

}
