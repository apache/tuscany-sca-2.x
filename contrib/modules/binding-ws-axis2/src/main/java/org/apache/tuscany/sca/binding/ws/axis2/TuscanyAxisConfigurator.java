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
package org.apache.tuscany.sca.binding.ws.axis2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.deployment.DeploymentConstants;
import org.apache.axis2.deployment.DeploymentErrorMsgs;
import org.apache.axis2.deployment.DeploymentException;
import org.apache.axis2.deployment.ModuleBuilder;
import org.apache.axis2.deployment.URLBasedAxisConfigurator;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.AxisConfigurator;
import org.apache.axis2.i18n.Messages;
import org.apache.axis2.util.Loader;

/**
 * Helps configure Axis2 from a resource in binding.ws.axis2 instead of Axis2.xml 
 * <p/> TODO: Review: should there be a single global Axis ConfigurationContext
 *
 * @version $Rev$ $Date$
 */
public class TuscanyAxisConfigurator extends URLBasedAxisConfigurator implements AxisConfigurator {
    
    /* these two fields are part of a temporary fix to solve problems that Maven has with including
     * rampart-1.4.mar into the classpath and also at the time of Release 1.0 rampart-1.4.mar seems
     * to pull in a SNAPSHOT version of rampart-project pom.  Hence rampart.mar has been excluded
     * as a Maven dependency and has been packed with this module 
     */
    /************start of fix *********************************************************************/
    private URL axis2_xml = 
        getResource("/org/apache/tuscany/sca/binding/ws/axis2/engine/config/axis2.xml");
    private URL axis2_repository = null;
    private URL rampart_mar_url =
        getResource("/org/apache/tuscany/sca/binding/ws/axis2/engine/config/modules/rampart-1.4.mar");
    /************** end of fix *************************************************************/
    
    private boolean isRampartRequired;
    
    public TuscanyAxisConfigurator(boolean isRampartRequired) throws AxisFault {
        //super(TuscanyAxisConfigurator.class.getResource("/org/apache/tuscany/sca/binding/ws/axis2/engine/config/axis2.xml"), 
        //      TuscanyAxisConfigurator.class.getResource("/org/apache/tuscany/sca/binding/ws/axis2/engine/config/modules/rampart.mar"));
        super(getResource("/org/apache/tuscany/sca/binding/ws/axis2/engine/config/axis2.xml"), 
                    null);
        this.isRampartRequired = isRampartRequired;
    }
    
    private static URL getResource(final String name) {
        return AccessController.doPrivileged(new PrivilegedAction<URL>() {
            public URL run() {
                return TuscanyAxisConfigurator.class.getResource(name);
            }
        });
    }

    public ConfigurationContext getConfigurationContext() throws AxisFault {
        if (configContext == null) {
            configContext = ConfigurationContextFactory.createConfigurationContext(this);
        }
        return configContext;
    }
    
    /* these three methods are part of a temporary fix to solve problems that Maven has with including
     * rampart-1.3.mar into the classpath and also at the time of Release 1.0 rampart-1.3.mar seems
     * to pull in a SNAPSHOT version of rampart-project pom.  Hence rampart.mar has been excluded
     * as a Maven dependency and has been packed with this module 
     */
    /************start of fix *********************************************************************/
    @Override
    public AxisConfiguration getAxisConfiguration() throws AxisFault {
        InputStream axis2xmlStream;
        try {
            if (axis2_xml == null) {
                axis2xmlStream =
                        Loader.getResourceAsStream(DeploymentConstants.AXIS2_CONFIGURATION_RESOURCE);
            } else {
                axis2xmlStream = axis2_xml.openStream();
            }
            axisConfig = populateAxisConfiguration(axis2xmlStream);
            if (isRampartRequired) {
                axisConfig.addGlobalModuleRef("rampart");
            }   
            if (axis2_repository == null) {
                Parameter axis2repoPara = axisConfig.getParameter(DeploymentConstants.AXIS2_REPO);
                if (axis2repoPara != null) {
                    String repoValue = (String) axis2repoPara.getValue();
                    if (repoValue != null && !"".equals(repoValue.trim())) {
                        if (repoValue.startsWith("file:/")) {
                            // we treat this case specially , by assuming file is
                            // located in the local machine
                            loadRepository(repoValue);
                        } else {
                            loadRepositoryFromURL(new URL(repoValue));
                        }
                    }
                } else {
                    //log.info("No repository found , module will be loaded from classpath");
                    try {
                        loadFromClassPath(); 
                    } catch ( Exception e ) {
                        if (isRampartRequired) {
                            loadRampartModule();
                        }
                    }
                }
                
            } else {
                loadRepositoryFromURL(axis2_repository);
            }

        } catch (IOException e) {
            throw new AxisFault(e.getMessage());
        }
        axisConfig.setConfigurator(this);
        return axisConfig;
    }
    
    public void loadRampartModule() throws DeploymentException {
        try {
            ClassLoader deploymentClassLoader =
                    org.apache.axis2.deployment.util.Utils.createClassLoader(
                            new URL[]{rampart_mar_url},
                            axisConfig.getModuleClassLoader(),
                            true,
                            (File) axisConfig.getParameterValue(Constants.Configuration.ARTIFACTS_TEMP_DIR));
            final AxisModule module = new AxisModule();
            module.setModuleClassLoader(deploymentClassLoader);
            module.setParent(axisConfig);
            //String moduleFile = fileUrl.substring(0, fileUrl.indexOf(".mar"));
            if (module.getName() == null) {
                module.setName(org.apache.axis2.util.Utils.getModuleName("rampart-1.4"));
                module.setVersion(org.apache.axis2.util.Utils.getModuleVersion("rampart-1.4"));
            }
            populateModule(module, rampart_mar_url);
            module.setFileName(rampart_mar_url);
            // Allow privileged access to read properties. Requires PropertiesPermission read in
            // security policy.
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                    public Object run() throws IOException {
                        addNewModule(module, axisConfig);
                        return null;
                    }
                });
            } catch (PrivilegedActionException e) {
                throw (AxisFault)e.getException();
            }            
           
            org.apache.axis2.util.Utils.
                    calculateDefaultModuleVersion(axisConfig.getModules(), axisConfig);
            axisConfig.validateSystemPredefinedPhases();
        } catch (IOException e) {
            throw new DeploymentException(e);
        }
    }
    
    private void populateModule(AxisModule module, URL moduleUrl) throws DeploymentException {
        try {
            ClassLoader classLoader = module.getModuleClassLoader();
            InputStream moduleStream = classLoader.getResourceAsStream("META-INF/module.xml");
            if (moduleStream == null) {
                moduleStream = classLoader.getResourceAsStream("meta-inf/module.xml");
            }
            if (moduleStream == null) {
                throw new DeploymentException(
                        Messages.getMessage(
                                DeploymentErrorMsgs.MODULE_XML_MISSING, moduleUrl.toString()));
            }
            ModuleBuilder moduleBuilder = new ModuleBuilder(moduleStream, module, axisConfig);
            moduleBuilder.populateModule();
        } catch (IOException e) {
            throw new DeploymentException(e);
        }
    }
    
    /************** end of fix *************************************************************/

}
