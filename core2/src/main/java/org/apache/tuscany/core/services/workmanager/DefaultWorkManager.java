package org.apache.tuscany.core.services.workmanager;

import javax.resource.spi.work.WorkManager;

import org.apache.geronimo.connector.work.GeronimoWorkManager;
import org.apache.geronimo.transaction.context.TransactionContextManager;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * A <code>WorkManager</code> service component implementation which just reuses the Geronimo WorkManager.
 *
 * @version $Rev$ $Date$
 */
@Service(WorkManager.class)
@Scope("MODULE")
public class DefaultWorkManager extends GeronimoWorkManager implements WorkManager {

    private final static int DEFAULT_POOL_SIZE = 10;

    public DefaultWorkManager() {
        super(DEFAULT_POOL_SIZE, new TransactionContextManager());
    }

    public DefaultWorkManager(int defaultSize) {
        super(defaultSize, new TransactionContextManager());
    }

    @Init(eager = true)
    public void init() throws Exception {
        doStart();
    }

    @Destroy
    public void destroy() throws Exception {
        doStop();
    }

    @Property
    public void setScheduledMaximumPoolSize(int maxSize) {
        super.setScheduledMaximumPoolSize(maxSize);
    }

    public int getScheduledMaximumPoolSize() {
        return super.getScheduledMaximumPoolSize();
    }

}
