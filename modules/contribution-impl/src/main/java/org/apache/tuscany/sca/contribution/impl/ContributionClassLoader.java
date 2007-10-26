package org.apache.tuscany.sca.contribution.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.java.JavaImport;
import org.apache.tuscany.sca.contribution.namespace.NamespaceImport;

public class ContributionClassLoader extends URLClassLoader {
    
    private Contribution contribution;

    /**
     * Constructor for contribution classloader
     * 
     * @param contribution
     * @throws MalformedURLException
     */
    public ContributionClassLoader(Contribution contribution) {
        
        // To enable contributions to access code outside of SCA contributions
        // (typically by providing them on CLASSPATH), use the thread context
        // classloader as the parent of all contribution classloaders.
        
        super(new URL[0], Thread.currentThread().getContextClassLoader());
        this.contribution = contribution;
    }

    
    /**
     * Add the URL of the contribution to the classloader search path.
     * 
     * @param location Contribution URL
     */
    public void setContributionLocation(String location) {
        
        try {
            this.addURL(new URL(contribution.getLocation()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
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
                if (matchesImport(className, import_, true)) {
                    // Delegate the resolution to the imported contribution
                    for (Contribution exportingContribution : import_.getExportContributions()) {
                                    
                        if (exportingContribution.getClassLoader() instanceof ContributionClassLoader) {

                            for (Export export : exportingContribution.getExports()) {
                                try {
                                    if (import_.match(export)) {
                                        clazz = ((ContributionClassLoader)exportingContribution.getClassLoader()).findClassFromContribution(className);
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
     *     Parent classloader
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
                if (matchesImport(name, import_, false)) {
                    // Delegate the resolution to the imported contribution
                    for (Contribution exportingContribution : import_.getExportContributions()) {
                                
                        if (exportingContribution.getClassLoader() instanceof ContributionClassLoader) {

                            for (Export export : exportingContribution.getExports()) {
                                if (import_.match(export)) {
                                    url = ((ContributionClassLoader)exportingContribution.getClassLoader()).findResourceFromContribution(name);
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
              
        return collectionToEnumeration(findResourceSet(name));
    }
    
    

    
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#getResource(java.lang.String)
     * 
     * Find a resource. 
     * Search path for resource:
     *     Parent classloader
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
     * classloader.
     */
    @Override
    public Enumeration<URL> getResources(String resName) throws IOException {
       
        HashSet<URL> resourceSet = findResourceSet(resName);
        addEnumerationToCollection(resourceSet, super.getResources(resName));
        
        return collectionToEnumeration(resourceSet);
    }
    

    /*
     * Find set of resources
     */
    private HashSet<URL> findResourceSet(String name) throws IOException {
        
        HashSet<URL> resources = new HashSet<URL>();

        addEnumerationToCollection(resources, super.findResources(name));
        
        for (Import import_ : this.contribution.getImports()) {
            if (matchesImport(name, import_, false)) {
                // Delegate the resolution to the imported contribution
                for (Contribution exportingContribution : import_.getExportContributions()) {
                                
                    if (exportingContribution.getClassLoader() instanceof ContributionClassLoader) {

                        for (Export export : exportingContribution.getExports()) {
                            if (import_.match(export)) {
                                addEnumerationToCollection(resources,
                                        ((ContributionClassLoader)exportingContribution.getClassLoader()).findResources(name));
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
     * Check if a class or resource matches an import statement.
     * Class matches if the package name used in <import.java/> matches
     * Resource matches if package/namespace match the directory of resource.
     * 
     * @param name    Name of class or resource
     * @param import_ SCA contribution import
     * @param matchJavaClass
     * @return true if this is a matching import
     */
    private boolean matchesImport(String name, Import import_, boolean matchJavaClass) {
       
        if (matchJavaClass) {
            if (import_ instanceof JavaImport && name != null && name.lastIndexOf('.') > 0) {
                JavaImport javaImport = (JavaImport) import_;
                String packageName = name.substring(0, name.lastIndexOf('.'));
                if (javaImport.getPackage() == null)
                    return false;
                else
                    return packageName.equals(javaImport.getPackage());
            }
            
        } else {
            if (name == null || name.lastIndexOf('/') <= 0)
                return false;
            else if (import_ instanceof JavaImport) {
                JavaImport javaImport = (JavaImport) import_;
                String packageName = name.substring(0, name.lastIndexOf('/'));
                if (javaImport.getPackage() == null)
                    return false;
                else
                    return packageName.equals(javaImport.getPackage().replaceAll("\\.", "/"));
            } else if (import_ instanceof NamespaceImport) {
                NamespaceImport namespaceImport = (NamespaceImport) import_;
                String namespace = name.substring(0, name.lastIndexOf('/'));
                if (namespaceImport.getNamespace() == null)
                    return false;
                else
                    return namespaceImport.getNamespace().equals(namespace);
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
    
    /*
     * Return an enumeration corresponding to a collection
     */
    private <T extends Object> Enumeration<T>  collectionToEnumeration(Collection<T> collection) {
        
        final Iterator<T> iterator = collection.iterator();
        
        return new Enumeration<T>() {
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            public T nextElement() {
                return iterator.next();
            }
        };
    }


    @Override
    public String toString() {
        return "SCA contribution classloader for : " + contribution.getLocation();
    }
    
    
}
