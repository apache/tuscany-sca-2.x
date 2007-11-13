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

public class WSDLFileOption {
    /**
     * Name of the wsdl file; if omitted all files in the directory are processed
     * 
     */
    private File fileName;

    /**
     * The Java package to generate into. By default the value is derived from the schema URI.
     *
     * 
     */
    private String javaPackage;

    /**
     * The directory to generate into; defaults to ${project.build.directory}/wsdl2java-source
     *
     * 
     */
    private String targetDirectory;

    /**
     * @parameter expression="${project.compileSourceRoots}"
     * @readonly
     */

    private String  ports[];

    /**
     * @parameter expression="${project.compileSourceRoots}"
     * @readonly
     */

    
   public WSDLFileOption(){}

    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

 
    public String[] getPorts() {
        return ports;
    }

    public void setPorts(String[] ports) {
        this.ports = ports;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public File getFileName() {
        return fileName;
    }

    public void setFileName(File fileName) {
        this.fileName = fileName;
    }

}
