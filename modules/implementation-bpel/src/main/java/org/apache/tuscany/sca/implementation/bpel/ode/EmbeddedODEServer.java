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

import org.apache.ode.il.config.OdeConfigProperties;
import org.apache.ode.il.dbutil.Database;
import org.apache.ode.bpel.dao.BpelDAOConnectionFactoryJDBC;
import org.apache.ode.bpel.engine.BpelServerImpl;
import org.apache.ode.bpel.engine.CountLRUDehydrationPolicy;
import org.apache.ode.bpel.memdao.BpelDAOConnectionFactoryImpl;
import org.apache.ode.bpel.scheduler.quartz.QuartzSchedulerImpl;
import org.apache.ode.bpel.iapi.Scheduler;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.transaction.TransactionManager;

/**
 *
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
    private ExecutorService _executor;
    private Scheduler _scheduler;

    public void init() throws ODEInitializationException {
        _config = new OdeConfigProperties(new Properties(), "ode-sca");
        _workRoot = new File("ode-work");

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
        try {
            GeronimoTxFactory txFactory = new GeronimoTxFactory();
            _txMgr = txFactory.getTransactionManager();
        } catch (Exception e) {
            __log.fatal("Couldn't initialize a transaction manager using Geronimo's transaction factory.", e);
            throw new ODEInitializationException("Couldn't initialize a transaction manager using " +
                    "Geronimo's transaction factory.", e);
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
        if (_config.getThreadPoolMaxSize() == 0)
            _executor = Executors.newCachedThreadPool();
        else
            _executor = Executors.newFixedThreadPool(_config.getThreadPoolMaxSize());

        _bpelServer = new BpelServerImpl();
        _scheduler = createScheduler();
        _scheduler.setJobProcessor(_bpelServer);

        _bpelServer.setDaoConnectionFactory(_daoCF);
        _bpelServer.setInMemDaoConnectionFactory(new BpelDAOConnectionFactoryImpl(_scheduler));
//        _bpelServer.setEndpointReferenceContext(new EndpointReferenceContextImpl(this));
//        _bpelServer.setMessageExchangeContext(new MessageExchangeContextImpl(this));
//        _bpelServer.setBindingContext(new BindingContextImpl(this, _store));
        _bpelServer.setScheduler(_scheduler);
        if (_config.isDehydrationEnabled()) {
            CountLRUDehydrationPolicy dehy = new CountLRUDehydrationPolicy();
            _bpelServer.setDehydrationPolicy(dehy);
        }
        _bpelServer.init();
    }

    protected Scheduler createScheduler() {
        QuartzSchedulerImpl scheduler = new QuartzSchedulerImpl();
        scheduler.setExecutorService(_executor, 20);
        scheduler.setTransactionManager(_txMgr);
        scheduler.setDataSource(_db.getDataSource());
        scheduler.init();
        return scheduler;
    }    

    public boolean isInitialized() {
        return _initialized;
    }

}
