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

package org.apache.tuscany.sca.contribution.java.impl;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.DefaultDelegatingModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * A Model Resolver for ClassReferences.
 *
 * @version $Rev$ $Date$
 */
public class ClassLoaderModelResolver extends URLClassLoader implements ModelResolver {
    private WeakReference<Contribution> contribution;
    private ProcessorContext context;
    private Map<String, ModelResolver> importResolvers = new HashMap<String, ModelResolver>();
    
    // a space to pass back the contribution that was used to resolve
    // a class via an import
    private Contribution contributionContainingClass;

    private static ClassLoader parentClassLoader(Contribution contribution) {
        if (contribution.getClassLoader() != null) {
            return contribution.getClassLoader();
        }
        ClassLoader parentClassLoader = ServiceDiscovery.getInstance().getContextClassLoader();
        return parentClassLoader;
    }

    private static URL[] getContributionURLs(final Contribution contribution) throws IOException {
        if (contribution.getClassLoader() != null) {
            // Do not include the contribution url
            return new URL[0];
        }
        List<URL> urls = new ArrayList<URL>();
        urls.add(new URL(contribution.getLocation()));
        urls.addAll(ContributionHelper.getNestedJarUrls(contribution));
        return urls.toArray(new URL[urls.size()]);
    }

    public ClassLoaderModelResolver(final Contribution contribution, FactoryExtensionPoint modelFactories) throws IOException {
        super(getContributionURLs(contribution), parentClassLoader(contribution));
        this.contribution = new WeakReference<Contribution>(contribution);
        // Index Java import resolvers by package name
        Map<String, List<ModelResolver>> resolverMap = new HashMap<String, List<ModelResolver>>();
        for (Import import_: this.contribution.get().getImports()) {
            if (import_ instanceof JavaImport) {
                JavaImport javaImport = (JavaImport)import_;
                List<ModelResolver> resolvers = resolverMap.get(javaImport.getPackage());
                if (resolvers == null) {
                    resolvers = new ArrayList<ModelResolver>();
                    resolverMap.put(javaImport.getPackage(), resolvers);
                }
                resolvers.add(javaImport.getModelResolver());
            }
        }

        // Create a delegating model resolver for each imported package
        for (Map.Entry<String, List<ModelResolver>> entry: resolverMap.entrySet()) {
            importResolvers.put(entry.getKey(), new DefaultDelegatingModelResolver(entry.getValue()));
        }
    }

    public void addModel(Object resolved, ProcessorContext context) {
        throw new IllegalStateException();
    }

    public Object removeModel(Object resolved, ProcessorContext context) {
        throw new IllegalStateException();
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved, ProcessorContext context) {
        if (!(unresolved instanceof ClassReference)) {
            return unresolved;
        }

        try {
            this.context = context;
            contributionContainingClass = contribution.get();
            
            // Load the class and return a class reference for it
            String className = ((ClassReference)unresolved).getClassName();
            Class<?> clazz = Class.forName(className, false, this);
            ClassReference classReference = new ClassReference(clazz);
            classReference.setContributionContainingClass(contributionContainingClass);
            contributionContainingClass = null;
            return modelClass.cast(classReference);

        } catch (ClassNotFoundException e) {
            return unresolved;
        }
    }

    @Override
    public URL findResource(String name) {

        //TODO delegate to the Java import resolvers

        URL url = super.findResource(name);
        return url;
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {

        //TODO delegate to the Java import resolvers
        //Enumeration<URL> importedResources;

        Enumeration<URL> resources = super.findResources(name);
        List<URL> allResources = new ArrayList<URL>();
        //for (; importedResources.hasMoreElements(); ) {
        //    allResources.add(importedResources.nextElement());
        //}
        for (; resources.hasMoreElements(); ) {
            allResources.add(resources.nextElement());
        }
        return Collections.enumeration(allResources);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        // Extract the package name
        int d = name.lastIndexOf('.');
        String packageName;
        if (d != -1) {
            packageName = name.substring(0, d);
        } else {
            packageName = null;
        }

        // First try to load the class using the Java import resolvers
        ModelResolver importResolver = importResolvers.get(packageName);
        if (importResolver != null) {
            ClassReference classReference = importResolver.resolveModel(ClassReference.class, new ClassReference(name), context);
            if (!classReference.isUnresolved()) {
                contributionContainingClass = classReference.getContributionContainingClass();
                return classReference.getJavaClass();
            }
        }

        // Next, try to load the class from the current contribution
        Class<?> clazz = super.findClass(name);
        return clazz;
    }

}
