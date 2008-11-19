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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.java.JavaImport;


public class ContributionClassLoader extends URLClassLoader {
// public class ContributionClassLoader  {
    
    private Contribution contribution;
    // private b urlClassLoader;
    
    /**
     * Constructor for contribution ClassLoader 
     * 
     * @param contribution
     * @param parent
     * @throws MalformedURLException
     */
    public ContributionClassLoader(Contribution contribution, final ClassLoader parent) {
        super(new URL[0], parent);
        // Note that privileged use of getContextClassLoader have been promoted to callers.
        // super(new URL[0], parent == null?Thread.currentThread().getContextClassLoader(): null);
        this.contribution = contribution;
        if (contribution.getLocation() != null) {
            try {
                this.addURL(new URL(contribution.getLocation()));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
     * Return the ClassLoader corresponding to a contribution providing an export
     * Create a new ClassLoader for the contribution if one does not exist
     */
    private ClassLoader getExportClassLoader(Contribution exportingContribution) {
    	ClassLoader cl = exportingContribution.getClassLoader();
        if (!(cl instanceof ContributionClassLoader)) {
            if (cl == null) {
                cl = getParent();
            }

            cl = new ContributionClassLoader(exportingContribution, cl);
            exportingContribution.setClassLoader(cl);
        }
        return cl;
    }
    
    /* (non-Javadoc)
     * @see java.net.URLClassLoader#findClass(java.lang.String)
     * 
     * Search path for class:
     *     This contribution
     *     Imported contributions
     */
    @Override   
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        
        Class<?> clazz = null;
        try {
            clazz = findClassFromContribution(className);
        } catch (ClassNotFoundException e) {
                
            for (Import import_ : this.contribution.getImports()) {
                if (classNameMatchesImport(className, import_)) {
                    // Delegate the resolution to the imported contribution
                    for (Contribution exportingContribution : ((JavaImportModelResolver)import_.getModelResolver()).getExportContributions()) {
                                    
                        ClassLoader exportClassLoader = getExportClassLoader(exportingContribution);
                        if (exportClassLoader instanceof ContributionClassLoader) {
                            
                            for (Export export : exportingContribution.getExports()) {
                                try {
                                    if (import_.match(export)) {
                                        clazz = ((ContributionClassLoader)exportClassLoader).findClassFromContribution(className);
                                        break;
                                    }
                                } catch (ClassNotFoundException e1) { 
                                    continue;
                                }
                                    
                            }
                            if (clazz != null)  break;
                        }
                    }
                    if (clazz != null) break;
                }
            }

            if (clazz == null) throw e;
        }
        return clazz;
    }

    
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
     * 
     * Search path for class:
     *     Parent ClassLoader
     *     This contribution
     *     Imported contributions
     *     
     */
    @Override
    protected synchronized Class<?> loadClass(String className, boolean resolveClass) 
        throws ClassNotFoundException {
       
        Class<?> clazz = null;
        try {
            
            if (this.getParent() != null)
                clazz = this.getParent().loadClass(className);
            
        } catch (ClassNotFoundException e) {
        }

        if (clazz == null)
            clazz = findClass(className);


        if (resolveClass)
            this.resolveClass(clazz);
        return clazz;
        
    }


   
    /*
     * (non-Javadoc)
     * 
     * @see java.net.URLClassLoader#findResource(java.lang.String)
     */
    @Override
    public URL findResource(String name) {
        
        URL url = findResourceFromContribution(name);
        
        if (url == null) {
            for (Import import_ : this.contribution.getImports()) {
                if (resourceNameMatchesImport(name, import_)) {
                    // Delegate the resolution to the imported contribution
                    for (Contribution exportingContribution : ((JavaImportModelResolver)import_.getModelResolver()).getExportContributions()) {
                                
                        ClassLoader exportClassLoader = getExportClassLoader(exportingContribution);
                        if (exportClassLoader instanceof ContributionClassLoader) {

                            for (Export export : exportingContribution.getExports()) {
                                if (import_.match(export)) {
                                    url = ((ContributionClassLoader)exportClassLoader).findResourceFromContribution(name);
                                    if (url != null) break;
                                }
                            }
                            if (url != null)  break;
                        }
                    }
                    if (url != null) break;
                }
            }

        }
        return url;
    }


    /* (non-Javadoc)
     * @see java.net.URLClassLoader#findResources(java.lang.String)
     */
    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
              
        return Collections.enumeration(findResourceSet(name));
    }
    
    

    
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#getResource(java.lang.String)
     * 
     * Find a resource. 
     * Search path for resource:
     *     Parent ClassLoader
     *     This contribution
     *     Imported contributions
     */
    @Override
    public URL getResource(String resName) {
 
        URL resource  = null;
        
        if (this.getParent() != null) {
            resource  = this.getParent().getResource(resName);
        }        
        if (resource == null)
            resource  = findResource(resName);
        
        return resource;
    }


    
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#getResources(java.lang.String)
     * 
     * Return list of resources from this contribution, resources
     * imported through imported contributions and resources from parent 
     * ClassLoader.
     */
    @Override
    public Enumeration<URL> getResources(String resName) throws IOException {
       
        HashSet<URL> resourceSet = findResourceSet(resName);
        addEnumerationToCollection(resourceSet, super.getResources(resName));
        
        return Collections.enumeration(resourceSet);
    }
    

    /*
     * Find set of resources
     */
    private HashSet<URL> findResourceSet(String name) throws IOException {
        
        HashSet<URL> resources = new HashSet<URL>();

        addEnumerationToCollection(resources, super.findResources(name));
        
        for (Import import_ : this.contribution.getImports()) {
            if (!(import_ instanceof JavaImport)) {
                continue;
            }
            if (resourceNameMatchesImport(name, import_)) {
                // Delegate the resolution to the imported contribution
                for (Contribution exportingContribution : ((JavaImportModelResolver)import_.getModelResolver()).getExportContributions()) {
                                
                    ClassLoader exportClassLoader = getExportClassLoader(exportingContribution);
                    if (exportClassLoader instanceof ContributionClassLoader) {

                        for (Export export : exportingContribution.getExports()) {
                            if (import_.match(export)) {
                                addEnumerationToCollection(resources,
                                        ((ContributionClassLoader)exportClassLoader).findResources(name));
                            }
                        }
                    }
                }
            }
         }

        return resources;
    }


    /*
     * Find class from contribution. If class has already been loaded, return loaded class.
     */
    private Class<?> findClassFromContribution(String className) throws ClassNotFoundException {
        
        Class<?> clazz = findLoadedClass(className);
        if (clazz == null)
            clazz =  super.findClass(className);
        return clazz;
       
    }
    
    /*
     * Find resource from contribution.
     */
    private URL findResourceFromContribution(String name) {
        
        return super.findResource(name);
    }
    
    /**
     * Check if a class name matches an import statement.
     * Class matches if the package name used in <import.java/> matches
     * 
     * @param name    Name of class 
     * @param import_ SCA contribution import
     * @return true if this is a matching import
     */
    private boolean classNameMatchesImport(String name, Import import_) {
        
        if (import_ instanceof JavaImport && name != null && name.lastIndexOf('.') > 0) {
            JavaImport javaImport = (JavaImport) import_;
                
            String packageName = name.substring(0, name.lastIndexOf('.'));
            if (javaImport.getPackage().endsWith(".*")) {
                String prefix = javaImport.getPackage().substring(0, javaImport.getPackage().length() -1);
                 if (packageName.startsWith(prefix)) {
                    return true;
                }
            } else {
                return packageName.equals(javaImport.getPackage());
            }
        }
        
        return false;
    }
    
    /**
     * Check if a resource name matches an import statement.
     * Resource matches if package/namespace match the directory of resource.
     * 
     * @param name    Name of resource
     * @param import_ SCA contribution import
     * @return true if this is a matching import
     */
    private boolean resourceNameMatchesImport(String name, Import import_) {
        
       
        if (name == null || name.lastIndexOf('/') <= 0)
            return false;
        else if (import_ instanceof JavaImport) {
            JavaImport javaImport = (JavaImport) import_;
            
            if (javaImport.getPackage().endsWith(".*")) {
                String packageName = name.substring(0, name.lastIndexOf('/')).replace('/', '.');
                String prefix = javaImport.getPackage().substring(0, javaImport.getPackage().length() -1);
                if (packageName.startsWith(prefix)) {
                    return true;
                }
            } else {
                // 'name' is a resource : contains "/" as separators
                // Get package name from resource name
                String packageName = name.substring(0, name.lastIndexOf('/'));
                 return packageName.equals(javaImport.getPackage().replaceAll("\\.", "/"));
            }
        } 
        return false;
    }
    
    /*
     * Add an enumeration to a Collection
     */
    private <T extends Object> void addEnumerationToCollection(Collection<T> collection, Enumeration<T> enumeration) {
        
        while (enumeration.hasMoreElements())
            collection.add(enumeration.nextElement());
    }
    
    
    @Override
    public String toString() {
        return "SCA Contribution ClassLoader location: " + contribution.getLocation() + " parent ClassLoader: " + getParent();
    }
    
    
}
