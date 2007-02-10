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
package org.apache.tuscany.launcher;

import java.net.URI;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.text.MessageFormat;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;

import org.apache.tuscany.host.runtime.TuscanyRuntime;
import org.apache.tuscany.runtime.standalone.DirectoryHelper;
import org.apache.tuscany.runtime.standalone.StandaloneRuntimeInfo;

/**
 * Main class for launcher runtime environment.
 * <code>
 * usage: java [jvm-options] -jar launcher.jar <componentURI>
 * </code>
 * where the componentURI identifies a component in the assembly that should be called.
 *
 * @version $Rev$ $Date$
 */
public class Main {
    /**
     * Main method.
     *
     * @param args the command line args
     * @throws Throwable if there are problems launching the runtime or application
     */
    public static void main(String[] args) throws Throwable {
        if (args.length == 0) {
            usage();
            throw new AssertionError();
        }

        StandaloneRuntimeInfo runtimeInfo = DirectoryHelper.createRuntimeInfo("launcher", Main.class);
        TuscanyRuntime runtime = DirectoryHelper.createRuntime(runtimeInfo);
        runtime.initialize();
        try {
            URI applicationURI = new URI(args[0]);
            String serviceName = applicationURI.getFragment();
            ComponentContext context = runtime.getComponentContext(applicationURI);
            if (context == null) {
                noComponent(applicationURI);
                throw new AssertionError();
            }
            ServiceReference<Callable> service;
            if (serviceName == null) {
                service = context.createSelfReference(Callable.class);
            } else {
                service = context.createSelfReference(Callable.class, serviceName);
            }
            Callable callable = service.getService();
            callable.call();
        } finally {
            runtime.destroy();
        }

    }

    private static void usage() {
        System.err.println(getMessage("org.apache.tuscany.launcher.Usage"));
        System.exit(1);
    }

    private static void noComponent(URI applicationURI) {
        System.err.println(getMessage("org.apache.tuscany.launcher.NoComponent", applicationURI));
        System.exit(2);
    }

    private static String getMessage(String id, Object... params) {
        ResourceBundle bundle = ResourceBundle.getBundle(Main.class.getName());
        String message = bundle.getString(id);
        return MessageFormat.format(message, params);
    }
}
