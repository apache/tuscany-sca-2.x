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
package org.apache.tuscany.model.assembly;


/**
 * Represents a wire.
 */
public interface Wire extends  Extensible {

    /**
     * Returns the source URI
     * @return
     */
    ServiceURI getSource();
    
    /**
     * Sets the source URI
     * @param uri
     */
    void setSource(ServiceURI uri);
    
    /**
     * Returns the target URI
     * @return
     */
    ServiceURI getTarget();
    
    /**
     * Sets the target URI
     * @param uri
     */
    void setTarget(ServiceURI uri);
    
}
