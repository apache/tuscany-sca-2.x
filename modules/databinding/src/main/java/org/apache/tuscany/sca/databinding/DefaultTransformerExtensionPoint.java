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
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.databinding.impl.DirectedGraph;
import org.apache.tuscany.sca.databinding.impl.ServiceConfigurationUtil;

/**
 * @version $Rev$ $Date$
 */
public class DefaultTransformerExtensionPoint implements TransformerExtensionPoint {
    private boolean loadedTransformers;
    
    private final DirectedGraph<Object, Transformer> graph = new DirectedGraph<Object, Transformer>();
    
    public DefaultTransformerExtensionPoint() {
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
        List<String> transformerDeclarations; 
        try {
            transformerDeclarations = ServiceConfigurationUtil.getServiceClassNames(classLoader, transformerClass.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // Load transformers
        for (String transformerDeclaration: transformerDeclarations) {
            Map<String, String> attributes = ServiceConfigurationUtil.parseServiceDeclaration(transformerDeclaration);
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
    
    /**
     * A transformer facade allowing transformers to be lazily loaded
     * and initialized.
     */
    private static class LazyPullTransformer implements PullTransformer<Object, Object> {

        private String source;
        private String target;
        private int weight;
        private WeakReference<ClassLoader> classLoader;
        private String className;
        private PullTransformer<Object, Object> transformer;

        public LazyPullTransformer(String source, String target, int weight, ClassLoader classLoader, String className) {
            this.source = source;
            this.target = target;
            this.weight = weight;
            this.classLoader = new WeakReference<ClassLoader>(classLoader);
            this.className = className;
        }

        /**
         * Load and instantiate the transformer class.
         * 
         * @return The transformer.
         */
        @SuppressWarnings("unchecked")
        private PullTransformer<Object, Object> getTransformer() {
            if (transformer == null) {
                try {
                    Class<PullTransformer<Object, Object>> transformerClass =
                        (Class<PullTransformer<Object, Object>>)Class.forName(className, true, classLoader.get());
                    Constructor<PullTransformer<Object, Object>> constructor = transformerClass.getConstructor();
                    transformer = constructor.newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return transformer;
        }

        public String getSourceDataBinding() {
            return source;
        }

        public String getTargetDataBinding() {
            return target;
        }

        public int getWeight() {
            return weight;
        }
        
        public Object transform(Object source, TransformationContext context) {
            return getTransformer().transform(source, context);
        }

    }

    /**
     * A transformer facade allowing transformers to be lazily loaded
     * and initialized.
     */
    private static class LazyPushTransformer implements PushTransformer<Object, Object> {

        private String source;
        private String target;
        private int weight;
        private WeakReference<ClassLoader> classLoader;
        private String className;
        private PushTransformer<Object, Object> transformer;

        public LazyPushTransformer(String source, String target, int weight, ClassLoader classLoader, String className) {
            this.source = source;
            this.target = target;
            this.weight = weight;
            this.classLoader = new WeakReference<ClassLoader>(classLoader);
            this.className = className;
        }

        /**
         * Load and instantiate the transformer class.
         * 
         * @return The transformer.
         */
        @SuppressWarnings("unchecked")
        private PushTransformer<Object, Object> getTransformer() {
            if (transformer == null) {
                try {
                    Class<PushTransformer<Object, Object>> transformerClass =
                        (Class<PushTransformer<Object, Object>>)Class.forName(className, true, classLoader.get());
                    Constructor<PushTransformer<Object, Object>> constructor = transformerClass.getConstructor();
                    transformer = constructor.newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return transformer;
        }

        public String getSourceDataBinding() {
            return source;
        }

        public String getTargetDataBinding() {
            return target;
        }

        public int getWeight() {
            return weight;
        }

        public void transform(Object source, Object sink, TransformationContext context) {
            getTransformer().transform(source, sink, context);
        }

    }

    
    //FIXME The following methods should be on a different class from
    // extension point
    
    public List<Transformer> getTransformerChain(String sourceType, String resultType) {
        loadTransformers();
        
        String source = sourceType;
        String result = resultType;
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

    @Override
    public String toString() {
        loadTransformers();
        
        return graph.toString();
    }

}
