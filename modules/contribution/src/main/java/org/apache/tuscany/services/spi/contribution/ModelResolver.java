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

package org.apache.tuscany.services.spi.contribution;

import java.util.Map;

import org.apache.tuscany.services.contribution.model.Contribution;

/**
 * This interface is implemented by models that can resolve model elements.
 * 
 * @version $Rev: 522653 $ $Date: 2007-03-26 15:30:21 -0700 (Mon, 26 Mar 2007) $
 */
public interface ModelResolver {
    
    /**
     * The key that can be used to retrieve the model resolver. For example for a WSDL model this
     * can be the namespace URI of the WSDL document.
     * @return the key used to retrieve the model resolver
     */
    Object getKey();
    
    /**
     * Resolves a model element within a model.
     * @param contribution the contribution containing the model
     * @param elementClass the type of element being resolved
     * @param key the key representing that element (for example the name of a WSDL portType)
     * @param attributes additional attributes that can be used to resolve the element
     * @return the resolved model element
     */
    <M> M resolve(Contribution contribution, Class<M> elementClass, Object key, Map<String, Object> attributes);
    
}
