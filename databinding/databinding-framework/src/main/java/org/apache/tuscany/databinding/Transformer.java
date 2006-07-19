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


/**
 * Transformer transforms data from one binding format to the other one.
 * 
 * @param <S> The source data type
 * @param <R> the target data type
 */
public interface Transformer<S, R> {
    // FIXME: [rfeng] I'm not very sure if Class is a good id to represent the data type. Another option
    // is to use URI strings
    /**
     * Get the source type that this transformer transforms data from 
     * @return
     */
    public Class<S> getSourceType();

    /**
     * Get the target type that this transformer transforms data into 
     * @return
     */
    public Class<R> getTargetType();

    /**
     * Get the cost of the transformation. The weight can be used to choose the most efficient path if
     * there are more than one available from the source to the target.
     * @return 
     */
    public int getWeight();
}
