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
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.axis2.wsdl.WSDL2Java;
import org.apache.tuscany.sdo.helper.XSDHelperImpl;
import org.apache.tuscany.sdo.util.DataObjectUtil;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;

import commonj.sdo.helper.XSDHelper;


public class WSDL2JavaGenerator
{
  
  /**
   * Generate Java interfaces from WSDL
   * 
   *   Usage arguments:
   *   
   *     [ -targetDirectory <target-root-directory> ]
   *     [ -javaPackage <java-package-name> ]
   *     <wsdl-file>
   *
   *   For example:
   *   
   *     generate somedir/somefile.wsdl
   *     
   *   Basic options:
   *   
   *     -targetDirectory
   *         Generates the Java source code in the specified directory. By default, the code is generated
   *         in the same directory as the input wsdl file.
   *     -javaPackage
   *         Overrides the Java package for the generated classes. By default the package name is derived
   *         from the targetNamespace of the WSDL definition being generated. For example, if the targetNamespace is
   *         "http://www.example.com/simple", the default package will be "com.example.simple".
   */
  public static void main(String args[])
  {
      try {
          WSDL2Java.main(args);
      } catch (Exception e) {
          throw new IllegalArgumentException(e);
      }
      if (true)
          return;
      
    if (args.length == 0)
    {
      printUsage();
      return;
    }
    
    
    String targetDirectory = null;
    String javaPackage = null;
    
    int genOptions = 0;

    int index = 0;
    for (; index < args.length && args[index].startsWith("-"); ++index)
    {
      if (args[index].equalsIgnoreCase("-targetDirectory"))
      {
        targetDirectory = args[++index];
      }
      else if (args[index].equalsIgnoreCase("-javaPackage"))
      {
        javaPackage = args[++index];
      }
      //else if (...)
      else
      {
        printUsage();
        return;
      }
    }

    String wsdlFileName = args[index];
    
    generateFromWSDL(wsdlFileName, targetDirectory, javaPackage, 0);
  }

