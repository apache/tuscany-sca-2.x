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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.oasisopen.sca.ServiceRuntimeException;

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
    
    private InputStream getResourceAsStream(final String resource) {
        return AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
            public InputStream run() {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                return cl.getResourceAsStream(resource);
            }
        });
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
                        getResourceAsStream(DeploymentConstants.AXIS2_CONFIGURATION_RESOURCE);
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
                module.setName("rampart");
                module.setVersion("1.4");
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
           
            calculateDefaultModuleVersion(axisConfig.getModules(), axisConfig);
            axisConfig.validateSystemPredefinedPhases();
        } catch (IOException e) {
            throw new DeploymentException(e);
        }
    }
    /**
     * Get the name of the module , where archive name is combination of module name + its version
     * The format of the name is as follows:
     * moduleName-00.0000
     * Example: "addressing-01.0001.mar" would return "addressing"
     *
     * @param moduleName the name of the module archive
     * @return the module name parsed out of the file name
     */
    public static String getModuleName(String moduleName) {
        if (moduleName.endsWith("-SNAPSHOT")) {
            return moduleName.substring(0, moduleName.indexOf("-SNAPSHOT"));
        }
        char delimiter = '-';
        int version_index = moduleName.lastIndexOf(delimiter);
        if (version_index > 0) {
            String versionString = getModuleVersion(moduleName);
            if (versionString == null) {
                return moduleName;
            } else {
                return moduleName.substring(0, version_index);
            }
        } else {
            return moduleName;
        }
    }

    public static String getModuleVersion(String moduleName) {
        if (moduleName.endsWith("-SNAPSHOT")) {
            return "SNAPSHOT";
        }
        char version_seperator = '-';
        int version_index = moduleName.lastIndexOf(version_seperator);
        if (version_index > 0) {
            String versionString = moduleName.substring(version_index + 1, moduleName.length());
            try {
                Float.parseFloat(versionString);
                return versionString;
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static String getModuleName(String moduleName, String moduleVersion) {
        if (moduleVersion != null && moduleVersion.length() != 0) {
            moduleName = moduleName + "-" + moduleVersion;
        } 
        return moduleName;
    }

    public static boolean isLatest(String moduleVersion, String currentDefaultVersion) {
        if (AxisModule.VERSION_SNAPSHOT.equals(moduleVersion)) {
            return true;
        } else {
            float m_version = Float.parseFloat(moduleVersion);
            float m_c_vresion = Float.parseFloat(currentDefaultVersion);
            return m_version > m_c_vresion;
        }
    }

    public static void calculateDefaultModuleVersion(HashMap modules,
                                                     AxisConfiguration axisConfig) {
        Iterator allModules = modules.values().iterator();
        HashMap defaultModules = new HashMap();
        while (allModules.hasNext()) {
            AxisModule axisModule = (AxisModule) allModules.next();
            String moduleName = axisModule.getName();
            String moduleNameString;
            String moduleVersionString;
            if (AxisModule.VERSION_SNAPSHOT.equals(axisModule.getVersion())) {
                moduleNameString = axisModule.getName();
                moduleVersionString = axisModule.getVersion();
            } else {
                if (axisModule.getVersion() == null) {
                    moduleNameString = getModuleName(moduleName);
                    moduleVersionString = getModuleVersion(moduleName);
                    if (moduleVersionString != null) {
                        try {
                            Float.valueOf(moduleVersionString);
                            axisModule.setVersion(moduleVersionString);
                            axisModule.setName(moduleName);
                        } catch (NumberFormatException e) {
                            moduleVersionString = null;
                        }
                    }
                } else {
                    moduleNameString = axisModule.getName();
                    moduleVersionString = axisModule.getVersion();
                }
            }
            String currentDefaultVerison = (String) defaultModules.get(moduleNameString);
            if (currentDefaultVerison != null) {
                // if the module version is null then , that will be ignore in this case
                if (!AxisModule.VERSION_SNAPSHOT.equals(currentDefaultVerison)) {
                    if (moduleVersionString != null &&
                        isLatest(moduleVersionString, currentDefaultVerison)) {
                        defaultModules.put(moduleNameString, moduleVersionString);
                    }
                }
            } else {
                defaultModules.put(moduleNameString, moduleVersionString);
            }

        }
        Iterator def_mod_itr = defaultModules.keySet().iterator();
        while (def_mod_itr.hasNext()) {
            String moduleName = (String) def_mod_itr.next();
            axisConfig.addDefaultModuleVersion(moduleName, (String) defaultModules.get(moduleName));
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

}
