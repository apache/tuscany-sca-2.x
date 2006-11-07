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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.apache.tuscany.service.persistence.common.JpaConstants.*;

/**
 * Encpasulates the information in the persistence.xml file.
 * 
 * This class is expected to be interogated by the provider only 
 * during the creation of the entity manager factory. Hence none 
 * of the values are cached, rather every time a property is queried 
 * the underlying DOM is interogated.
 *
 */
class TuscanyPersistenceUnitInfo implements PersistenceUnitInfo  {

    /** Persistence DOM */
    private Node persistenceDom;

    /** Classloader */
    private ClassLoader classLoader;

    /** Root Url */
    private String rootUrl;

    /** XPath API */
    XPath xpath = XPathFactory.newInstance().newXPath();

    /**
     * Initializes the properties.
     * 
     * @param persistenceDom
     * @param classLoader
     * @param rootUrl
     */
    public TuscanyPersistenceUnitInfo(Node persistenceDom, ClassLoader classLoader, String rootUrl) {
        this.persistenceDom = persistenceDom;
        this.classLoader = classLoader;
        this.rootUrl = rootUrl;
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
        return getBooleanValue(persistenceDom, EXCLUDE_UNLISTED_CLASSES);
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

        List<String> jarFiles = getMultipleValues(persistenceDom, JAR_FILE);
        try {
            List<URL> jarUrls = new LinkedList<URL>();
            for (String jarFile : jarFiles) {
                jarUrls.add(new URL(jarFile));
            }
            return jarUrls;
        } catch (MalformedURLException ex) {
            throw new TuscanyJpaException(ex);
        }

    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getJtaDataSource()
     */
    public DataSource getJtaDataSource() {

        String jtaDsName = getSingleValue(persistenceDom, JTA_DATA_SOURCE);
        if (jtaDsName == null || "".equals(jtaDsName)) {
            return null;
        }
        return lookupDataSource(jtaDsName);

    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getManagedClassNames()
     */
    public List<String> getManagedClassNames() {
        return getMultipleValues(persistenceDom, CLASS);
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getMappingFileNames()
     */
    public List<String> getMappingFileNames() {
        return getMultipleValues(persistenceDom, MAPPING_FILE);
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getNewTempClassLoader()
     */
    public ClassLoader getNewTempClassLoader() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getNonJtaDataSource()
     */
    public DataSource getNonJtaDataSource() {

        String nonJtaDsName = getSingleValue(persistenceDom, NON_JTA_DATA_SOURCE);
        if (nonJtaDsName == null || "".equals(nonJtaDsName)) {
            return null;
        }
        return lookupDataSource(nonJtaDsName);

    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getPersistenceProviderClassName()
     */
    public String getPersistenceProviderClassName() {
        return getSingleValue(persistenceDom, PROVIDER);
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getPersistenceUnitName()
     */
    public String getPersistenceUnitName() {
        return getSingleValue(persistenceDom, NAME);
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getPersistenceUnitRootUrl()
     */
    public URL getPersistenceUnitRootUrl() {
        try {
            return new URL(rootUrl);
        } catch (MalformedURLException ex) {
            throw new TuscanyJpaException(ex);
        }
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getProperties()
     */
    public Properties getProperties() {
        return getProperties(persistenceDom);
    }

    /* (non-Javadoc)
     * @see javax.persistence.spi.PersistenceUnitInfo#getTransactionType()
     */
    public PersistenceUnitTransactionType getTransactionType() {
        String transactionType = getSingleValue(persistenceDom, TRANSACTION_TYPE);
        return "JTA".equals(transactionType) ? PersistenceUnitTransactionType.JTA : PersistenceUnitTransactionType.RESOURCE_LOCAL;
    }

    /*
     * Extracts additional properties.
     */
    private Properties getProperties(Node root) {

        try {
            NodeList nodeList = (NodeList) xpath.evaluate(PROPERTY, root, XPathConstants.NODESET);
            Properties data = new Properties();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element property = (Element) nodeList.item(i);
                data.put(property.getAttribute(PROPERTY_NAME), property.getAttribute(PROPERTY_VALUE));
            }

            return data;
        } catch (XPathExpressionException ex) {
            throw new TuscanyJpaException(ex);
        }

    }

    /*
     * Gets multiple values for the specified expression.
     */
    private List<String> getMultipleValues(Node context, String expression) {

        try {
            NodeList nodeList = (NodeList) xpath.evaluate(expression, context, XPathConstants.NODESET);
            List<String> data = new LinkedList<String>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                data.add(nodeList.item(i).getTextContent());
            }

            return data;
        } catch (XPathExpressionException ex) {
            throw new TuscanyJpaException(ex);
        }

    }

    /*
     * Gets single value for the specified expression.
     */
    private String getSingleValue(Node context, String expression) {

        try {
            return xpath.evaluate(expression, context);
        } catch (XPathExpressionException ex) {
            throw new TuscanyJpaException(ex);
        }

    }

    /*
     * Gets single value for the specified expression.
     */
    private boolean getBooleanValue(Node context, String expression) {
        return Boolean.valueOf(getSingleValue(context, expression));
    }

    /*
     * Looks up datasource.
     */
    private DataSource lookupDataSource(String dsName) {
        Context ctx = null;
        try {
            ctx = new InitialContext();
            return (DataSource) ctx.lookup(dsName);
        } catch (NamingException ex) {
            throw new TuscanyJpaException(ex);
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException ex) {
                    throw new TuscanyJpaException(ex);
                }
            }
        }
    }

}
