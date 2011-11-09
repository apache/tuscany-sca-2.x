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

package org.apache.tuscany.sca.binding.jms.provider.xml;

import java.util.Properties;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.runtime.RuntimeProperties;

public class XMLHelperFactory {
    
    public static XMLHelper<?> createXMLHelper(ExtensionPointRegistry epr) {
        
        XMLHelper<?> xmlHelper = epr.getExtensionPoint(UtilityExtensionPoint.class).getUtility(XMLHelper.class);
        if (xmlHelper != null) {
            return xmlHelper;
        }
        if (useAXIOM(epr)) {
            return new AXIOMXMLHelper(epr);
        } else {
            return new DOMXMLHelper(epr);
        }
    }

    private static boolean useAXIOM(ExtensionPointRegistry epr) {
        Properties runtimeProps = epr.getExtensionPoint(UtilityExtensionPoint.class).getUtility(RuntimeProperties.class).getProperties();
        return Boolean.parseBoolean(runtimeProps.getProperty(RuntimeProperties.USE_AXIOM));
    }

}
