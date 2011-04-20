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
package org.apache.tuscany.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.tuscany.sca.TuscanyRuntime;

/**
 * Maven Mojo to stop a Tuscany runtime that was started with the start mojo.
 * 
 * @goal stop
 * @execute phase="test-compile"
 * @description Stop a Tuscany runtime that was started with the start mojo
 */
public class TuscanyStopMojo extends AbstractMojo {

    /**
     * @parameter expression="${id}" default-value="defaultId"
     */
    private String id;

    public void execute() throws MojoExecutionException, MojoFailureException {
    	if (id.length() < 1) {
    		// if id is set to "" then stop all runtimes
    		for (String id : TuscanyStartMojo.runtimes.keySet()) {
            	TuscanyRuntime runtime = TuscanyStartMojo.runtimes.get(id);
          		runtime.stop();
                getLog().info("stopped Tuscany runtime " + id);
    		}
        	TuscanyStartMojo.runtimes.clear();
    	} else {
        	TuscanyRuntime runtime = TuscanyStartMojo.runtimes.remove(id);
        	if (runtime == null) {
                getLog().info("No started runtime found for ID " + id);
        	} else {
        		runtime.stop();
                getLog().info("stopped Tuscany runtime " + id);
        	}
    	}
    }
}
