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
import java.util.Map;

import javax.persistence.spi.PersistenceUnitInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Scans the classloader for the specified persistence unit.
 *
 * @version $Rev$ $Date$
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
            
            if(persistenceUnitInfos.containsKey(unitName)) {
                return persistenceUnitInfos.get(unitName);
            }

            try {

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                Enumeration<URL> persistenceUnitUrls = classLoader.getResources("META-INF/persistence.xml");
                while (persistenceUnitUrls.hasMoreElements()) {

                    URL persistenceUnitUrl = persistenceUnitUrls.nextElement();
                    Document persistenceDom = db.parse(persistenceUnitUrl.openStream());

                    String rootJarUrl = persistenceUnitUrl.toString();
                    rootJarUrl = rootJarUrl.substring(0, rootJarUrl.lastIndexOf("META-INF"));

                    PersistenceUnitInfo info = new TuscanyPersistenceUnitInfo(persistenceDom, classLoader, rootJarUrl);
                    if (!unitName.equals(info.getPersistenceUnitName())) {
                        continue;
                    }

                    persistenceUnitInfos.put(unitName, info);
                    return info;

                }
            } catch (IOException ex) {
                throw new TuscanyJpaException(ex);
            } catch (ParserConfigurationException ex) {
                throw new TuscanyJpaException(ex);
            } catch (SAXException ex) {
                throw new TuscanyJpaException(ex);
            }

        }

        throw new TuscanyJpaException("Unabel to find persistence unit: " + unitName);

    }

}
