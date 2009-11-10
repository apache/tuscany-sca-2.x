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

package org.apache.tuscany.sca.endpoint.zookeeper;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.zookeeper.server.NIOServerCnxn;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;

/**
 * This class starts and runs a standalone ZooKeeperServer.
 */
public class LocalZooKeeperServer {
    private static final Logger logger = Logger.getLogger(LocalZooKeeperServer.class.getName());
    private static final String USAGE = "Usage: LocalZooKeeperServer configfile | port datadir [ticktime] [maxcnxns]";
    private NIOServerCnxn.Factory cnxnFactory;
    private ZooKeeperServer zkServer;

    /**
     * 
     */
    public LocalZooKeeperServer() {
        super();
    }

    public Thread folk(final ServerConfig config) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    LocalZooKeeperServer.this.run(config);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
        thread.start();
        return thread;
    }

    public Thread folk(final String[] args) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    LocalZooKeeperServer.this.run(args);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        };
        thread.start();
        return thread;
    }

    /**
     * Run from a ServerConfig.
     * @param config ServerConfig to use.
     * @throws IOException
     */
    public void run(ServerConfig config) throws IOException {
        logger.info("Starting ZooKeeper server");
        try {
            // Note that this thread isn't going to be doing anything else,
            // so rather than spawning another thread, we will just call
            // run() in this thread.
            // create a file logger url from the command line args
            zkServer = new ZooKeeperServer();

            FileTxnSnapLog ftxn = new FileTxnSnapLog(new File(config.getDataLogDir()), new File(config.getDataDir()));
            zkServer.setTxnLogFactory(ftxn);
            zkServer.setTickTime(config.getTickTime());
            cnxnFactory = new NIOServerCnxn.Factory(config.getClientPort(), config.getMaxClientCnxns());
            cnxnFactory.startup(zkServer);
            cnxnFactory.join();
            if (zkServer.isRunning()) {
                zkServer.shutdown();
            }
        } catch (InterruptedException e) {
            // warn, but generally this is ok
            logger.log(Level.WARNING, "Server interrupted", e);
        }
    }

    public void run(String[] args) throws ConfigException, IOException {
        ServerConfig config = new ServerConfig();
        if (args.length == 1) {
            config.parse(args[0]);
        } else {
            config.parse(args);
        }
        run(config);
    }

    /**
     * Shutdown the serving instance
     */
    public void shutdown() {
        cnxnFactory.shutdown();
    }

    /*
     * Start up the ZooKeeper server.
     *
     * @param args the configfile or the port datadir [ticktime]
     */
    public static void main(String[] args) {
        LocalZooKeeperServer main = new LocalZooKeeperServer();
        try {
            main.run(args);
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Invalid arguments, exiting abnormally", e);
            logger.info(USAGE);
            System.err.println(USAGE);
            System.exit(2);
        } catch (ConfigException e) {
            logger.log(Level.SEVERE, "Invalid config, exiting abnormally", e);
            System.err.println("Invalid config, exiting abnormally");
            System.exit(2);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected exception, exiting abnormally", e);
            System.exit(1);
        }
        logger.info("Exiting normally");
        System.exit(0);
    }
}
