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
package org.apache.tuscany.core.services.extension;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.deployer.Deployer;

/**
 * @version $Rev$ $Date$
 */
public class AbstractExtensionDeployer {
    protected Deployer deployer;
    protected Component parent;

    @Reference
    public void setDeployer(Deployer deployer) {
        this.deployer = deployer;
    }

    protected void deployExtension(File file) {
        // extension name is file name less any extension
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            name = name.substring(0, dot);
        }
        URL url;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            // toURI should have encoded the URL
            throw new AssertionError();
        }

        deployExtension(name, url);
    }

    protected void deployExtension(String name, URL url) {
        throw new UnsupportedOperationException("");
//        // FIXME for now, assume this class's ClassLoader is the Tuscany system classloader
//        // FIXME we should really use the one associated with the parent composite
//        CompositeClassLoader extensionCL = new CompositeClassLoader(getClass().getClassLoader());
//
//        // see if the URL points to a composite JAR by looking for a default SCDL file inside it
//        URL scdlLocation;
//        try {
//            scdlLocation = new URL("jar:" + url.toExternalForm() + "!/META-INF/sca/default.scdl");
//        } catch (MalformedURLException e) {
//            // the form of the jar: URL should be correct given url.toExternalForm() worked
//            throw new AssertionError();
//        }
//        try {
//            scdlLocation.openStream().close();
//            // we connected to the SCDL so let's add the JAR file to the classloader
//            extensionCL.addURL(url);
//        } catch (IOException e) {
//            // assume that the URL we were given is not a JAR file so just use the supplied resource
//            scdlLocation = url;
//        }
//
//        // create a ComponentDefinition to represent the component we are going to deploy
//        SystemCompositeImplementation implementation = new SystemCompositeImplementation();
//        implementation.setScdlLocation(scdlLocation);
//        implementation.setClassLoader(extensionCL);
//        URI uri = parent.getUri().resolve(name);
//        ComponentDefinition<SystemCompositeImplementation> definition =
//            new ComponentDefinition<SystemCompositeImplementation>(uri, implementation);
//
//        // FIXME: [rfeng] Should we reset the thread context class loader here?
//        // From the debugger with tomcat, the current TCCL is the RealmClassLoader
//        // ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
//        try {
//            // Thread.currentThread().setContextClassLoader(extensionCL);
//            Component component;
//            try {
//                component = deployer.deploy(parent, definition);
//                component.start();
//            } catch (BuilderException e) {
//                // FIXME JFM handle the exception
//                e.printStackTrace();
//            } catch (ComponentException e) {
//                // FIXME handle the exception
//                e.printStackTrace();
//            } catch (ResolutionException e) {
//                // FIXME handle the exception
//                e.printStackTrace();
//            }
//        } catch (LoaderException e) {
//            // FIXME handle the exception
//            e.printStackTrace();
//        }
    }
}
