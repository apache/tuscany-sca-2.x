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
package org.apache.tuscany.container.script.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.osoa.sca.annotations.Constructor;

/**
 * Loader for handling implementation.script elements.
 * 
 * <implementation.script script="path/foo.py" class="myclass">
 * 
 */
public abstract class ScriptHelperImplementationLoader extends LoaderExtension<ScriptHelperImplementation> {

    @Constructor( { "registry" })
    public ScriptHelperImplementationLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public abstract QName getXMLType();

    protected String loadSource(ClassLoader cl, String resource) throws LoaderException {
        URL url = cl.getResource(resource);
        if (url == null) {
            throw new MissingResourceException(resource);
        }
        InputStream is;
        try {
            is = url.openStream();
        } catch (IOException e) {
            MissingResourceException mre = new MissingResourceException(resource, e);
            mre.setIdentifier(resource);
            throw mre;
        }
        try {
            Reader reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[1024];
            StringBuilder source = new StringBuilder();
            int count;
            while ((count = reader.read(buffer)) > 0) {
                source.append(buffer, 0, count);
            }
            return source.toString();
        } catch (IOException e) {
            LoaderException le = new LoaderException(e);
            le.setIdentifier(resource);
            throw le;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