  public static void generateFromWSDL(String xsdFileName, String targetDirectory, String javaPackage, int genOptions)
  {
    DataObjectUtil.initRuntime();
    EPackage.Registry packageRegistry = new EPackageRegistryImpl(EPackage.Registry.INSTANCE)
    {
      public EPackage firstPackage = null;
            
    };
    ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(packageRegistry);
    XSDHelper xsdHelper = new XSDHelperImpl(extendedMetaData);

    try
    {
      File inputFile = new File(xsdFileName).getAbsoluteFile();
      InputStream inputStream = new FileInputStream(inputFile);
      xsdHelper.define(inputStream, inputFile.toURI().toString());

      if (targetDirectory == null)
      {
        targetDirectory = new File(xsdFileName).getCanonicalFile().getParent();
      }
      else
      {
        targetDirectory = new File(targetDirectory).getCanonicalPath();
      }

      if (!packageRegistry.values().isEmpty())
      {
        String packageURI = getSchemaNamespace(xsdFileName);
        ResourceSet resourceSet = DataObjectUtil.createResourceSet();
        
        List usedGenPackages = new ArrayList();
        GenModel genModel = null;
        
        for (Iterator iter = packageRegistry.values().iterator(); iter.hasNext();)
        {
          EPackage currentEPackage = (EPackage)iter.next();
          String currentBasePackage = extractBasePackageName(currentEPackage, javaPackage);
          String currentPrefix = CodeGenUtil.capName(currentEPackage.getName());
          
          GenPackage currentGenPackage = createGenPackage(currentEPackage, currentBasePackage, currentPrefix, genOptions, resourceSet);
          if (currentEPackage.getNsURI().equals(packageURI))
          {
            genModel = currentGenPackage.getGenModel();
          }
          else
          {
            usedGenPackages.add(currentGenPackage);
          }
        }
        
        genModel.getUsedGenPackages().addAll(usedGenPackages);
        generateFromGenModel(genModel, targetDirectory);
      }

      /*
      for (Iterator iter = packageRegistry.values().iterator(); iter.hasNext();)
      {
        EPackage ePackage = (EPackage)iter.next();
        String basePackage = extractBasePackageName(ePackage, javaPackage);
        if (prefix == null)
        {
          prefix = CodeGenUtil.capName(ePackage.getName());
        }
        generateFromEPackage(ePackage, targetDirectory, basePackage, prefix, genOptions);
      }
      */
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static String getSchemaNamespace(String xsdFileName)
  {
    File inputFile = new File(xsdFileName).getAbsoluteFile();
    ResourceSet resourceSet = DataObjectUtil.createResourceSet();
    Resource model = resourceSet.createResource(URI.createURI(inputFile.toURI().toString()));
    try {
      InputStream inputStream = new FileInputStream(inputFile);
      ((XSDResourceImpl)model).load(inputStream, null);
    }
    catch (Exception e) {}
    XSDSchema schema = (XSDSchema)model.getContents().get(0);
    return schema.getTargetNamespace();

  }

  public static GenPackage createGenPackage(EPackage ePackage, String basePackage, String prefix, int genOptions, ResourceSet resourceSet)
  {
    GenModel genModel = ecore2GenModel(ePackage, basePackage, prefix, genOptions);

    URI ecoreURI = URI.createURI("file:///" + ePackage.getName() + ".ecore");
    URI genModelURI = ecoreURI.trimFileExtension().appendFileExtension("genmodel");

    Resource ecoreResource = resourceSet.createResource(ecoreURI);
    ecoreResource.getContents().add(ePackage);

    Resource genModelResource = resourceSet.createResource(genModelURI);
    genModelResource.getContents().add(genModel);

    return (GenPackage)genModel.getGenPackages().get(0);
  }

  public static void generateFromEPackage(EPackage ePackage, String targetDirectory, String basePackage, String prefix, int genOptions)
  {
    GenModel genModel = ecore2GenModel(ePackage, basePackage, prefix, genOptions);

    ResourceSet resourceSet = DataObjectUtil.createResourceSet();
    URI ecoreURI = URI.createURI("file:///temp.ecore");
    URI genModelURI = ecoreURI.trimFileExtension().appendFileExtension("genmodel");

    Resource ecoreResource = resourceSet.createResource(ecoreURI);
    ecoreResource.getContents().add(ePackage);

    Resource genModelResource = resourceSet.createResource(genModelURI);
    genModelResource.getContents().add(genModel);

    generateFromGenModel(genModel, targetDirectory);
  }

  public static void generateFromGenModel(GenModel genModel, String targetDirectory)
  {
    Resource resource = genModel.eResource();

    if (targetDirectory != null)
    {
      resource.getResourceSet().getURIConverter().getURIMap().put(
        URI.createURI("platform:/resource/TargetProject/"),
        URI.createFileURI(targetDirectory + "/"));
      genModel.setModelDirectory("/TargetProject");
    }

    genModel.gen(new BasicMonitor.Printing(System.out));

    for (Iterator j = resource.getContents().iterator(); j.hasNext();)
    {
      EObject eObject = (EObject)j.next();
      Diagnostic diagnostic = Diagnostician.INSTANCE.validate(eObject);
      if (diagnostic.getSeverity() != Diagnostic.OK)
      {
        printDiagnostic(diagnostic, "");
      }
    }
  }

  public static GenModel ecore2GenModel(EPackage ePackage, String basePackage, String prefix, int genOptions)
  {
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
    //FIXME workaround java.lang.NoClassDefFoundError: org/eclipse/jdt/core/jdom/IDOMNode with 02162006 build
    genModel.setFacadeHelperClass("Hack");
    genModel.setForceOverwrite(true);
    
    GenPackage genPackage = (GenPackage)genModel.getGenPackages().get(0);

    if (basePackage != null)
    {
      genPackage.setBasePackage(basePackage);
    }
    if (prefix != null) 
    {
      genPackage.setPrefix(prefix);
    }

    return genModel;
  }

  public static String extractBasePackageName(EPackage ePackage, String javaPackage)
  {
    String qualifiedName = javaPackage != null ? javaPackage : ePackage.getName();
    String name = /*CodeGenUtil.*/shortName(qualifiedName);
    String baseName = qualifiedName.substring(0, qualifiedName.length() - name.length());
    if (javaPackage != null || !name.equals(qualifiedName))
    {
      ePackage.setName(name);
    }
    return baseName != null ? /*CodeGenUtil.*/safeQualifiedName(baseName) : null;
  }

  public static String shortName(String qualifiedName)
  {
    int index = qualifiedName.lastIndexOf(".");
    return index != -1 ? qualifiedName.substring(index + 1) : qualifiedName;
  }

  public static String safeQualifiedName(String qualifiedName)
  {
    StringBuffer safeQualifiedName = new StringBuffer();
    for (StringTokenizer stringTokenizer = new StringTokenizer(qualifiedName, "."); stringTokenizer.hasMoreTokens();)
    {
      String name = stringTokenizer.nextToken();
      safeQualifiedName.append(CodeGenUtil.safeName(name));
      if (stringTokenizer.hasMoreTokens())
      {
        safeQualifiedName.append('.');
      }
    }
    return safeQualifiedName.toString();
  }

  protected static void printDiagnostic(Diagnostic diagnostic, String indent)
  {
    System.out.print(indent);
    System.out.println(diagnostic.getMessage());
    for (Iterator i = diagnostic.getChildren().iterator(); i.hasNext();)
    {
      printDiagnostic((Diagnostic)i.next(), indent + "  ");
    }
  }

  protected static void printUsage()
  {
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
