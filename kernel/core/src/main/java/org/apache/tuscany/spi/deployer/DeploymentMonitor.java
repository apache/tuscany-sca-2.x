package org.apache.tuscany.spi.deployer;

import org.apache.tuscany.api.TuscanyException;
import org.apache.tuscany.api.annotation.LogLevel;

/**
 * @version $Rev$ $Date$
 */
public interface DeploymentMonitor {

    @LogLevel("DEBUG")
    void startDeployment();

    @LogLevel("DEBUG")
    void endDeployment();

    @LogLevel("SEVERE")
    void deploymentError(TuscanyException e);

}
