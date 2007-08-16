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
package org.apache.tuscany.tools.wsdl2java.generate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.apache.tuscany.sdo.generate.XSD2JavaGenerator;
import org.apache.tuscany.sdo.helper.HelperContextImpl;
import org.apache.tuscany.sdo.util.DataObjectUtil;
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XSDHelper;

public class WSDL2JavaGenerator {
    //Note: Dynamic SDO is defined as 0x4000 to avoid conflict with XSD2Java genOptions
    static protected final int DYNAMIC_SDO = 0x1;
    static protected final int GENERATE_SDO = 0x2;
    static protected final int VERBOSE_MODE = 0x4;
    static protected final String NO_GEN_PARM = "-noGenerate";
    static protected final String TARGET_DIR_PARM = "-targetDirectory";
    static protected final String JAVA_PACKAGE_PARM = "-javaPackage";
    static protected final String ALL_NAMESPACES_PARM = "-schemaNamespace all";

    /**
     * Generate Java interfaces from WSDL Usage arguments: [ -targetDirectory
     * <target-root-directory> ] [ -javaPackage <java-package-name> ]
     * <wsdl-file> For example: generate somedir/somefile.wsdl Basic options:
     * -targetDirectory Generates the Java source code in the specified
     * directory. By default, the code is generated in the same directory as the
     * input wsdl file. -javaPackage Overrides the Java package for the
     * generated classes. By default the package name is derived from the
     * targetNamespace of the WSDL definition being generated. For example, if
     * the targetNamespace is "http://www.example.com/simple", the default
     * package will be "com.example.simple".
     */
    public static void main(String args[]) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String portName = null;
        String targetDirectory = null;
        String wsdlJavaPackage = null;
        String xsdJavaPackage = null;
        String sdoGenArgs = null;
        String sdoGenArgsString = null;
        int genOptions = 0;

        int index = 0;
        for (; index < args.length && args[index].startsWith("-"); ++index) {
            if (args[index].equalsIgnoreCase("-port")) {
                portName = args[++index];
            } else if (args[index].equalsIgnoreCase("-targetDirectory")) {
                targetDirectory = args[++index];
            } else if (args[index].equalsIgnoreCase("-javaPackage")) {
                wsdlJavaPackage = args[++index];
            } else if (args[index].equalsIgnoreCase("-dynamicSDO")) {
                genOptions |= DYNAMIC_SDO;
            } else if (args[index].equalsIgnoreCase("-generateSDO")){
                genOptions |= GENERATE_SDO;
            } else if (args[index].equalsIgnoreCase("-sdoGenArgs")){
                sdoGenArgs = args[++index];
            } else if (args[index].equalsIgnoreCase("-verbose")){
               genOptions |= VERBOSE_MODE; 
            }
            // else if (...)
            else {
                printUsage();
                return;
            }
        }
        if (sdoGenArgs!=null && (GENERATE_SDO & genOptions)== 0){
            genOptions |= GENERATE_SDO;
        }
        
        if (targetDirectory == null) {
            targetDirectory = ".";
        }
        String wsdlFileName = args[index];
        if (wsdlFileName == null || ((DYNAMIC_SDO & genOptions)!=0 && (GENERATE_SDO & genOptions)!= 0)) {
            printUsage();
            return;
        }
        if (sdoGenArgs !=null){
            try {
                    File inFile = new File(sdoGenArgs).getAbsoluteFile();
                    FileReader inputFile = new FileReader(inFile);
                    BufferedReader bufRead = new BufferedReader(inputFile);
                    StringBuffer fileContents= new StringBuffer();
                    String line;
                    while ((line = bufRead.readLine())!=null){
                        fileContents.append(line + " ");
                    }
                    sdoGenArgsString = fileContents.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException(e);
                }
        } else {
                sdoGenArgsString = null;
        }
        
