/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.async.work;

import javax.resource.spi.work.WorkManager;

import org.apache.geronimo.connector.work.GeronimoWorkManager;
import org.apache.geronimo.transaction.context.TransactionContextManager;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * A Work Manager service component implementation which just reuses the Geronimo WorkManager. 
 * 
 * @version $Rev$ $Date$
 */
@Service(WorkManager.class)
@Scope("MODULE")
public class DefaultWorkManager extends GeronimoWorkManager implements WorkManager {

    private final static int DEFAULT_POOL_SIZE = 10;
    
    @Property
    public int scheduledMaximumPoolSize;
    
    /**
     * Constructs a new instance.
     */
    public DefaultWorkManager() {
        super(DEFAULT_POOL_SIZE, new TransactionContextManager());
    }
    
    @Init(eager=true)
    public void init() throws Exception {
        doStart();
    }
    
    @Destroy
    public void destroy() throws Exception {
        doStop();
    }
    
    public void setScheduledMaximumPoolSize(int maxSize) {
        super.setScheduledMaximumPoolSize(maxSize);
    }
    
    public int getScheduledMaximumPoolSize() {
        return super.getScheduledMaximumPoolSize();
    }
    
}
