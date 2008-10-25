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

package org.apache.tuscany.sca.plugin.core.launch;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

/**
 * Launch configuration delegate for the Tuscany launch configuration.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate implements
    ILaunchConfigurationDelegate {

    public static final String TUSCANY_LAUNCH_CONFIGURATIONTYPE = "org.apache.tuscany.sca.plugin.core.launch.configurationtype";

    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
        throws CoreException {

        // Verify the configuration
        String mainTypeName = verifyMainTypeName(configuration);
        IVMInstall vm = verifyVMInstall(configuration);
        IVMRunner runner = vm.getVMRunner(mode);

        ExecutionArguments execArgs =
            new ExecutionArguments(getVMArguments(configuration), getProgramArguments(configuration));
        Map vmAttributesMap = getVMSpecificAttributesMap(configuration);
        String[] classpath = getClasspath(configuration);

        File workingDir = verifyWorkingDirectory(configuration);
        String workingDirName = null;
        if (workingDir != null)
            workingDirName = workingDir.getAbsolutePath();

        // Create a VM runner configuration
        VMRunnerConfiguration runConfig = new VMRunnerConfiguration(mainTypeName, classpath);
        runConfig.setProgramArguments(execArgs.getProgramArgumentsArray());
        runConfig.setVMArguments(execArgs.getVMArgumentsArray());
        runConfig.setVMSpecificAttributesMap(vmAttributesMap);
        runConfig.setWorkingDirectory(workingDirName);
        runConfig.setBootClassPath(getBootpath(configuration));

        // Run!!
        runner.run(runConfig, launch, monitor);
    }
}
