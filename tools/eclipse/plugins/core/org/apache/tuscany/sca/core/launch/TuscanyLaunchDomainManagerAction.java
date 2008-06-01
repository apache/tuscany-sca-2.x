package org.apache.tuscany.sca.core.launch;

import static org.apache.tuscany.sca.core.launch.DomainManagerLauncherUtil.launchDomainManager;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
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
            Platform.getLog(
                Platform.getBundle("org.apache.tuscany.sca.core")).log(
                new Status(IStatus.ERROR, "org.apache.tuscany.sca.core", IStatus.OK, "Could not launch SCA Domain Manager", e));
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
