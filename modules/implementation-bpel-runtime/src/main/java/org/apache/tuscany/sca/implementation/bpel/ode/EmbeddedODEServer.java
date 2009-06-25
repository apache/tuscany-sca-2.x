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

package org.apache.tuscany.sca.implementation.bpel.ode;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.transaction.TransactionManager;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.ode.axis2.BindingContextImpl;
//import org.apache.ode.axis2.EndpointReferenceContextImpl;
//import org.apache.ode.axis2.MessageExchangeContextImpl;
//import org.apache.ode.axis2.EndpointReferenceContextImpl;
import org.apache.ode.bpel.dao.BpelDAOConnectionFactoryJDBC;
import org.apache.ode.bpel.engine.BpelServerImpl;
import org.apache.ode.bpel.engine.CountLRUDehydrationPolicy;
import org.apache.ode.bpel.iapi.Scheduler;
import org.apache.ode.bpel.memdao.BpelDAOConnectionFactoryImpl;
import org.apache.ode.il.config.OdeConfigProperties;
import org.apache.ode.il.dbutil.Database;
import org.apache.ode.scheduler.simple.JdbcDelegate;
import org.apache.ode.scheduler.simple.SimpleScheduler;
import org.apache.ode.utils.GUID;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.eclipse.core.runtime.FileLocator;



/**
 * Embedded ODE process server
 * 
 * @version $Rev$ $Date$
 */
public class EmbeddedODEServer {
    protected final Log __log = LogFactory.getLog(getClass());

    private boolean _initialized;

    private OdeConfigProperties _config;

    private TransactionManager _txMgr;

    private Database _db;

    private File _workRoot;

    private BpelDAOConnectionFactoryJDBC _daoCF;

    private BpelServerImpl _bpelServer;

    private Scheduler _scheduler;
    
    protected ExecutorService _executorService;

    private Map<QName, RuntimeComponent> tuscanyRuntimeComponents = new ConcurrentHashMap<QName, RuntimeComponent>();
    
    public EmbeddedODEServer(TransactionManager txMgr) {
        _txMgr = txMgr;
    }
    
    public void init() throws ODEInitializationException {
        Properties p = System.getProperties();
        p.put("derby.system.home", "target");

        Properties confProps = new Properties();
        confProps.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=false)");
        _config = new OdeConfigProperties(confProps, "ode-sca");

        // Setting work root as the directory containing our database
        try {
        	_workRoot = getDatabaseLocationAsFile();
            //_workRoot = new File(dbLocation.toURI()).getParentFile();
        } catch (URISyntaxException e) {
            throw new ODEInitializationException(e);
        }

        initTxMgr();
        initPersistence();
        initBpelServer();

        try {
            _bpelServer.start();
        } catch (Exception ex) {
            String errmsg = "An error occured during the ODE BPEL server startup.";
            __log.error(errmsg, ex);
            throw new ODEInitializationException(errmsg, ex);
        }
        
        // Added MJE, 24/06/2009 - for 1.3.2 version of ODE
        _scheduler.start();
        // end of addition

