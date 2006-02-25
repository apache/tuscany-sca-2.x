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
 * A connection between a requestor (source) and a provider (target).
 */
public interface Wire extends  Extensible {
    /**
     * Returns the URI for the source of the request.
     * @return the URI for the source of the request
     */
    ServiceURI getSource();
    
    /**
     * Sets the URI for the source of the request.
     * @param uri the URI for the source of the request
     */
    void setSource(ServiceURI uri);
    
    /**
     * Returns the URI for the target of the request.
     * @return the URI for the target of the request
     */
    ServiceURI getTarget();
    
    /**
     * Sets the URI for the target of the request.
     * @param uri the URI for the target of the request
     */
    void setTarget(ServiceURI uri);
    
}
