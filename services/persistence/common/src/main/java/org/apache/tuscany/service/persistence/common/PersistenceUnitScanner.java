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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.spi.PersistenceUnitInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Scans the classloader for the specified persistence unit.
 *
 */
class PersistenceUnitScanner {
    
    /** Cache of persistence unit info */
    private Map<String, PersistenceUnitInfo> persistenceUnitInfos = new HashMap<String, PersistenceUnitInfo>();
    
    /**
     * Scans the lassloader for the specified persistence unit and creates 
     * an immutable representation of the information present in the matching 
     * persistence.xml file.
     * 
     * @param unitName Persistence unit name.
     * @param classLoader Classloader to scan.
     * @return Persistence unit information.
     */
    PersistenceUnitInfo getPersistenceUnitInfo(String unitName, ClassLoader classLoader) {
        
        
        synchronized (persistenceUnitInfos) {
            
            try {
                
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                
                XPathFactory xpathFactory = XPathFactory.newInstance();
                XPath xpath = xpathFactory.newXPath();
                
                Enumeration<URL> persistenceUnitUrls = classLoader.getResources("META-INF/persistence.xml");
                while(persistenceUnitUrls.hasMoreElements()) {
                    
                    URL persistenceUnitUrl = persistenceUnitUrls.nextElement();
                    Document root = db.parse(persistenceUnitUrl.openStream());
                    
                    String name = getSingleValue(xpath, root, "//persistence-unit/@name");
                    if(!unitName.equals(name)) {
                        continue;
                    }
                    
                    String transactionType = getSingleValue(xpath, root, "//persistence-unit/@transaction-type");
                    String provider = getSingleValue(xpath, root, "//persistence-unit/provider");
                    String jtaDsName = getSingleValue(xpath, root, "//persistence-unit/jta-data-source");
                    String nonJtaDsName = getSingleValue(xpath, root, "//persistence-unit/non-jta-data-source");
                    List<String> mappingFiles = getMultipleValues(xpath, root, "//persistence-unit/mapping-file");
                    List<String> jarFiles = getMultipleValues(xpath, root, "//persistence-unit/jar-file");
                    boolean exludeUnlistedClasses = getBooleanValue(xpath, root, "//persistence-unit/exclude-unlisted-classes");
                    List<String> managedClasses = getMultipleValues(xpath, root, "//persistence-unit/class");
                    
                    // TODO load properties
                    Properties prop = new Properties();
                    
                    PersistenceUnitInfo info = new TuscanyPersistenceUnitInfo(transactionType, prop, persistenceUnitUrl, unitName, provider, nonJtaDsName, null, mappingFiles, managedClasses, jtaDsName, jarFiles, classLoader, exludeUnlistedClasses);
                    persistenceUnitInfos.put(unitName, info);
                    return info;
                    
                }
            } catch (IOException ex) {
                throw new TuscanyJpaException(ex);
            } catch (ParserConfigurationException ex) {
                throw new TuscanyJpaException(ex);
            } catch (SAXException ex) {
                throw new TuscanyJpaException(ex);
            } catch (XPathExpressionException ex) {
                throw new TuscanyJpaException(ex);
            }
        }
        
        throw new TuscanyJpaException("Unabel to find persistence unit: " + unitName);
        
    }
    
    /*
     * Gets multiple values for the specified expression.
     */
    private List<String> getMultipleValues(XPath xpath, Node context, String expression) throws XPathExpressionException {
        
        NodeList nodeList = (NodeList)xpath.evaluate(expression, context, XPathConstants.NODESET);
        List<String> data = new LinkedList<String>();
        
        for(int i = 0;i < nodeList.getLength();i++) {
            data.add(nodeList.item(i).getTextContent());
        }
        
        return data;
        
    }
    
    /*
     * Gets single value for the specified expression.
     */
    private String getSingleValue(XPath xpath, Node context, String expression) throws XPathExpressionException {
        return xpath.evaluate(expression, context);
    }
    
    /*
     * Gets single value for the specified expression.
     */
    private boolean getBooleanValue(XPath xpath, Node context, String expression) throws XPathExpressionException {
        return Boolean.valueOf(xpath.evaluate(expression, context));
    }

}
