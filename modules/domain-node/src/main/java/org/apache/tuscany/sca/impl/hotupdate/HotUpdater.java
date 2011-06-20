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

package org.apache.tuscany.sca.impl.hotupdate;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.impl.NodeImpl;

/**
 * Code to do dynamic updates to a running Node.
 * Very experimental presently, mainly just to see what type of things are required 
 */
public class HotUpdater {

    private final Node node;
    private final File domainDir;

    // key is contribution URI (which for exploded contributions is the directory name)
    private final Map<String, LastModifiedTracker> contributions = new HashMap<String, LastModifiedTracker>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public HotUpdater(Node node, File domainDir){
        this.node = node;
        this.domainDir = domainDir;
        
        final Runnable checker = new Runnable() {
            public void run() {
                check();
            }
        };

        scheduler.scheduleAtFixedRate(checker, 10, 10, TimeUnit.SECONDS);
    }

    private void check() {
        Set<String> found = new HashSet<String>();
        for (File f : domainDir.listFiles()) {
            if (f.isDirectory() && !f.getName().startsWith(".")) {
                found.add(f.getName());
                LastModifiedTracker scanner = contributions.get(f.getName());
                if (scanner == null) {
                    //newContribution(f);
                    contributions.put(f.getName(), new LastModifiedTracker(f));
                } else {
                    if (scanner.checkModified()) {
                        updatedContribution(f);
                    }
                }
            }
        }
        
        HashSet<String> removed = new HashSet<String>(contributions.keySet());
        removed.removeAll(found);
        for (String curi : removed) {
            removedContribution(curi);
        }

    }

    private void removedContribution(String curi) {
        try {
            ((NodeImpl)node).uninstallContribution(curi, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        contributions.remove(curi);
    }

    private void updatedContribution(File f) {
        try {
            ((NodeImpl)node).updateContribution(f.getName(), f.toURI().toURL().toString(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newContribution(File f) {
        try {
            node.installContribution(f.getName(), f.toURI().toURL().toString(), null, null);
            node.startDeployables(f.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
