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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.spi.PersistenceProvider;
import javax.transaction.TransactionManager;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.dao.BpelDAOConnectionFactoryJDBC;
import org.apache.ode.bpel.engine.BpelServerImpl;
import org.apache.ode.bpel.engine.CountLRUDehydrationPolicy;
import org.apache.ode.bpel.evt.BpelEvent;
import org.apache.ode.bpel.evt.CorrelationMatchEvent;
import org.apache.ode.bpel.evt.NewProcessInstanceEvent;
import org.apache.ode.bpel.evt.ProcessMessageExchangeEvent;
import org.apache.ode.bpel.iapi.BpelEventListener;
import org.apache.ode.bpel.iapi.Scheduler;
import org.apache.ode.bpel.memdao.BpelDAOConnectionFactoryImpl;
import org.apache.ode.dao.jpa.ProcessDAOImpl;
import org.apache.ode.il.config.OdeConfigProperties;
import org.apache.ode.il.dbutil.Database;
import org.apache.ode.scheduler.simple.JdbcDelegate;
import org.apache.ode.scheduler.simple.SimpleScheduler;
import org.apache.ode.utils.GUID;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.eclipse.core.runtime.FileLocator;



/**
 * Embedded ODE process server
 * 
 * @version $Rev$ $Date$
 */
public class EmbeddedODEServer {
    private static final String TUSCANY_IMPL_BPEL_DBLOCATION = "TUSCANY_IMPL_BPEL_DBLOCATION";
    
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
    
    private Map<String, Long> mexToProcessMap = new ConcurrentHashMap<String, Long>();
    
    private Map<Long, Map<String, EndpointReference>> callbackMap = new ConcurrentHashMap<Long, Map<String, EndpointReference>>();
    
    private final Lock metadataLock 		= new ReentrantLock();
    private final Condition mexAdded  		= metadataLock.newCondition(); 
    private final Condition callbackAdded  	= metadataLock.newCondition(); 
       
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
        
        // Start ODE scheduler
        _scheduler.start();
        
