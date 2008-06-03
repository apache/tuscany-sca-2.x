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

package org.apache.tuscany.sca.core.launch;

import static org.apache.tuscany.sca.core.launch.NodeLauncherUtil.launchNode;
import static org.apache.tuscany.sca.core.log.LogUtil.error;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * A launch shortcut for SCA .composite files.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyLaunchNodeShortcut implements ILaunchShortcut {
    
    public void launch(final ISelection selection, final String mode) {

        try {
            
            // Make sure we have a .composite file selected
            if (!(selection instanceof IStructuredSelection)) {
                return;
            }
            Object[] selections = ((IStructuredSelection)selection).toArray();
            if (selections.length == 0) {
                return;
            }
            final IFile file = (IFile)selections[0];
            if (!file.getFileExtension().equals("composite")) {
                return;
            }
            
            // Run with a progress monitor
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true, true, new IRunnableWithProgress() {

                public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
                    try {
                        progressMonitor.beginTask("Starting SCA Composite", 100);
                        
                        launchNode(mode, file, progressMonitor);
                        
                        progressMonitor.done();
                            
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        progressMonitor.done();
                    }
                }
            });

        } catch (Exception e) {
            error("Could not launch SCA composite", e);
        }
    }

    public void launch(IEditorPart editor, String mode) {
        //TODO later...
    }
    
}
