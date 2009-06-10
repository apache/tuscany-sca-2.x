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

package org.apache.tuscany.sca.node.osgi.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;

/**
 * NodeUtil
 *
 * @version $Rev: $ $Date: $
 */
public class NodeUtil {
    private NodeUtil() {
    }

    static Contribution contribution(ContributionFactory contributionFactory, org.apache.tuscany.sca.node.Contribution c) {
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI(c.getURI());
        contribution.setLocation(c.getLocation());
        contribution.setUnresolved(true);
        return contribution;
    }

    /**
     * Open a URL connection without cache
     * @param url
     * @return
     * @throws IOException
     */
    static InputStream openStream(URL url) throws IOException {
        InputStream is = null;
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        is = connection.getInputStream();
        return is;
    }

    /**
     * Escape the space in URL string
     * @param uri
     * @return
     */
    static URI createURI(String uri) {
        if (uri.indexOf('%') != -1) {
            // Avoid double-escaping
            return URI.create(uri);
        }
        int index = uri.indexOf(':');
        String scheme = null;
        String ssp = uri;
        if (index != -1) {
            scheme = uri.substring(0, index);
            ssp = uri.substring(index + 1);
        }
        try {
            return new URI(scheme, ssp, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