        __log.info("ODE BPEL server started.");
        _initialized = true;
    }
    
    /**
     * Gets the location of the database used for the ODE BPEL engine as a File object for
     * the directory containing the database
     * @return
     * @throws ODEInitializationException
     * @throws URISyntaxException
     */
    private File getDatabaseLocationAsFile() throws ODEInitializationException,
                                                    URISyntaxException {
    	File locationFile = null;
    	// TODO - provide a system property / environment variable to set the path to the DB
    	
        URL dbLocation = getClass().getClassLoader().getResource("jpadb");
        // Handle OSGI bundle case
        if( dbLocation.getProtocol() == "bundleresource" ) {
        	try {
	        	dbLocation = FileLocator.toFileURL( dbLocation );
        	} catch (Exception ce ) {
        		throw new ODEInitializationException("Couldn't find database in the OSGi bundle");
        	} // end try
        } // end if 
        if (dbLocation == null)
            throw new ODEInitializationException("Couldn't find database in the classpath");
        locationFile = new File(dbLocation.toURI()).getParentFile();
    	return locationFile;
    } // end method getDatabaseLocationAsFile

    private void initTxMgr() {
        if(_txMgr == null) {
            try {
                GeronimoTxFactory txFactory = new GeronimoTxFactory();
                _txMgr = txFactory.getTransactionManager();
            } catch (Exception e) {
                __log.fatal("Couldn't initialize a transaction manager using Geronimo's transaction factory.", e);
                throw new ODEInitializationException("Couldn't initialize a transaction manager using " + "Geronimo's transaction factory.", e);
            }            
        }
    }

    private void initPersistence() {
        _db = new Database(_config);
        _db.setTransactionManager(_txMgr);
        _db.setWorkRoot(_workRoot);

        try {
            _db.start();
            _daoCF = _db.createDaoCF();
        } catch (Exception ex) {
            String errmsg = "Error while configuring ODE persistence.";
            __log.error(errmsg, ex);
            throw new ODEInitializationException(errmsg, ex);
        }
    }
  
    private void initBpelServer() {
        if (__log.isDebugEnabled()) {
            __log.debug("ODE initializing");
        }
        ThreadFactory threadFactory = new ThreadFactory() {
            int threadNumber = 0;
            public Thread newThread(Runnable r) {
                threadNumber += 1;
                Thread t = new Thread(r, "ODEServer-"+threadNumber);
                t.setDaemon(true);
                return t;
            }
        };

        _executorService = Executors.newCachedThreadPool(threadFactory);
        
        // executor service for long running bulk transactions
        ExecutorService _polledRunnableExecutorService = Executors.newCachedThreadPool(new ThreadFactory() {
            int threadNumber = 0;
            public Thread newThread(Runnable r) {
                threadNumber += 1;
                Thread t = new Thread(r, "PolledRunnable-"+threadNumber);
                t.setDaemon(true);
                return t;
            }
        });

        _bpelServer = new BpelServerImpl();
        _scheduler = createScheduler();
        _scheduler.setJobProcessor(_bpelServer);
        
        BpelServerImpl.PolledRunnableProcessor polledRunnableProcessor = new BpelServerImpl.PolledRunnableProcessor();
        polledRunnableProcessor.setPolledRunnableExecutorService(_polledRunnableExecutorService);
        polledRunnableProcessor.setContexts(_bpelServer.getContexts());
        _scheduler.setPolledRunnableProcesser(polledRunnableProcessor);

        _bpelServer.setDaoConnectionFactory(_daoCF);
        _bpelServer.setInMemDaoConnectionFactory(new BpelDAOConnectionFactoryImpl(_scheduler));
        
        //_bpelServer.setEndpointReferenceContext(eprContext);
        _bpelServer.setMessageExchangeContext(new ODEMessageExchangeContext(this));
        _bpelServer.setBindingContext(new ODEBindingContext());
        _bpelServer.setScheduler(_scheduler);
        if (_config.isDehydrationEnabled()) {
            CountLRUDehydrationPolicy dehy = new CountLRUDehydrationPolicy();
            dehy.setProcessMaxAge(_config.getDehydrationMaximumAge());
            dehy.setProcessMaxCount(_config.getDehydrationMaximumCount());
            _bpelServer.setDehydrationPolicy(dehy);
        }
        _bpelServer.setConfigProperties(_config.getProperties());
        _bpelServer.init();
        _bpelServer.setInstanceThrottledMaximumCount(_config.getInstanceThrottledMaximumCount());
        _bpelServer.setProcessThrottledMaximumCount(_config.getProcessThrottledMaximumCount());
        _bpelServer.setProcessThrottledMaximumSize(_config.getProcessThrottledMaximumSize());
        _bpelServer.setHydrationLazy(_config.isHydrationLazy());
        _bpelServer.setHydrationLazyMinimumSize(_config.getHydrationLazyMinimumSize());
    }

    public void stop() throws ODEShutdownException {
        if(_bpelServer != null) {
            try {
                __log.debug("Stopping BPEL Embedded server");
                _bpelServer.shutdown();
                _bpelServer = null;
            } catch (Exception ex) {
                __log.debug("Error stopping BPEL server");
            }
        }

        if(_scheduler != null) {
            try {
                __log.debug("Stopping scheduler");
                _scheduler.shutdown();
                _scheduler = null;
            } catch (Exception ex) {
                __log.debug("Error stopping scheduler");
            }
        }
        
        if(_daoCF != null) {
            try {
                __log.debug("Stopping DAO");
                _daoCF.shutdown();
                _daoCF = null;
            } catch (Exception ex) {
                __log.debug("Error stopping DAO");
            }
        }
        
        if(_db != null) {
            try {
                __log.debug("Stopping DB");
                _db.shutdown();
                _db = null;
            } catch (Exception ex) {
                __log.debug("Error stopping DB");
            }
        }
        
        if(_txMgr != null) {
            try {
                __log.debug("Stopping Transaction Manager");
                _txMgr = null;
            } catch (Exception ex) {
                __log.debug("Error stopping Transaction Manager");
            }
        }
    }

    protected Scheduler createScheduler() {
    	Properties odeProperties = new Properties();
    	// TODO Find correct values for these properties - MJE 22/06/2009
    	odeProperties.put("ode.scheduler.queueLength", "100" );
    	odeProperties.put("ode.scheduler.immediateInterval", "30000" );
    	odeProperties.put("ode.scheduler.nearFutureInterval", "600000" );
    	odeProperties.put("ode.scheduler.staleInterval", "100000" );
    	
        SimpleScheduler scheduler = new SimpleScheduler(new GUID().toString(),
        		                                        new JdbcDelegate(_db.getDataSource()),
                                                        odeProperties );
        scheduler.setTransactionManager(_txMgr);

        return scheduler;
    }

    public boolean isInitialized() {
        return _initialized;
    }

    public BpelServerImpl getBpelServer() {
        return _bpelServer;
    }
    
    public Scheduler getScheduler() {
        return _scheduler;
    }
    
    public ExecutorService getExecutor() {
    	return _executorService;
    }

    // Updated by Mike Edwards, 23/05/2008
    public void deploy(ODEDeployment d, BPELImplementation implementation) {
        try {
        	TuscanyProcessConfImpl processConf = new TuscanyProcessConfImpl( implementation );
        	_bpelServer.register(processConf);
        	__log.debug("Completed calling new Process deployment code...");
        } catch (Exception ex) {
            String errMsg = ">>> DEPLOY: Unexpected exception: " + ex.getMessage();
            __log.debug(errMsg, ex);
            throw new ODEDeploymentException(errMsg,ex);
        }
    }

    public void undeploy(ODEDeployment d) {
        //TODO
    }
    
    public void registerTuscanyRuntimeComponent(QName processName,RuntimeComponent componentContext) {
        tuscanyRuntimeComponents.put(processName, componentContext);
    }
        
    public RuntimeComponent getTuscanyRuntimeComponent(QName processName) {
        return tuscanyRuntimeComponents.get(processName);
    }
}
