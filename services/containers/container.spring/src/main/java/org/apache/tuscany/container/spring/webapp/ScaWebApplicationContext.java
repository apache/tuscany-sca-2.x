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
package org.apache.tuscany.container.spring.webapp;

import java.net.URL;
import java.io.File;
import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.sca.ScaAdapterAware;
import org.springframework.sca.ScaAdapterPostProcessor;
import org.springframework.util.Assert;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import org.apache.tuscany.api.TuscanyRuntimeException;
import org.apache.tuscany.container.spring.config.SCANamespaceHandlerResolver;
import org.apache.tuscany.container.spring.impl.SpringScaAdapter;
import org.apache.tuscany.container.spring.impl.SpringRuntimeInfo;
import org.apache.tuscany.container.spring.model.SpringComponentType;
import org.apache.tuscany.runtime.webapp.TuscanyContextListener;
import org.apache.tuscany.runtime.webapp.WebappRuntime;
import org.apache.tuscany.runtime.webapp.WebappUtil;
import org.apache.tuscany.runtime.webapp.WebappUtilImpl;
import org.apache.tuscany.core.launcher.LauncherImpl;

/**
 * WebApplicationContext implementation that understands SCA extensions.
 * This class is not very complicated, the key issue is getting hold of a reference
 * to the Tuscany runtime which the webapp is using.
 *
 * @author Andy Piper
 */
public class ScaWebApplicationContext extends XmlWebApplicationContext
        implements ConfigurableWebApplicationContext {
    private WebappRuntime runtime;
    private SpringComponentType componentType;

    public ScaWebApplicationContext() {
    }

    protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
//    beanDefinitionReader.setEntityResolver(null);
        beanDefinitionReader
                .setNamespaceHandlerResolver(new SCANamespaceHandlerResolver(getClassLoader(), componentType));
    }

    public Resource getResource(String location) {
        Assert.notNull(location, "location is required");
        return super.getResource(location);
    }

    /* (non-Javadoc)
    * @see org.springframework.context.support.AbstractApplicationContext#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
    */
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        super.postProcessBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(new ScaAdapterPostProcessor
                (new SpringScaAdapter(componentType)));
        beanFactory.ignoreDependencyInterface(ScaAdapterAware.class);
    }

    protected void onRefresh() {
        if (runtime != null) { // egregious hack to prevent recursion in refresh()
            return;
        }
        ServletContext servletContext = getServletContext();
        WebappUtil utils = getUtils(servletContext);

        try {
            ClassLoader webappClassLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader bootClassLoader = utils.getBootClassLoader(webappClassLoader);
            runtime = utils.getRuntime(bootClassLoader);
            URL systemScdl = utils.getSystemScdl(bootClassLoader);
            URL applicationScdl = utils.getApplicationScdl(webappClassLoader);

            runtime.setMonitorFactory(runtime.createDefaultMonitorFactory());
            runtime.setApplicationName(utils.getApplicationName());
            runtime.setServletContext(servletContext);
            runtime.setHostClassLoader(webappClassLoader);
            runtime.setSystemScdl(systemScdl);
            runtime.setApplicationScdl(applicationScdl);
            runtime.setRuntimeInfo(new SpringWebappRuntimeInfo(getApplicationRootDirectory(), this));
            runtime.initialize();
        } catch (TuscanyRuntimeException e) {
            servletContext.log(e.getMessage(), e);
            throw e;
        }
    }

    protected WebappUtil getUtils(ServletContext servletContext) {
        return new WebappUtilImpl(servletContext);
    }

    protected void onClose() {
        if (runtime != null) {
            runtime.destroy();
            runtime = null;
        }
    }

    /**
     * What does this do and why to we need it?
     * @return
     */
    private File getApplicationRootDirectory() {
        String property = System.getProperty("tuscany.applicationRootDir");
        if (property != null) {
            return new File(property);
        }

        return new File(System.getProperty("user.dir"));
    }
}
