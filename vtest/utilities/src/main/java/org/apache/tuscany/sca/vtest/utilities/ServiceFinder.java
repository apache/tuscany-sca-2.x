/*
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

package org.apache.tuscany.sca.vtest.utilities;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * 
 * 
 */
public class ServiceFinder {

    private static SCADomain domain;

    protected ServiceFinder() {
        super();
    }

    public static void init(String compositeFileName) {
        if (domain != null)
            System.out.println("VTEST WARNING: domain already exists is is being overwritten!");
        domain = SCADomain.newInstance(compositeFileName);
    } 
    
    
    public static <B> B getService(Class<B> businessInterface, String serviceName) {
        return domain.getService(businessInterface, serviceName);
    }

    public static void cleanup() {
        domain.close();
        domain = null;
    }

}
