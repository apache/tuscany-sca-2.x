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

package org.apache.tuscany.sca.binding.rest.js.dojo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.binding.rest.RESTBinding;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;
import org.apache.tuscany.sca.web.javascript.JavascriptProxyFactory;

public class RESTBindingJavascriptProxyFactory implements JavascriptProxyFactory {
    private static final QName NAME = RESTBinding.TYPE;

    public Class<?> getModelType() {
        return RESTBinding.class;
    }

    public QName getQName() {
        return NAME;
    }

    public String getJavascriptProxyFile() {
        return null;
    }

    public InputStream getJavascriptProxyFileAsStream() throws IOException {
        return null;
    }

    public String createJavascriptHeader(ComponentReference componentReference) throws IOException {
        return "dojo.require('tuscany.RestService');";
    }

    public String createJavascriptReference(ComponentReference componentReference) throws IOException {
        EndpointReference epr = componentReference.getEndpointReferences().get(0);
        Endpoint targetEndpoint = epr.getTargetEndpoint();
        if (targetEndpoint.isUnresolved()) {
            //force resolution and targetEndpoint binding calculations
            //by calling the getInvocationChain
            ((RuntimeEndpointReference) epr).getInvocationChains();
            targetEndpoint = epr.getTargetEndpoint();
        }

        Binding binding = targetEndpoint.getBinding();

        URI targetURI = URI.create(binding.getURI());
        String targetPath = targetURI.getPath();

        return "tuscany.RestService(\"" + targetPath + "\")";
    }

}
