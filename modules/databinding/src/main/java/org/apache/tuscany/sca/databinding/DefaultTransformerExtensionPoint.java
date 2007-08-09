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
package org.apache.tuscany.sca.databinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.databinding.impl.DirectedGraph;
import org.apache.tuscany.sca.databinding.impl.LazyPullTransformer;
import org.apache.tuscany.sca.databinding.impl.LazyPushTransformer;
import org.apache.tuscany.sca.interfacedef.impl.TempServiceDeclarationUtil;

/**
 * @version $Rev$ $Date$
 */
public class DefaultTransformerExtensionPoint implements TransformerExtensionPoint {
    private DataBindingExtensionPoint dataBindings;
    private boolean loadedTransformers;
    
    private final DirectedGraph<Object, Transformer> graph = new DirectedGraph<Object, Transformer>();
    
    public DefaultTransformerExtensionPoint() {
    }
    
    //FIXME Hack
    public void setDataBindings(DataBindingExtensionPoint dataBindings) {
        this.dataBindings = dataBindings;
    }

    public void addTransformer(String sourceType, String resultType, int weight, Transformer transformer) {
        graph.addEdge(sourceType, resultType, transformer, weight);
    }

    public void addTransformer(Transformer transformer) {
        graph.addEdge(transformer.getSourceDataBinding(),
            transformer.getTargetDataBinding(),
            transformer,
            transformer.getWeight());
    }

    public boolean removeTransformer(String sourceType, String resultType) {
        return graph.removeEdge(sourceType, resultType);
    }

    public Transformer getTransformer(String sourceType, String resultType) {
        loadTransformers();
        
        DirectedGraph<Object, Transformer>.Edge edge = graph.getEdge(sourceType, resultType);
        return (edge == null) ? null : edge.getValue();
    }

    /**
     * Dynamically load transformers registered under META-INF/services.
     *
     */
    private void loadTransformers() {
        if (loadedTransformers) {
            return;
        }
        loadTransformers(PullTransformer.class);
        loadTransformers(PushTransformer.class);
        loadedTransformers = true;
    }

    /**
     * Dynamically load transformers registered under META-INF/services.
     * 
     * @param transformerClass
     */
    private void loadTransformers(Class<?> transformerClass) {

        // Get the transformer service declarations
        ClassLoader classLoader = transformerClass.getClassLoader();
        Set<String> transformerDeclarations; 
        try {
            transformerDeclarations = TempServiceDeclarationUtil.getServiceClassNames(classLoader, transformerClass.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // Load transformers
        for (String transformerDeclaration: transformerDeclarations) {
            Map<String, String> attributes = TempServiceDeclarationUtil.parseServiceDeclaration(transformerDeclaration);
            String className = attributes.get("class");
            String source = attributes.get("source");
            String target = attributes.get("target");
            int weight = Integer.valueOf(attributes.get("weight"));
                
            // Create a transformer wrapper and register it
            Transformer transformer;
            if (transformerClass == PullTransformer.class) {
                transformer = new LazyPullTransformer(source, target, weight, classLoader, className);
            } else {
                transformer = new LazyPushTransformer(source, target, weight, classLoader, className);
            }
            addTransformer(transformer);
        }
    }
    
    //FIXME The following methods should be on a different class from
    // extension point
    
    public List<Transformer> getTransformerChain(String sourceType, String resultType) {
        loadTransformers();
        
        String source = normalize(sourceType);
        String result = normalize(resultType);
        List<Transformer> transformers = new ArrayList<Transformer>();
        DirectedGraph<Object, Transformer>.Path path = graph.getShortestPath(source, result);
        if (path == null) {
            return null;
        }
        for (DirectedGraph<Object, Transformer>.Edge edge : path.getEdges()) {
            transformers.add(edge.getValue());
        }
        return transformers;
    }

    public String toString() {
        loadTransformers();
        
        return graph.toString();
    }

    /**
     * Normalize the id to a name of a data binding as databindings may have aliases
     * @param id
     * @return
     */
    private String normalize(String id) {
        loadTransformers();
        
        if (dataBindings != null) {
            DataBinding dataBinding = dataBindings.getDataBinding(id);
            return dataBinding == null ? id : dataBinding.getName();
        } else {
            return id;
        }
    }

}
