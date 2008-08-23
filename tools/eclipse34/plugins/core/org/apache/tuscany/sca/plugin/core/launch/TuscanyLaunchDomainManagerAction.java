/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tuscany.sca.plugin.core.launch;

import static org.apache.tuscany.sca.plugin.core.launch.DomainManagerLauncherUtil.launchDomainManager;
import static org.apache.tuscany.sca.plugin.core.log.LogUtil.error;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Launch the SCA DomainManager.
 * 
 * @version $Rev: $ $Date: $
 */
public class TuscanyLaunchDomainManagerAction implements IWorkbenchWindowActionDelegate {
    
    private IWorkbenchWindow window;

    public TuscanyLaunchDomainManagerAction() {
    }

    public void run(IAction action) {

        try {
            
            // Run with a progress monitor
            window.run(true, true, new IRunnableWithProgress() {

                public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
                    try {
                        
                        launchDomainManager(progressMonitor);
                            
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        progressMonitor.done();
                    }
                }
            });

        } catch (Exception e) {
            error("Could not launch SCA Domain Manager", e);
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }
}