        if ((DYNAMIC_SDO & genOptions) != 0){
            generateDynamicFromWSDL(wsdlFileName, portName!=null? new String[]{portName}:null, targetDirectory, wsdlJavaPackage, xsdJavaPackage, genOptions);
        } else {
            generateFromWSDL(wsdlFileName, portName!=null? new String[]{portName}:null, targetDirectory, wsdlJavaPackage, genOptions, sdoGenArgsString);   
        }
        
    }
    
    @SuppressWarnings("unchecked")
    public static void generateFromWSDL(String wsdlFileName, String[] ports, String targetDirectory, String wsdlJavaPackage, int genOptions, String sdoGenArgsString)
    {
        try {        
                if (targetDirectory == null) {
                    targetDirectory = new File(wsdlFileName).getCanonicalFile().getParent();
                } else {
                    targetDirectory = new File(targetDirectory).getCanonicalPath();
                }
                          
                // Populate the typeMapping table that will be given to the Axis2 WSDL2Java 
                Map<QName, SDODataBindingTypeMappingEntry> typeMapping =
                                        new HashMap<QName, SDODataBindingTypeMappingEntry>();
                StringBuffer buildCommand = new StringBuffer();
                //build XSD command lines
                //build generic command for simple case & dynamic SDO
                buildCommand.append(ALL_NAMESPACES_PARM);
                if (sdoGenArgsString == null){
                     buildCommand.append(" " + TARGET_DIR_PARM + " " + targetDirectory);
                     if (wsdlJavaPackage != null && (genOptions & GENERATE_SDO)!=0){
                         buildCommand.append(" " + JAVA_PACKAGE_PARM + " " + wsdlJavaPackage);
                     }
                     if ((genOptions & GENERATE_SDO)==0){
                          buildCommand.append(" ");
                          buildCommand.append(NO_GEN_PARM);        
                     }
                } else {
                    buildCommand.append(" ");
                    buildCommand.append(sdoGenArgsString);
                }
                buildCommand.append(" ");
                buildCommand.append(wsdlFileName);
                String[] sdoGenCommand = buildCommand.toString().split("\\s+");  
                
                if ((genOptions & VERBOSE_MODE)!=0){
                        System.out.println("Options passed to XSD2Java: ");
                        for (int i=0; i<sdoGenCommand.length; i++){ System.out.println("\"" + sdoGenCommand[i] + "\"");}        
                }
                
                XSD2JavaGenerator codeGen = new XSD2JavaGenerator();
                try {
                        codeGen.generateFromXMLSchema( sdoGenCommand );  
                } catch (IllegalArgumentException e) {
                            System.out.println("Specified Invalid XSD2Java Arguments.\nFollow the XSD2Java usage, omitting the wsdl/xsd file argument.");
                            throw new IllegalArgumentException(e);
                }
                                    
                List packages = codeGen.getGeneratedPackageInfo();
                     
                for (Iterator iter = packages.iterator(); iter.hasNext();)
                {
                    XSD2JavaGenerator.GeneratedPackage packageInfo = (XSD2JavaGenerator.GeneratedPackage)iter.next();
                    for (Iterator iterClass = packageInfo.getClasses().iterator(); iterClass.hasNext();)
                    {
                        XSD2JavaGenerator.GeneratedPackage.PackageClassInfo classInfo = (XSD2JavaGenerator.GeneratedPackage.PackageClassInfo)iterClass.next();
                        SDODataBindingTypeMappingEntry typeMappingEntry;
                        if ((genOptions & DYNAMIC_SDO)==0){
                            typeMappingEntry = new SDODataBindingTypeMappingEntry(classInfo.getClassName(), classInfo.getAnonymous(), classInfo.getProperties());
                        } else {
                            // TO DO implement dynamic sdo case
                            typeMappingEntry = null;
                            System.out.println();
                        }                              
                        QName qname = new QName(packageInfo.getNamespace(),classInfo.getName());
                        typeMapping.put(qname, typeMappingEntry);
                    }          
                }
       
                JavaInterfaceGenerator codeGenerator = new JavaInterfaceGenerator(wsdlFileName,
                                                                                  ports,
                                                                                  targetDirectory,
                                                                                  wsdlJavaPackage,
                                                                                  typeMapping);
                codeGenerator.generate();
        } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void generateFromWSDL(String wsdlFileName, String targetDirectory,
            String wsdlJavaPackage,
            String xsdJavaPackage, int genOptions){
        String sdoGenArgsString = null;
        if (xsdJavaPackage != null){
            sdoGenArgsString = JAVA_PACKAGE_PARM + " " + xsdJavaPackage;
        }
        generateFromWSDL( wsdlFileName, null, targetDirectory, wsdlJavaPackage, genOptions, sdoGenArgsString);
        
    }
    
    public static void generateFromWSDL(String wsdlFileName, String[] ports,
                    String targetDirectory, String wsdlJavaPackage,
                    String xsdJavaPackage, int genOptions) {
        String sdoGenArgsString = null;
        if (xsdJavaPackage != null){
           sdoGenArgsString = JAVA_PACKAGE_PARM + " " + xsdJavaPackage;
        }
        generateFromWSDL( wsdlFileName, null, targetDirectory, wsdlJavaPackage, genOptions, sdoGenArgsString);
    }
    
    @SuppressWarnings("unchecked")
      public static void generateDynamicFromWSDL(String wsdlFileName, String[] ports,
                 String targetDirectory, String wsdlJavaPackage,
                 String xsdJavaPackage, int genOptions) 
     {

        // Initialize the SDO runtime
        DataObjectUtil.initRuntime();
        EPackage.Registry packageRegistry = new EPackageRegistryImpl(EPackage.Registry.INSTANCE);
        ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(packageRegistry);
        HelperContext context = new HelperContextImpl(extendedMetaData, false);
        XSDHelper xsdHelper = context.getXSDHelper();

        try {
            
            // Load the WSDL file
            File inputFile = new File(wsdlFileName).getAbsoluteFile();
            InputStream inputStream = new FileInputStream(inputFile);
            
            // Define SDO metadata
            xsdHelper.define(inputStream, inputFile.toURI().toString());

            if (targetDirectory == null) {
                targetDirectory = new File(wsdlFileName).getCanonicalFile().getParent();
            } else {
                targetDirectory = new File(targetDirectory).getCanonicalPath();
            }

            // Populate the typeMapping table that will be given to the Axis2 WSDL2Java 
            Map<QName, SDODataBindingTypeMappingEntry> typeMapping =
                                    new HashMap<QName, SDODataBindingTypeMappingEntry>();
            if (!packageRegistry.values().isEmpty()) {
                ResourceSet resourceSet = DataObjectUtil.createResourceSet();

                // Populate list of GenPackages and a map of GenClasses keyed by EClass 
                List<GenPackage> genPackages = new ArrayList<GenPackage>();
                Map<EClass, GenClass> genClasses = new HashMap<EClass, GenClass>();
                for (Iterator iter = packageRegistry.values().iterator(); iter.hasNext();) {
                    EPackage currentEPackage = (EPackage)iter.next();
                    String currentBasePackage = extractBasePackageName(currentEPackage, xsdJavaPackage);
                    String currentPrefix = CodeGenUtil.capName(currentEPackage.getName());

                    GenPackage currentGenPackage = createGenPackage(currentEPackage, currentBasePackage,
                                                                    currentPrefix, genOptions, resourceSet);
                    genPackages.add(currentGenPackage);
                    for (GenClass genClass : (List<GenClass>)currentGenPackage.getGenClasses()) {
                        genClasses.put(genClass.getEcoreClass(), genClass);
                    }

                }

                // Process all the SDO packages
                // Populate the qname -> interfaceName typeMapping table
                for (GenPackage currentGenPackage : genPackages) {
                    EPackage currentEPackage = currentGenPackage.getEcorePackage();
                    
                    // Populate the type mappings for all the complex types
                    for (GenClass genClass : (List<GenClass>)currentGenPackage.getGenClasses()) {
                        QName qname = new QName(extendedMetaData.getNamespace(currentEPackage),
                                                extendedMetaData.getName(genClass.getEcoreClass()));
                        String interfaceName = (DYNAMIC_SDO & genOptions) == DYNAMIC_SDO ? "commonj.sdo.DataObject" : currentGenPackage
                                .getInterfacePackageName()
                                + '.' + genClass.getInterfaceName();
                        SDODataBindingTypeMappingEntry typeMappingEntry =
                                new SDODataBindingTypeMappingEntry(interfaceName, false, null);
                        typeMapping.put(qname, typeMappingEntry);
                    }

                    // Process all the global XSD elements
                    EClass documentRoot = extendedMetaData.getDocumentRoot(currentEPackage);
                    if (documentRoot != null) {
                        for (EStructuralFeature element : (List<EStructuralFeature>)extendedMetaData
                            .getElements(documentRoot)) {
                            EClassifier elementType = element.getEType();
                            
                            // Handle a complex type
                            if (elementType instanceof EClass) {
                                EClass eClass = (EClass)elementType;

                                GenClass genClass = genClasses.get(elementType);
                                QName qname = new QName(extendedMetaData.getNamespace(currentEPackage),
                                        extendedMetaData.getName(element));
                                String interfaceName = (DYNAMIC_SDO & genOptions) == DYNAMIC_SDO ? "commonj.sdo.DataObject" : genClass
                                        .getGenPackage().getInterfacePackageName()
                                + '.' + genClass.getInterfaceName();
                                boolean anonymous = extendedMetaData.isAnonymous(eClass);
                                
                                // Build list of property class names
                                List<String> propertyClassNames=new ArrayList<String>();
                                for (EStructuralFeature feature : (List<EStructuralFeature>)eClass.getEStructuralFeatures()) {
                                    EClassifier propertyType = feature.getEType();
                                    if (propertyType instanceof EClass) {
                                        GenClass propertyGenClass = genClasses.get(propertyType);
                                        String propertyClassName = (DYNAMIC_SDO & genOptions) == DYNAMIC_SDO ? "commonj.sdo.DataObject"
                                                : propertyGenClass.getGenPackage().getInterfacePackageName() + '.'
                                                               + '.' + propertyGenClass.getInterfaceName();
                                        propertyClassNames.add(propertyClassName);
                                    } else if (propertyType instanceof EClassifier) {
                                        String propertyClassName = propertyType.getInstanceClass().getName();
                                        propertyClassNames.add(propertyClassName);
                                    }
                                }

                                SDODataBindingTypeMappingEntry typeMappingEntry = 
                                    new SDODataBindingTypeMappingEntry(interfaceName, anonymous, propertyClassNames);
                                typeMapping.put(qname, typeMappingEntry);
                                
                            } else {
                                
                                // Handle a simple type
                                QName qname = new QName(extendedMetaData.getNamespace(currentEPackage),
                                                        extendedMetaData.getName(element));
                                String className = elementType.getInstanceClass().getName();
                                SDODataBindingTypeMappingEntry typeMappingEntry = 
                                        new SDODataBindingTypeMappingEntry(className, false, null);
                                typeMapping.put(qname, typeMappingEntry);
                            }
                        }
                    }
                }
            }

            try {
                JavaInterfaceGenerator codeGenerator = new JavaInterfaceGenerator(wsdlFileName,
                                                                                  ports,
                                                                                  targetDirectory,
                                                                                  wsdlJavaPackage,
                                                                                  typeMapping);
                codeGenerator.generate();
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException(e);
            }

        } catch (IOException e) {
                e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static GenPackage createGenPackage(EPackage ePackage, String basePackage, String prefix,
                                              int genOptions, ResourceSet resourceSet) {
        GenModel genModel = ecore2GenModel(ePackage, basePackage, prefix, genOptions);

        URI ecoreURI = URI.createURI("file:///" + ePackage.getName() + ".ecore");
        URI genModelURI = ecoreURI.trimFileExtension().appendFileExtension("genmodel");

        Resource ecoreResource = resourceSet.createResource(ecoreURI);
        ecoreResource.getContents().add(ePackage);

        Resource genModelResource = resourceSet.createResource(genModelURI);
        genModelResource.getContents().add(genModel);

        return (GenPackage)genModel.getGenPackages().get(0);
    }

    public static GenModel ecore2GenModel(EPackage ePackage, String basePackage,
                                          String prefix, int genOptions) {
        GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
        genModel.initialize(Collections.singleton(ePackage));

        genModel.setRootExtendsInterface("");
        genModel.setRootImplementsInterface("commonj.sdo.DataObject");
        genModel.setRootExtendsClass("org.apache.tuscany.sdo.impl.DataObjectImpl");
        genModel.setFeatureMapWrapperInterface("commonj.sdo.Sequence");
        genModel.setFeatureMapWrapperInternalInterface("org.apache.tuscany.sdo.util.BasicSequence");
        genModel.setFeatureMapWrapperClass("org.apache.tuscany.sdo.util.BasicSequence");
        genModel.setSuppressEMFTypes(true);
        genModel.setSuppressEMFMetaData(true);
        genModel.setSuppressEMFModelTags(true);
        genModel.setCanGenerate(true);
        // FIXME workaround java.lang.NoClassDefFoundError:
        // org/eclipse/jdt/core/jdom/IDOMNode with 02162006 build
        genModel.setFacadeHelperClass("Hack");
        genModel.setForceOverwrite(true);

        GenPackage genPackage = (GenPackage)genModel.getGenPackages().get(0);

        if (basePackage != null) {
            genPackage.setBasePackage(basePackage);
        }
        if (prefix != null) {
            genPackage.setPrefix(prefix);
        }

        return genModel;
    }

    public static String extractBasePackageName(EPackage ePackage, String javaPackage) {
        String qualifiedName = javaPackage != null ? javaPackage : ePackage.getName();
        String name = /* CodeGenUtil. */shortName(qualifiedName);
        String baseName = qualifiedName.substring(0, qualifiedName.length() - name.length());
        if (javaPackage != null || !name.equals(qualifiedName)) {
            ePackage.setName(name);
        }
        return baseName != null ? /* CodeGenUtil. */safeQualifiedName(baseName) : null;
    }

    public static String shortName(String qualifiedName) {
        int index = qualifiedName.lastIndexOf(".");
        return index != -1 ? qualifiedName.substring(index + 1) : qualifiedName;
    }

    public static String safeQualifiedName(String qualifiedName) {
        StringBuffer safeQualifiedName = new StringBuffer();
        for (StringTokenizer stringTokenizer = new StringTokenizer(qualifiedName, ".");
            stringTokenizer.hasMoreTokens();) {
            String name = stringTokenizer.nextToken();
            safeQualifiedName.append(CodeGenUtil.safeName(name));
            if (stringTokenizer.hasMoreTokens()) {
                safeQualifiedName.append('.');
            }
        }
        return safeQualifiedName.toString();
    }
    /*
     * Converts myClassName to MyClassName
     */
    public static String normalizeClassName(String className) {

        StringBuffer normalizedClassName = new StringBuffer();
        
        String beginPart = className.substring(0,1);
        String endPart = className.substring(1);
        
        normalizedClassName.append(beginPart.toUpperCase());
        normalizedClassName.append(endPart);

        return normalizedClassName.toString();
    }

    protected static void printDiagnostic(Diagnostic diagnostic, String indent) {
        System.out.print(indent);
        System.out.println(diagnostic.getMessage());
        for (Iterator i = diagnostic.getChildren().iterator(); i.hasNext();) {
            printDiagnostic((Diagnostic)i.next(), indent + "  ");
        }
    }

    protected static void printUsage() {
        System.out.println("Usage arguments:");
        System.out.println("  [ -targetDirectory <target-root-directory> ]");
        System.out.println("  [ -javaPackage <java-package-name> ]");
        System.out.println("  [ -dynamicSDO | -generateSDO [ -sdoGenArgs <command-file-name> ]]");
        System.out.println("  [ -verbose ]");
        System.out.println("  <wsdl-file>");
        System.out.println("");
        System.out.println("Where <command-file-name> is a text file containing valid XSD2Java command\narguments (w/o the wsdl/xsd file name)");
        System.out.println("For example:");
        System.out.println("");
        System.out.println("    WSDL2JavaGenerator -targetDirectory myDir somedir/somefile.wsdl");
    }


}
