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
package org.apache.tuscany.binding.rmi.assembly;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.model.assembly.Binding;

import commonj.sdo.helper.TypeHelper;

/**
 * Represents a Web service binding.
 */
public interface RMIBinding extends Binding {

    String getRMIHostName();
    
    void setRMIHostName(String hostName);
    
    String getRMIPort();
    
    void setRMIPort(String port);
    
    String getRMIServerName();
    
    void setRMIServerName(String serverName);
    
    TypeHelper getTypeHelper();

    void setTypeHelper(TypeHelper typeHelper);

    ResourceLoader getResourceLoader();

    void setResourceLoader(ResourceLoader resourceLoader);
}
