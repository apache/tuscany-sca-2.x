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
