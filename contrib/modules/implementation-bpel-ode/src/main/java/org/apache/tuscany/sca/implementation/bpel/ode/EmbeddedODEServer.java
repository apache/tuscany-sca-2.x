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
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.transaction.TransactionManager;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.dao.BpelDAOConnectionFactoryJDBC;
import org.apache.ode.bpel.engine.BpelServerImpl;
import org.apache.ode.bpel.engine.CountLRUDehydrationPolicy;
import org.apache.ode.bpel.iapi.ProcessConf;
import org.apache.ode.bpel.iapi.ProcessStore;
import org.apache.ode.bpel.iapi.ProcessStoreEvent;
import org.apache.ode.bpel.iapi.ProcessStoreListener;
import org.apache.ode.bpel.iapi.Scheduler;
import org.apache.ode.bpel.memdao.BpelDAOConnectionFactoryImpl;
import org.apache.ode.il.config.OdeConfigProperties;
import org.apache.ode.il.dbutil.Database;
import org.apache.ode.scheduler.simple.JdbcDelegate;
import org.apache.ode.scheduler.simple.SimpleScheduler;
import org.apache.ode.store.ProcessStoreImpl;
import org.apache.ode.utils.GUID;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

//-------------- Added by Mike Edwards 16/05/2008
import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
//-------------- End of Mike Edwards additions

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

        // Setting work root as the directory containing our database (wherever in the classpath)
        URL dbLocation = getClass().getClassLoader().getResource("jpadb");
        if (dbLocation == null)
            throw new ODEInitializationException("Couldn't find database in the classpath");
        _workRoot = new File(dbLocation.getFile()).getParentFile();

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

        __log.info("ODE BPEL server started.");
        _initialized = true;
    }

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
        
        //FIXME: externalize the configuration for ThreadPoolMaxSize
        _executorService = Executors.newCachedThreadPool();
       
        _bpelServer = new BpelServerImpl();
        _scheduler = createScheduler();
        _scheduler.setJobProcessor(_bpelServer);

        _bpelServer.setDaoConnectionFactory(_daoCF);
        _bpelServer.setInMemDaoConnectionFactory(new BpelDAOConnectionFactoryImpl(_scheduler));
        // _bpelServer.setEndpointReferenceContext(new EndpointReferenceContextImpl(this));
        _bpelServer.setMessageExchangeContext(new ODEMessageExchangeContext(this));
        _bpelServer.setBindingContext(new ODEBindingContext());
        _bpelServer.setScheduler(_scheduler);
        if (_config.isDehydrationEnabled()) {
            CountLRUDehydrationPolicy dehy = new CountLRUDehydrationPolicy();
            _bpelServer.setDehydrationPolicy(dehy);
        }

        _bpelServer.init();
    } // end InitBpelServer

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
        SimpleScheduler scheduler = new SimpleScheduler(new GUID().toString(),new JdbcDelegate(_db.getDataSource()));
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
        	System.out.println("Completed calling new Process deployment code...");
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
