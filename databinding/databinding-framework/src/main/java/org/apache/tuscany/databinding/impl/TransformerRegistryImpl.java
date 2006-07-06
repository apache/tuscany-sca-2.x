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
package org.apache.tuscany.databinding.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.databinding.Mediator;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.Transformer;
import org.apache.tuscany.databinding.TransformerRegistry;
import org.apache.tuscany.databinding.util.DirectedGraph;

public class TransformerRegistryImpl implements TransformerRegistry, Mediator {

    private DirectedGraph<Object, Transformer> graph = new DirectedGraph<Object, Transformer>();

    public void registerTransformer(Object sourceType, Object resultType, Transformer transformer, int weight) {
        graph.addEdge(sourceType, resultType, transformer, weight);
    }

    public void registerTransformer(Transformer transformer) {
        graph.addEdge(transformer.getSourceType(), transformer.getResultType(), transformer, transformer.getWeight());
    }

    public boolean removeTransformer(Object sourceType, Object resultType) {
        return graph.removeEdge(sourceType, resultType);
    }

    public Transformer getTransformer(Object sourceType, Object resultType) {
        DirectedGraph<Object, Transformer>.Edge edge = graph.getEdge(sourceType, resultType);
        return (edge == null) ? null : edge.getValue();
    }

    public List<Transformer> getTransformerChain(Object sourceType, Object resultType) {
        List<Transformer> transformers = new ArrayList<Transformer>();
        DirectedGraph<Object, Transformer>.Path path = graph.getShortestPath(sourceType, resultType);
        for (DirectedGraph<Object, Transformer>.Edge edge : path.getEdges()) {
            transformers.add(edge.getValue());
        }
        return transformers;
    }

    public String toString() {
        return graph.toString();
    }

    @SuppressWarnings("unchecked")
    public Object mediate(Object source, Object sourceType, Object resultType, TransformationContext context) {
        List<Transformer> path = getTransformerChain(sourceType, resultType);

        Object result = source;
        for (Transformer transformer : path) {
            result = transformer.transform(result, context);
        }
        
        return result;
    }

}
