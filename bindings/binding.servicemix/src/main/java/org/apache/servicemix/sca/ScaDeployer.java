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
package org.apache.servicemix.sca;

import java.io.File;

import javax.jbi.management.DeploymentException;

import org.apache.servicemix.common.AbstractDeployer;
import org.apache.servicemix.common.BaseComponent;
import org.apache.servicemix.common.ServiceUnit;

public class ScaDeployer extends AbstractDeployer {

	public static final String SCA_MODULE_FILE = "sca.module";
	
	public ScaDeployer(BaseComponent component) {
		super(component);
	}

	public boolean canDeploy(String serviceUnitName, String serviceUnitRootPath) {
		File module = new File(serviceUnitRootPath, SCA_MODULE_FILE);
		return module.exists() && module.isFile();
	}

	public ServiceUnit deploy(String serviceUnitName, String serviceUnitRootPath)
			throws DeploymentException {
		File module = new File(serviceUnitRootPath, SCA_MODULE_FILE);
		if (!module.exists() || !module.isFile()) {
            throw failure("deploy", "No sca.module found", null);
		}
		try {
			ScaServiceUnit su = new ScaServiceUnit();
	        su.setComponent(component);
	        su.setName(serviceUnitName);
	        su.setRootPath(serviceUnitRootPath);
	        su.init();
	        return su;
		} catch (Exception e) {
			throw failure("deploy", "Error loading sca module", e);
		}
	}

}
