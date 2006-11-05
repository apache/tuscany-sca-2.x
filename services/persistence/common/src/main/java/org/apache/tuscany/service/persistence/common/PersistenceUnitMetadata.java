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
package org.apache.tuscany.service.persistence.common;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

/**
 * Encpasulates the information in the persistence.xml file.
 *
 */
class PersistenceUnitMetadata implements PersistenceUnitInfo {

    /**
     * Transaction type.
     */
    private PersistenceUnitTransactionType transactionType;
    
    /**
     * Configuration properties.
     */
    private Properties properties;
    
    /**
     * Root JAT URL.
     */
    private URL rootUrl;
    
    /**
     * Persistence unit name.
     */
    private String unitName;
    
    /**
     * Persistence provider class.
     */
    private String providerClass;
    
    /**
     * Non JTA Datasource.
     */
    private DataSource nonJtaDataSource;
    
    /**
     * Temporary classloader.
     */
    private ClassLoader tempClassLoader;
    
    /**
     * Mapped file names.
     */
    private List<String> mappingFileNames;
    
    /**
     * Mapped persistent classes.
     */
    private List<String> managedClassNames;
    
    /**
     * JTA datasource.
     */
    private DataSource jtaDataSource;
    
    /**
     * JAR file URLs.
     */
    private List<URL> jarFileUrls;
    
    /**
     * Classloader.
     */
    private ClassLoader classLoader;
    
    /**
     * Whether unlisted classes in the DD are exluded.
     */
    private boolean unlistedClassesExcluded;

    
    /**
     * Initializes the properties.
     * 
     * @param transactionType Transaction type.
     * @param properties Configuration properties.
     * @param rootUrl Root JAT URL.
     * @param unitName Persistence unit name.
     * @param providerClass Persistence provider class.
     * @param nonJtaDataSource Non JTA Datasource.
     * @param tempClassLoader Temporary classloader.
     * @param mappingFileNames Mapped file names.
     * @param managedClassNames Mapped persistent classes.
     * @param jtaDataSource JTA datasource.
     * @param jarFileUrls JAR file URLs.
     * @param classLoader Classloader.
     * @param unlistedClassesExcluded Whether unlisted classes in the DD are exluded.
     */
    public PersistenceUnitMetadata(PersistenceUnitTransactionType transactionType, Properties properties, URL rootUrl, String unitName, String providerClass, DataSource nonJtaDataSource, ClassLoader tempClassLoader, List<String> mappingFileNames, List<String> managedClassNames, DataSource jtaDataSource, List<URL> jarFileUrls, ClassLoader classLoader, boolean unlistedClassesExcluded) {
        super();
        this.transactionType = transactionType;
        this.properties = properties;
        this.rootUrl = rootUrl;
        this.unitName = unitName;
        this.providerClass = providerClass;
        this.nonJtaDataSource = nonJtaDataSource;
        this.tempClassLoader = tempClassLoader;
        this.mappingFileNames = mappingFileNames;
        this.managedClassNames = managedClassNames;
        this.jtaDataSource = jtaDataSource;
        this.jarFileUrls = jarFileUrls;
        this.classLoader = classLoader;
        this.unlistedClassesExcluded = unlistedClassesExcluded;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#addTransformer(javax.persistence.spi.ClassTransformer)
     */
    public void addTransformer(ClassTransformer classTransformer) {
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#excludeUnlistedClasses()
     */
    public boolean excludeUnlistedClasses() {
        return unlistedClassesExcluded;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getClassLoader()
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getJarFileUrls()
     */
    public List<URL> getJarFileUrls() {
        return jarFileUrls;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getJtaDataSource()
     */
    public DataSource getJtaDataSource() {
        return jtaDataSource;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getManagedClassNames()
     */
    public List<String> getManagedClassNames() {
        return managedClassNames;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getMappingFileNames()
     */
    public List<String> getMappingFileNames() {
        return mappingFileNames;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getNewTempClassLoader()
     */
    public ClassLoader getNewTempClassLoader() {
        return tempClassLoader;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getNonJtaDataSource()
     */
    public DataSource getNonJtaDataSource() {
        return nonJtaDataSource;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getPersistenceProviderClassName()
     */
    public String getPersistenceProviderClassName() {
        return providerClass;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getPersistenceUnitName()
     */
    public String getPersistenceUnitName() {
        return unitName;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getPersistenceUnitRootUrl()
     */
    public URL getPersistenceUnitRootUrl() {
        return rootUrl;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getProperties()
     */
    public Properties getProperties() {
        return properties;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getTransactionType()
     */
    public PersistenceUnitTransactionType getTransactionType() {
        return transactionType;
    }

}
