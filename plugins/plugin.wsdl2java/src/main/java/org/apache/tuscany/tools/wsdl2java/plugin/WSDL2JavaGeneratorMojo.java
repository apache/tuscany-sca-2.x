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
package org.apache.tuscany.tools.wsdl2java.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tuscany.tools.wsdl2java.generate.WSDL2JavaGenerator;

/**
 * @version $Rev$ $Date$
 * @goal generate
 * @phase generate-sources
 * @description Generate SDO interface classes from an XML Schema
 */
public class WSDL2JavaGeneratorMojo extends AbstractMojo {
    /**
     * The directory containing wsdl files; defaults to ${basedir}/src/main/wsdl
     * @parameter expression="${basedir}/src/main/wsdl"
     */
    private String wsdlDir;

    /**
     * Name of the wsdl file; if omitted all files in the directory are processed
     * @parameter
     */
    private File wsdlFile;

    /**
     * The Java package to generate into. By default the value is derived from the schema URI.
     *
     * @parameter
     */
    private String javaPackage;

    /**
     * The directory to generate into; defaults to ${project.build.directory}/wsdl2java-source
     *
     * @parameter expression="${project.build.directory}/wsdl2java-source"
     */
    private String targetDirectory;
    
    /**
     * The directory to generate into; defaults to ${project.build.directory}/wsdl2java-source
     *
     * @parameter 
     */
    private WSDLFileOption[] wsdlFiles;

    /**
     * @parameter expression="${project.compileSourceRoots}"
     * @readonly
     */
    private List compilerSourceRoots;

    public void execute() throws MojoExecutionException {
        
        if(null != wsdlFiles){
            for(int i=0; i< wsdlFiles.length ; ++i ){
                System.err.println("wsdlFiles" + wsdlFiles[i].getFileName());
                WSDLFileOption wf = wsdlFiles[i];

                if(null == wf.getTargetDirectory())
                    wf.setTargetDirectory(targetDirectory);
                if(null == wf.getJavaPackage()){
                    wf.setJavaPackage(javaPackage);
                }
                if(wf.getFileName() == null || wf.getFileName().length() ==0){
                    throw new MojoExecutionException("no fileName specfied for wsdl.");    
                }
                if(!wf.getFileName().canRead() || !wf.getFileName().isFile()){

                    throw new MojoExecutionException("file can not be read:"+wf.getFileName());    
                }

            }
        }else{


            
            if (wsdlFile == null) {
             
             File[] files = new File(wsdlDir).listFiles(FILTER);
             
             wsdlFiles=   new WSDLFileOption[files.length];
             for(int i= files.length -1; i> -1; --i){
                 
                 
                 wsdlFiles[i] = new WSDLFileOption();
                 wsdlFiles[i].setFileName(files[i]);
                 wsdlFiles[i].setJavaPackage(javaPackage);
                 wsdlFiles[i].setPorts(null);
                 wsdlFiles[i].setTargetDirectory(targetDirectory);
                 
                 
             }
                
            } else {
                wsdlFiles=   new WSDLFileOption[]{new WSDLFileOption()};
                wsdlFiles[0].setFileName(wsdlFile);
                wsdlFiles[0].setJavaPackage(javaPackage);
                wsdlFiles[0].setPorts(null);
                wsdlFiles[0].setTargetDirectory(targetDirectory);
            }
        }
        
        int genOptions = 0;

        for (int i = 0; i < wsdlFiles.length; i++) {
            File file = wsdlFiles[i].getFileName();
            File marker = new File(targetDirectory, ".gen#" + file.getName()+".wsdl2java");
            if (file.lastModified() > marker.lastModified()) {
                getLog().info("Generating Java service interfaces from " + file);
                WSDL2JavaGenerator.generateFromWSDL(file.toString(), wsdlFiles[i].getPorts(), wsdlFiles[i].getTargetDirectory(), wsdlFiles[i].getJavaPackage(), null, genOptions);
            }
            try {
                marker.createNewFile();
            } catch (IOException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
            marker.setLastModified(System.currentTimeMillis());
        }

        compilerSourceRoots.add(targetDirectory);
    }

    private static final FileFilter FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return (pathname.isFile() || !pathname.isHidden());
        }
    };
}