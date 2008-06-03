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

package org.apache.tuscany.sca.core.newwizards;

import static org.apache.tuscany.sca.core.log.LogUtil.error;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

/**
 * Wizard page for the new .composite file wizard.
 *
 * @version $Rev$ $Date$
 */
public class NewCompositeWizardPage extends WizardNewFileCreationPage {
        
        private IWorkbench workbench;

        public NewCompositeWizardPage(IWorkbench workbench, IStructuredSelection selection)  {
                super("New SCA Composite Page", selection);
                
                this.workbench = workbench;
                
                setTitle("SCA Composite");
                setDescription("Create a new SCA Composite.");
                
                try {
                        String location = FileLocator.toFileURL(Platform.getBundle("org.apache.tuscany.sca.core").getEntry("/")).getFile().toString();
                        setImageDescriptor(ImageDescriptor.createFromImageData((new ImageLoader()).load(location + "/icons/tuscany.gif")[0]));
                } catch (Exception e) {
                    error("Could not create wizard", e);
                }
                
                setFileName("sample.composite");
                
        }
        
        public boolean finish() {
                try {
                        IFile file = createNewFile();
                        
            IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
                IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
                IDE.openEditor(workbenchPage, file, true);
                } catch (Exception e) {
                    error("Could not open editor", e);
                        return false;
                }
                return true;
        }

        @Override
        protected InputStream getInitialContents() {

                IPath path = new Path(getFileName());
                String name = path.removeFileExtension().toString();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream);
                printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");              
                printWriter.println("<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\"");
                printWriter.println("    xmlns:t=\"http://tuscany.apache.org/xmlns/sca/1.0\"");
                printWriter.println("    xmlns:c=\"http://" + name + "\"");     
                printWriter.println("    targetNamespace=\"http://" + name + "\"");      
                printWriter.println("    name=\"" + name + "\">");
                printWriter.println();
                printWriter.println();
                printWriter.println("</composite>");
                printWriter.close();
                
                return new ByteArrayInputStream(outputStream.toByteArray());
        }
}
