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

package org.apache.tuscany.sca.runtime.war;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WarContextListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(WarContextListener.class.getName());

    protected Launcher launcher;

    protected static final String DEFAULT_REPOSITORY_FOLDER = "sca-contributions";

    public void contextInitialized(ServletContextEvent event) {
        try {
            launcher = new Launcher(getRepositoryFolder(event));
            launcher.start();
        } catch (Throwable e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "exception starting SCA runtime", e);
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        if (launcher != null)
            try {
                launcher.stop();
            } catch (Throwable e) {
                logger.log(Level.SEVERE, "exception stopping SCA runtime", e);
            }
    }

    protected File getRepositoryFolder(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();

        File repositoryFolder;
        if (servletContext.getInitParameter("repositoryFolder") != null) {
            repositoryFolder = new File(servletContext.getInitParameter("repositoryFolder"));
        } else {
            repositoryFolder = new File(servletContext.getRealPath("sca-contributions"));
        }

        logger.info((new StringBuilder()).append("Contribution Repository -> ").append(repositoryFolder).toString());
        return repositoryFolder;
    }

}
