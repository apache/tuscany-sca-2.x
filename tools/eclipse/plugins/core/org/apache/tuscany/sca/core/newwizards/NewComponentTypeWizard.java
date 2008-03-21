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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * A wizard to create new .componentType files.
 *
 * @version $Rev$ $Date$
 */
public class NewComponentTypeWizard extends Wizard implements IWorkbenchWizard {

	private IWorkbench workbench;
	private IStructuredSelection selection;
	private NewComponentTypeWizardPage mainPage;

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	      this.workbench = workbench;
	      this.selection = selection;
	      setWindowTitle("New SCA ComponentType");
	}
	
	public void addPages() {
		mainPage = new NewComponentTypeWizardPage(workbench, selection);
		addPage(mainPage);
	}	
	
	@Override
	public boolean performFinish() {
		return mainPage.finish();
	}
}
