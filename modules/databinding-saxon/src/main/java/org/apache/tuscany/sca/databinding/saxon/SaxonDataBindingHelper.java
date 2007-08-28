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
package org.apache.tuscany.sca.databinding.saxon;

import net.sf.saxon.Configuration;

/**
 * Provides helper functionality for saxon data bindings
 * @version $Rev$ $Date$
 */
public class SaxonDataBindingHelper {
    /**
     * This variable is meaningfull only in the context of XQoery expression
     * execution. It is used by the DataObject2NodeInfoTransformer and
     * Node2NodeInfoTransformer to create the correct NodeInfo objects
     * in the Output2Output transformations. 
     * For Input2Input transformations it is meaningless:
     *    - if it is null - it is ignored by the transformers as they create new
     *      configuration objects
     *    - if it is not null - it is reused
     * However the XQueryInvoker transforms all NodeInfo-s to NodeInfo-s with
     * its current configuration, so there is no effect for Input2Input transformations
     */
    public static Configuration CURR_EXECUTING_CONFIG = null;
}
