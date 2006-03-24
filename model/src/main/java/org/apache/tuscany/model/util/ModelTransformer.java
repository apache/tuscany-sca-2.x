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
package org.apache.tuscany.model.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A model transformer. Invokes a model content handler to perform the actual
 * transformation.
 */
public interface ModelTransformer {

    /**
     * Uses a ModelContentHandler to transform a model.
     */
    List<Object> transform(Iterator<Object> iterator, ModelContentHandler handler);

    /**
     * Uses a ModelContentHandler to transform a model.
     * Performs the first transform pass.
     */
    List<Object> transformPass1(Iterator<Object> iterator, ModelContentHandler handler,
                                List<Runnable> deferredHandlers);

    /**
     * Uses a ModelContentHandler to transform a model.
     * Performs the first transform pass.
     */
    List<Object> transformPass1(Iterator<Object> iterator, final ModelContentHandler handler,
                                List<Runnable> deferredHandlers, Map<Object, Object> targets,
                                List<Object> contents);

    /**
     * Uses a ModelContentHandler to transform a model.
     * Performs the second transform pass.
     */
    void transformPass2(List<Runnable> deferredHandlers);

}
