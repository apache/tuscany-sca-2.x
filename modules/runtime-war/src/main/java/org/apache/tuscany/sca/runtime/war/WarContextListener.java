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
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.tuscany.sca.host.webapp.WebAppServletHost;
import org.apache.tuscany.sca.runtime.Launcher;

public class WarContextListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(WarContextListener.class.getName());

    protected Launcher launcher;

    protected static final String DEFAULT_REPOSITORY_FOLDER = "/repository";

    public void contextInitialized(ServletContextEvent event) {
        try {
            String cp = initContextPath(event.getServletContext());
            hackContextPath(cp);
            launcher = new Launcher(getRepositoryFolder(event), cp);
            launcher.start();
        } catch (Throwable e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "exception starting SCA runtime", e);
        }
    }

    /**
     * TODO: How context paths work is still up in the air so for now
     *    this hacks in a path that gets some samples working
     */
    protected void hackContextPath(String cp) {
        try {
            WebAppServletHost.getInstance().setContextPath2(new URL(cp).getPath());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
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
            try {
                repositoryFolder = new File(servletContext.getRealPath(DEFAULT_REPOSITORY_FOLDER));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
//            repositoryFolder = new File(servletContext.getRealPath(DEFAULT_REPOSITORY_FOLDER));
        }

        logger.info((new StringBuilder()).append("Contribution Repository -> ").append(repositoryFolder).toString());
        return repositoryFolder;
    }

    /**
     * Initializes the contextPath
     * The 2.5 Servlet API has a getter for this, for pre 2.5 servlet
     * containers use an init parameter.
     */
    @SuppressWarnings("unchecked")
    public String initContextPath(ServletContext context) {
        String contextPath;
        if (Collections.list(context.getInitParameterNames()).contains("contextPath")) {
            contextPath = context.getInitParameter("contextPath");
        } else {
            Method m;
            try {
                m = context.getClass().getMethod("getContextPath", new Class[]{});
                try {
                    contextPath = (String)m.invoke(context, new Object[]{});
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("'contextPath' init parameter must be set for pre-2.5 servlet container");
            }
        }
        logger.info("initContextPath: " + contextPath);
        return "http://localhost:8080" + contextPath;
    }
}
