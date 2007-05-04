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

package org.apache.tuscany.implementation.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.apache.tuscany.contribution.service.ContributionReadException;

/**
 * TODO: Shouldn't this be using the contrabution service?
 */
public class ResourceHelper {

    public static String readResource(String scriptName) throws ContributionReadException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL scriptSrcUrl = cl.getResource(scriptName);
        if (scriptSrcUrl == null) {
            throw new ContributionReadException("No script: " + scriptName);
        }

        InputStream is;
        try {
            is = scriptSrcUrl.openStream();
        } catch (IOException e) {
            throw new ContributionReadException(e);
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
            throw new ContributionReadException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

}