        __log.info("ODE BPEL server started.");
        _initialized = true;

    } // end method init()
    
    /**
     * Gets the location of the database used for the ODE BPEL engine as a File object for
     * the directory containing the database
     * @return
     * @throws ODEInitializationException
     * @throws URISyntaxException
     */
    private File getDatabaseLocationAsFile() throws ODEInitializationException, URISyntaxException {
        File locationFile 	= null;
        URL dbLocation		= null;

        // An environment variable to set the path to the DB
        String dbFile = System.getenv(TUSCANY_IMPL_BPEL_DBLOCATION);
        if( dbFile != null ) {
            try {
                locationFile = new File(dbFile).getParentFile();
            } catch (Exception e ) {
                System.out.println("Environment variable " + TUSCANY_IMPL_BPEL_DBLOCATION + " has the wrong format: " + dbFile);
                System.out.println("Exception is: " + e.getClass().toString() + " " + e.getMessage());
            } // end try
        } else {
            dbLocation = getClass().getClassLoader().getResource("jpadb");
            if (dbLocation == null) {
                throw new ODEInitializationException("Couldn't find database in the classpath:" + 
                " try setting the " + TUSCANY_IMPL_BPEL_DBLOCATION + " environment variable");
            }
            // Handle OSGI bundle case
            if( dbLocation.getProtocol() == "bundleresource" ) {
                try {
                    dbLocation = FileLocator.toFileURL( dbLocation );
                } catch (Exception ce ) {
                    throw new ODEInitializationException("Couldn't find database in the OSGi bundle");
                } // end try
            } // end if 
            locationFile = new File(dbLocation.toURI()).getParentFile();
        } // end if

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
        //_scheduler.setPolledRunnableProcesser(polledRunnableProcessor);

        _bpelServer.setDaoConnectionFactory(_daoCF);
        _bpelServer.setInMemDaoConnectionFactory(new BpelDAOConnectionFactoryImpl(_scheduler));
        
        _bpelServer.setEndpointReferenceContext( new ODEEprContext() );
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
    
        // Register event listener on the BPEL server
        _bpelServer.registerBpelEventListener( new ODEEventListener( this, _bpelServer) );
    } // end method initBpelLServer

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

    /**
     * Deploy the BPEL process implementation to the ODE Engine
     * @param d - ODEDeployment structure
     * @param implementation - the BPEL Implementation 
     * @param component - the SCA component which uses the implementation
     */
    public void deploy(ODEDeployment d, BPELImplementation implementation, RuntimeComponent component ) {
        try {
        	TuscanyProcessConfImpl processConf = new TuscanyProcessConfImpl( implementation, component );
        	_bpelServer.register(processConf);
        	d.setProcessConf(processConf);
        	__log.debug("Completed calling new Process deployment code...");
        } catch (Exception ex) {
            String errMsg = ">>> DEPLOY: Unexpected exception during deploy of BPEL. /n Component = " 
            	             + component.getName()
            	             + " implementation = "
            	             + implementation.getProcess()
            	             + ex.getMessage();
            __log.debug(errMsg, ex);
            throw new ODEDeploymentException(errMsg,ex);
        }
    }

    /**
     * Undeploy the BPEL process implementation from the ODE Engine
     * @param d - ODEDeployment structure
     */
    public void undeploy(ODEDeployment d) {
    	TuscanyProcessConfImpl processConf = d.getProcessConf();
    	if( processConf != null ) {
    		processConf.stop();
    	} // end if
    } // end method undeploy
    
    public void registerTuscanyRuntimeComponent(QName processName,RuntimeComponent componentContext) {
        tuscanyRuntimeComponents.put(processName, componentContext);
    }
        
    public RuntimeComponent getTuscanyRuntimeComponent(QName processName) {
        return tuscanyRuntimeComponents.get(processName);
    }
    
    /**
     * Records a connection between a MessageExchange ID and a Process Instance ID
     * @param mexID
     * @param processID
     */
    public void addMexToProcessIDLink( String mexID, Long processID ) {
    	//System.out.println("Add mapping Mex - ProcessID = " + mexID + " " + processID.toString());
    	if( mexID == null ) {
    		//System.out.println("Mex ID is null !");
    		return;
    	} // end if
    	metadataLock.lock();
    	try {
    		mexToProcessMap.put(mexID, processID);
    		mexAdded.signalAll();
    		return;
    	} catch (Exception e) {
    		return;
    	} finally {
    		metadataLock.unlock();
    	} // end try
    } // end method addMexToProcessIDLink( mexID, processID )
    
    /**
     * Connects from a MessageExchangeID to a Process Instance ID
     * @param mexID - the MessageExchange ID
     * @return - a Long which is the Process Instance ID
     */
    public Long getProcessIDFromMex( String mexID ) {
    	//System.out.println("Get mapping for Mex: " + mexID);
    	metadataLock.lock();
    	try {
    		Long processID = mexToProcessMap.get(mexID);
    		while( processID == null ) {
    			mexAdded.await();
    			processID = mexToProcessMap.get(mexID);
    		} // end while
    		return processID;
    	} catch (Exception e) {
    		return null;
    	} finally {
    		metadataLock.unlock();
    	} // end try

    } // end method getProcessIDFromMex
    
    /**
     * Remove the connection between a Message Exchange ID and a Process Instance ID
     * @param mexID - the Message Exchange ID
     */
    public void removeMexToProcessIDLink( String mexID ) {
    	mexToProcessMap.remove(mexID);
    } // end method removeMexToProcessIDLink
    
    /**
     * Stores the metadata for a Callback
     * @param processID - Process ID of the BPEL Process Instance for which this callback applies
     * @param serviceName - the name of the service which has the callback
     * @param callbackEndpoint - a Tuscany Endpoint which is the target of the callback
     */
    public void saveCallbackMetadata( Long processID, String serviceName, EndpointReference callbackEPR ) {
    	//System.out.println("Save callback metadata: ProcessID " + processID.toString() + " service: " + serviceName);
    	metadataLock.lock();
    	try {
	    	Map<String, EndpointReference> processMap = callbackMap.get(processID);
	    	if( processMap == null ) {
	    		processMap = new ConcurrentHashMap<String, EndpointReference>();
	    		callbackMap.put(processID, processMap);
	    	} // end if
	    	// Put the mapping of service name to callback endpoint - note that this overwrites any
	    	// previous mapping for the same service name
	    	processMap.put(serviceName, callbackEPR);
	    	callbackAdded.signalAll();
    	} finally {
    		metadataLock.unlock();
    	} // end try
    } // end saveCallbackMetadata
    
    /**
     * Get the metadata for a Callback, based on a BPEL Process Instance ID and a Service name
     * @param processID - the BPEL Process Instance ID
     * @param serviceName - the service name
     * @return - and Endpoint which is the Callback endpoint for the service for this process instance.
     * Returns null if there is no callback metadata for this service.
     */
    public EndpointReference getCallbackMetadata( Long processID, String serviceName ) {
    	EndpointReference theEPR;
    	//System.out.println("Get callback metadata: ProcessID " + processID.toString() + " service: " + serviceName);
    	
    	metadataLock.lock();
    	try {
    		while(true) {
		    	Map<String, EndpointReference> processMap = callbackMap.get(processID);
		    	theEPR = processMap.get(serviceName);
		    	if( theEPR != null ) return theEPR;
		    	callbackAdded.await();
    		} // end while
    	} catch (Exception e) {
    		return null;
    	} finally {
    		metadataLock.unlock();
    	} // end try
    } // end method getCallbackMetadata
    
    /**
     * Removes the metadata for a Callback
     * @param processID - the Process Instance ID of the process instance to which the callback metadata applies
     * @param serviceName - the service name for the service which has a callback - can be NULL, in which case ALL
     * callback metadata for the process instance is removed
     */
    public void removeCallbackMetadata( Long processID, String serviceName ) {
    	
    	if( serviceName == null ) {
    		callbackMap.remove(processID);
    	} else {
    		Map<String, EndpointReference> processMap = callbackMap.get(processID);
        	processMap.remove(serviceName);
    	} // end if
    	
    } // end method removeCallbackMetadata
    
    private class ODEEventListener implements BpelEventListener {
    	
    	private EmbeddedODEServer ODEServer;
    	private BpelServerImpl bpelServer;
    	
    	ODEEventListener( EmbeddedODEServer ODEServer, BpelServerImpl bpelServer ) {
    		this.ODEServer 			= ODEServer;
    		this.bpelServer 		= bpelServer;
    	} // end constructor

    	/**
    	 * Method which receives events from the ODE Engine as processing proceeds
    	 */
		public void onEvent(BpelEvent bpelEvent) {
			if( bpelEvent instanceof ProcessMessageExchangeEvent ||
			    bpelEvent instanceof NewProcessInstanceEvent ||
			    bpelEvent instanceof CorrelationMatchEvent ) {
				handleProcMexEvent( (ProcessMessageExchangeEvent) bpelEvent );
				return;
			} // end if
			
		} // end method onEvent
		
		/**
		 * Handle a ProcessMessageExchangeEvent
		 * - the important aspect of this event is that it establishes a connection between a MessageExchange object 
		 * and the BPEL Process instance to which it relates. 
		 * @param bpelEvent - the ProcessMessageExchangeEvent
		 */
		private void handleProcMexEvent( ProcessMessageExchangeEvent bpelEvent) {
			// Extract the message ID and the process instance ID - it is the connection between these
			// that is vital to know
			String mexID 	= bpelEvent.getMessageExchangeId();
			Long processID 	= bpelEvent.getProcessInstanceId();
			ODEServer.addMexToProcessIDLink( mexID, processID );
		} // end method handleProcMexEvent

		public void shutdown() {
			// Intentionally left blank		
		}

		public void startup(Properties configProperties) {
			// Intentionally left blank		
		}
    	
    } // end Class BPELEventListener
}
