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
package org.apache.tuscany.spi.util;

import java.net.URI;

/**
 * Utility methods for handling URIs
 *
 * @version $Rev$ $Date$
 */
public final class UriHelper {

    private UriHelper() {
    }

    /**
     * Returns the base name for a component URI, e.g. 'Bar' for 'sca://foo/Bar'
     *
     * @param uri the URI to parse
     * @return the base name
     */
    public static String getBaseName(URI uri) {
        String s = uri.toString();
        int pos = s.lastIndexOf('/');
        if (pos > -1) {
            return s.substring(pos + 1);
        } else {
            return s;
        }
    }

    public static URI getDefragmentedName(URI uri) {
        if (uri.getFragment() == null) {
            return uri;
        }
        String s = uri.toString();
        int pos = s.lastIndexOf('#');
        return URI.create(s.substring(0, pos));
    }
}
