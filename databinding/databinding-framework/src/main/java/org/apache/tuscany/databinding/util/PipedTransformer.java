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

package org.apache.tuscany.databinding.util;

import org.apache.tuscany.databinding.DataPipe;
import org.apache.tuscany.databinding.PushStyleTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.Transformer;

/**
 *
 * @param <S>
 * @param <I>
 * @param <R>
 */
public class PipedTransformer<S, I, R> implements Transformer<S, R> {
    private PushStyleTransformer<S, I> pumper;

    private DataPipe<I, R> pipe;

    /**
     * @param pumper
     * @param pipe
     */
    public PipedTransformer(PushStyleTransformer<S, I> pumper, DataPipe<I, R> pipe) {
        super();
        this.pumper = pumper;
        this.pipe = pipe;
    }

    public R transform(S source, TransformationContext context) {
        pumper.transform(source, pipe.getSink(), context);
        return pipe.getResult();
    }

    public Class<S> getSourceType() {
        return pumper.getSourceType();
    }

    public Class<R> getResultType() {
        return pipe.getResultType();
    }

    public int getWeight() {
        return pumper.getWeight() + pipe.getWeight();
    }

}
