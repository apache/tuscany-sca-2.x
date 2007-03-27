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
package org.apache.tuscany.services.contribution.installer;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.tuscany.services.contribution.ContributionRepositoryImpl;
import org.apache.tuscany.services.contribution.ContributionServiceImpl;
import org.apache.tuscany.services.spi.contribution.ContributionRepository;
import org.apache.tuscany.services.spi.contribution.ContributionService;

/**
 * Main class for Contribution Services installer. <code>
 * usage: java [jvm-options] -jar installer.jar <contributionURI> <contribution source> 
 * </code>
 * where the componentURI identifies the URI that identifies the new contribution and
 * contribution source is the actual archive with the contribution contents
 * 
 * @version $Rev: 513906 $ $Date: 2007-03-02 11:38:00 -0800 (Fri, 02 Mar 2007) $
 */
public class Main {

    /**
     * Main method.
     * 
     * @param args the command line args
     * @throws Throwable if there are problems launching the runtime or
     *             application
     */
    public static void main(String[] args) throws Throwable {

        if (args.length < 2) {
            usage();
            throw new AssertionError();
        }

        URI contributionURI = new URI(args[0]);
        
        File contributionSource = new File(args[1]);
        if (!contributionSource.exists()) {
            System.err.println(getMessage("org.apache.tuscany.services.contribution.installer.Invalid_Contribution", contributionSource));
            System.exit(2);
        }
        
        //start the process of installing the contribution
        ContributionRepository contributionRepository = new ContributionRepositoryImpl(null);
        ContributionService contributionService = new ContributionServiceImpl(contributionRepository, null);

        URL contributionURL = contributionSource.toURL();
        InputStream contributionStream = contributionURL.openStream();
        
        contributionService.contribute(contributionURI, contributionStream);
        
        System.exit(0);
    }

    private static void usage() {
        System.err.println(getMessage("org.apache.tuscany.services.contribution.installer.Usage"));
        System.exit(1);
    }

    private static String getMessage(String id, Object... params) {
        ResourceBundle bundle = ResourceBundle.getBundle(Main.class.getName());
        String message = bundle.getString(id);
        return MessageFormat.format(message, params);
    }
}
