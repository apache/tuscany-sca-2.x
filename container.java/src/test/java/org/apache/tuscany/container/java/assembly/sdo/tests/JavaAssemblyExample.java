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
package org.apache.tuscany.container.java.assembly.sdo.tests;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.osoa.sca.model.JavaImplementation;

import org.apache.tuscany.container.java.assembly.sdo.DocumentRoot;
import org.apache.tuscany.container.java.assembly.sdo.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.sdo.JavaAssemblyPackage;

/**
 * <!-- begin-user-doc -->
 * A sample utility for the '<em><b>assembly</b></em>' package.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class JavaAssemblyExample {
    /**
     * <!-- begin-user-doc -->
     * Load all the argument file paths or URIs as instances of the model.
     * <!-- end-user-doc -->
     *
     * @param args the file paths or URIs.
     * @generated
     */
    public static void main(String[] args) {
        // Create a resource set to hold the resources.
        //
        ResourceSet resourceSet = new ResourceSetImpl();

        // Register the appropriate resource factory to handle all file extentions.
        //
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put
                (Resource.Factory.Registry.DEFAULT_EXTENSION,
                        new XMIResourceFactoryImpl());

        // Register the package to ensure it is available during loading.
        //
        resourceSet.getPackageRegistry().put
                (JavaAssemblyPackage.eNS_URI,
                        JavaAssemblyPackage.eINSTANCE);

        // If there are no arguments, emit an appropriate usage message.
        //
        if (args.length == 0) {
            System.out.println("Enter a list of file paths or URIs that have content like this:");
            try {
                Resource resource = resourceSet.createResource(URI.createURI("http:///My.javaassembly"));
                DocumentRoot documentRoot = JavaAssemblyFactory.eINSTANCE.createDocumentRoot();
                JavaImplementation root = JavaAssemblyFactory.eINSTANCE.createJavaImplementation();
                documentRoot.setImplementationJava(root);
                resource.getContents().add(documentRoot);
                resource.save(System.out, null);
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            // Iterate over all the arguments.
            //
            for (int i = 0; i < args.length; ++i) {
                // Construct the URI for the instance file.
                // The argument is treated as a file path only if it denotes an existing file.
                // Otherwise, it's directly treated as a URL.
                //
                File file = new File(args[0]);
                URI uri = file.isFile() ? URI.createFileURI(file.getAbsolutePath()) : URI.createURI(args[0]);

                try {
                    // Demand load resource for this file.
                    //
                    Resource resource = resourceSet.getResource(uri, true);
                    System.out.println("Loaded " + uri);

                    // Validate the contents of the loaded resource.
                    //
                    for (Iterator j = resource.getContents().iterator(); j.hasNext();) {
                        EObject eObject = (EObject) j.next();
                        Diagnostic diagnostic = Diagnostician.INSTANCE.validate(eObject);
                        if (diagnostic.getSeverity() != Diagnostic.OK) {
                            printDiagnostic(diagnostic, "");
                        }
                    }
                }
                catch (RuntimeException exception) {
                    System.out.println("Problem loading " + uri);
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     * <!-- begin-user-doc -->
     * Prints diagnostics with indentation.
     * <!-- end-user-doc -->
     *
     * @param diagnostic the diagnostic to print.
     * @param indent     the indentation for printing.
     * @generated
     */
    protected static void printDiagnostic(Diagnostic diagnostic, String indent) {
        System.out.print(indent);
        System.out.println(diagnostic.getMessage());
        for (Iterator i = diagnostic.getChildren().iterator(); i.hasNext();) {
            printDiagnostic((Diagnostic) i.next(), indent + "  ");
        }
    }

} //JavaAssemblyExample
