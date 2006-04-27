/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.tools.wsdl2java.generate;

import java.io.File;
import java.io.FileInputStream;
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

import org.apache.tuscany.sdo.helper.XSDHelperImpl;
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

import commonj.sdo.helper.XSDHelper;

public class WSDL2JavaGenerator {

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

        String targetDirectory = null;
        String wsdlJavaPackage = null;
        String xsdJavaPackage = null;

        int index = 0;
        for (; index < args.length && args[index].startsWith("-"); ++index) {
            if (args[index].equalsIgnoreCase("-targetDirectory")) {
                targetDirectory = args[++index];
            } else if (args[index].equalsIgnoreCase("-javaPackage")) {
                wsdlJavaPackage = args[++index];
            }
            // else if (...)
            else {
                printUsage();
                return;
            }
        }

        String wsdlFileName = args[index];
        if (wsdlFileName == null || targetDirectory == null) {
            printUsage();
            return;
        }

        generateFromWSDL(wsdlFileName, targetDirectory, wsdlJavaPackage, xsdJavaPackage, 0);

    }

    @SuppressWarnings("unchecked")
    public static void generateFromWSDL(String wsdlFileName, String targetDirectory,
                                        String wsdlJavaPackage,
                                        String xsdJavaPackage, int genOptions) {
        DataObjectUtil.initRuntime();
        EPackage.Registry packageRegistry = new EPackageRegistryImpl(EPackage.Registry.INSTANCE);
        ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(packageRegistry);
        XSDHelper xsdHelper = new XSDHelperImpl(extendedMetaData);

        try {
            File inputFile = new File(wsdlFileName).getAbsoluteFile();
            InputStream inputStream = new FileInputStream(inputFile);
            xsdHelper.define(inputStream, inputFile.toURI().toString());

            if (targetDirectory == null) {
                targetDirectory = new File(wsdlFileName).getCanonicalFile().getParent();
            } else {
                targetDirectory = new File(targetDirectory).getCanonicalPath();
            }

            Map<QName, Object> typeMapping = new HashMap<QName, Object>();
            if (!packageRegistry.values().isEmpty()) {
                ResourceSet resourceSet = DataObjectUtil.createResourceSet();

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

                for (GenPackage currentGenPackage : genPackages) {
                    EPackage currentEPackage = currentGenPackage.getEcorePackage();
                    for (GenClass genClass : (List<GenClass>)currentGenPackage.getGenClasses()) {
                        QName qname = new QName(extendedMetaData.getNamespace(currentEPackage),
                                                extendedMetaData.getName(genClass.getEcoreClass()));
                        String interfaceName = currentGenPackage.getInterfacePackageName() + '.'
                                               + genClass.getInterfaceName();
                        typeMapping.put(qname, interfaceName);
                    }

                    EClass documentRoot = extendedMetaData.getDocumentRoot(currentEPackage);
                    if (documentRoot != null) {
                        for (EStructuralFeature element : (List<EStructuralFeature>)extendedMetaData
                            .getElements(documentRoot)) {
                            EClassifier elementType = element.getEType();
                            if (extendedMetaData.isAnonymous(elementType)) {
                                EClass eClass = (EClass)elementType;
                                QName qname = new QName(extendedMetaData.getNamespace(currentEPackage),
                                        extendedMetaData.getName(element));
                                List<String> interfaceNames=new ArrayList<String>();
                                for (EStructuralFeature feature :
                                    (List<EStructuralFeature>)eClass.getEStructuralFeatures()) {
                                    elementType = feature.getEType();
                                    if (elementType instanceof EClass) {
                                        GenClass genClass = genClasses.get(elementType);
                                        String interfaceName = genClass.getGenPackage().getInterfacePackageName()
                                                               + '.' + genClass.getInterfaceName();
                                        interfaceNames.add(interfaceName);
                                    } else if (elementType instanceof EClassifier) {
                                        String interfaceName = elementType.getInstanceClass().getName();
                                        interfaceNames.add(interfaceName);
                                    }
                                    typeMapping.put(qname, interfaceNames);
                                }
                            } else {
                                if (elementType instanceof EClass) {
                                    GenClass genClass = genClasses.get(elementType);
                                    QName qname = new QName(extendedMetaData.getNamespace(currentEPackage),
                                                            extendedMetaData.getName(element));
                                    String interfaceName = genClass.getGenPackage().getInterfacePackageName()
                                                           + '.' + genClass.getInterfaceName();
                                    typeMapping.put(qname, interfaceName);
                                } else if (elementType instanceof EClassifier) {
                                    QName qname = new QName(extendedMetaData.getNamespace(currentEPackage),
                                                            extendedMetaData.getName(element));
                                    String interfaceName = elementType.getInstanceClass().getName();
                                    typeMapping.put(qname, interfaceName);
                                }
                            }
                        }
                    }
                }
            }

            try {
                JavaInterfaceGenerator codeGenerator = new JavaInterfaceGenerator(wsdlFileName,
                                                                                  targetDirectory,
                                                                                  wsdlJavaPackage,
                                                                                  typeMapping);
                codeGenerator.generate();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }

        } catch (IOException e) {
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
        System.out.println("  <wsdl-file>");
        System.out.println("");
        System.out.println("For example:");
        System.out.println("");
        System.out.println("  generate somedir/somefile.wsdl");
    }

}
