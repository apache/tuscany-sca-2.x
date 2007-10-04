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

package org.apache.tuscany.sca.domain.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.util.FileHelper;
import org.apache.tuscany.sca.core.assembly.ActivationException;


/**
 * Some utility methods for the Domain implementation
 * 
 * @version $Rev: 556897 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
public class SCADomainUtil {
    private final static Logger logger = Logger.getLogger(SCADomainUtil.class.getName());
    
    /**
     * Given the name of a composite this method finds the contribution that it belongs to
     * this could be either a local directory of a jar file.
     * 
     * @param classLoader
     * @param compositeString
     * @return the contribution URL
     * @throws MalformedURLException
     */  
    public static URL findContributionFromComposite(ClassLoader classLoader, String compositeString)
      throws MalformedURLException {
    	   	
        URL contributionURL = classLoader.getResource(compositeString);
        
        if ( contributionURL != null ){ 
            String contributionURLString = contributionURL.toExternalForm();
            String protocol = contributionURL.getProtocol();
            
            if ("file".equals(protocol)) {
                // directory contribution
                if (contributionURLString.endsWith(compositeString)) {
                    String location = contributionURLString.substring(0, contributionURLString.lastIndexOf(compositeString));
                    // workaround from evil url/uri form maven
                    contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
                }
    
            } else if ("jar".equals(protocol)) {
                // jar contribution
                String location = contributionURLString.substring(4, contributionURLString.lastIndexOf("!/"));
                // workaround for evil url/uri from maven
                contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
            }
        } 
        
    	return contributionURL;
    } 
}