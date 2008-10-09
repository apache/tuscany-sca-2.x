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

package org.apache.tuscany.sca.node;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * ContributionLocationHelper
 *
 * @version $Rev: $ $Date: $
 */
public class ContributionLocationHelper {
    
    /**
     * Returns the location of the SCA contribution containing the given class.
     * 
     * @param anchorClass
     * @return
     */
    public static String getContributionLocation(final Class<?> anchorClass) {
        URL url = AccessController.doPrivileged(new PrivilegedAction<URL>() {
            public URL run() {
                return anchorClass.getProtectionDomain().getCodeSource().getLocation();
            }
        });
        String uri = url.toString();
        return uri;
    }

}
