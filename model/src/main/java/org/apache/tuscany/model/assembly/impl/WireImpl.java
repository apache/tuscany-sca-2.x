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
package org.apache.tuscany.model.assembly.impl;

import org.apache.tuscany.model.assembly.ServiceURI;
import org.apache.tuscany.model.assembly.Wire;

/**
 * An implementation of Wire.
 */
public class WireImpl extends ExtensibleImpl implements Wire {
    
    private ServiceURI source;
    private ServiceURI target;

    /**
     * Constructor
     */
    protected WireImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.Wire#getSource()
     */
    public ServiceURI getSource() {
        return source;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.Wire#setSource(org.apache.tuscany.model.assembly.ServiceURI)
     */
    public void setSource(ServiceURI uri) {
        checkNotFrozen();
        source=uri;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.Wire#getTarget()
     */
    public ServiceURI getTarget() {
        return target;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.Wire#setTarget(org.apache.tuscany.model.assembly.ServiceURI)
     */
    public void setTarget(ServiceURI uri) {
        checkNotFrozen();
        target=uri;
    }
    
}
