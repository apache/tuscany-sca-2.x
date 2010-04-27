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

package org.apache.tuscany.sca.binding.rest.operationselector.jaxrs;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.xml.Constants;

/**
 * JAX_RS Operation Selector model
 * 
 * @version $Rev$ $Date$
 */
public interface JAXRSOperationSelector extends OperationSelector {
    /**
     *  QName representing the REST JAX_RS Operation Selector extension
     */
    public static final QName REST_OPERATION_SELECTOR_JAXRS_QNAME = new QName(Constants.SCA11_TUSCANY_NS, "operationSelector.jaxrs");

    /**
     * Return the QName identifying the operation selector 
     * @return the QName identifying the operation selector
     */
    QName getSchemaName();
}
