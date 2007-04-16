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
package org.apache.tuscany.databinding;

/**
 * A transformer provides the data transformation from source type to target type. The cost of the transformation is
 * modeled as weight.
 */
public interface Transformer {
    /**
     * Get the source type that this transformer transforms data from. The type is used as the key when the transformer
     * is registered with TransformerRegistry.
     * 
     * @return A key indentifying the source type
     */
    String getSourceDataBinding();

    /**
     * Get the target type that this transformer transforms data into. The type is used as the key when the transformer
     * is registered with TransformerRegistry.
     * 
     * @return A key indentifying the target type
     */
    String getTargetDataBinding();

    /**
     * Get the cost of the transformation. The weight can be used to choose the most efficient path if there are more
     * than one available from the source to the target.
     * 
     * @return An integer representing the cost of the transformation
     */
    int getWeight();
}
