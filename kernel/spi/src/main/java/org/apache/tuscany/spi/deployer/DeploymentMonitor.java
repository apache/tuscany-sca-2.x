package org.apache.tuscany.spi.deployer;

import org.apache.tuscany.api.TuscanyException;
import org.apache.tuscany.api.annotation.LogLevel;

/**
 * @version $Rev$ $Date$
 */
public interface DeploymentMonitor {

    @LogLevel("FINER")
    void startDeployment();

    @LogLevel("FINER")
    void endDeployment();

    @LogLevel("SEVERE")
    void deploymentError(TuscanyException e);

}
