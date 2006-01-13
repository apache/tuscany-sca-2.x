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
package org.apache.tuscany.model.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.URIConverterImpl;

import org.apache.tuscany.common.io.util.FixedURLInputStream;


/**
 * The EMF URI implementation has a bug dealing with some special URIs used
 * by WebSphere classloaders. For example, if the URI is
 * "wsjar:file:/C:/.../lib/sca-ejb.jar!/META-INF/sca.policy",
 * URI.fileExtension() returns null.
 * <p/>
 * This URIConverter replaces "wsjar" with "jar".
 *
 */
public class FixedURIConverterImpl extends URIConverterImpl {

    /**
     * @see org.eclipse.emf.ecore.resource.URIConverter#createInputStream(org.eclipse.emf.common.util.URI)
     */
    public InputStream createInputStream(URI uri) throws IOException {
        return new FixedURLInputStream(new URL(uri.toString()));
    }

    /**
     * @see org.eclipse.emf.ecore.resource.URIConverter#createOutputStream(org.eclipse.emf.common.util.URI)
     */
    public OutputStream createOutputStream(URI uri) throws IOException {
        URL url = new URL(uri.toString());
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoOutput(true);
        return urlConnection.getOutputStream();
    }

    /**
     * @see org.eclipse.emf.ecore.resource.URIConverter#normalize(org.eclipse.emf.common.util.URI)
     */
    public URI normalize(URI uri) {
        return super.normalize(fixURI(uri));
    }

    /**
     * Fix a URI.
     *
     * @param uri
     * @return
     */
    public URI fixURI(URI uri) {
        String scheme = uri.scheme();
        if ((scheme != null) && scheme.equals("wsjar")) {
            String uriStr = uri.toString();
            uriStr = "jar" + uriStr.substring("wsjar".length());
            uri = URI.createURI(uriStr);
        }
        return uri;
    }

}