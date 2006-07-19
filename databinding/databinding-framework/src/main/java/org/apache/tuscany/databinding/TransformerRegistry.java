/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.databinding;

import java.util.List;

/**
 * Registry for data transformers
 *
 */
public interface TransformerRegistry {
    /**
     * @param sourceType
     * @param resultType
     * @param transformer
     * @param weight
     */
    public void registerTransformer(Object sourceType, Object resultType, Transformer transformer, int weight);

    /**
     * @param transformer
     */
    public void registerTransformer(Transformer transformer);

    /**
     * @param sourceType
     * @param resultType
     * @return
     */
    public boolean removeTransformer(Object sourceType, Object resultType);

    /**
     * Get the direct Transformer which can transform data from source type to result type
     * 
     * @param sourceType
     * @param resultType
     * @return
     */
    public Transformer getTransformer(Object sourceType, Object resultType);

    /**
     * Get the a chain of Transformers which can transform data from source type to result type
     * @param sourceType
     * @param resultType
     * @return
     */
    public List<Transformer> getTransformerChain(Object sourceType, Object resultType);
}
