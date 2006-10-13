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
package org.apache.tuscany.container.javascript.utils.xmlfromxsd;

/**
 * This is a factory for creating XML Generators.  The types of XML Generators 
 * are enumerated in the XMLGenerator interface.
 *
 */
public class XMLGeneratorFactory {
    private static XMLGeneratorFactory factory = null;

    protected XMLGeneratorFactory() {

    }

    public static XMLGeneratorFactory getInstance() {
        if (factory == null) {
            factory = new XMLGeneratorFactory();
        }
        return factory;
    }

    public XMLGenerator createGenerator(int generatorType) {
        XMLGenerator generator = null;
        switch (generatorType) {
        case XMLGenerator.SDO_BASED: {
            // generator = new SDObasedXMLGenerator(new XMLfromXSDConfiguration());
            break;
        }
        case XMLGenerator.XMLBEANS_BASED: {
            generator = new XBbasedXMLGenerator(new XMLfromXSDConfiguration());
            break;
        }
        }

        return generator;
    }
}
