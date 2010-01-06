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

package org.apache.tuscany.sca.domain.node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DomainNodeMain {

    /**
     * Start an SCA domain node
     * @param args a list of contribution jars for the node to run
     */
    public static void main(String[] args) throws Exception {

        String configURI = "vm://defaultDoamin";

        List<String> contributions = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("vm:") || args[i].startsWith("tribes:") || args[i].startsWith("tuscany:")) {
                configURI = args[i];
            } else{
                File f = new File(args[i]);
                if (!f.exists()) {
                    System.err.println("contribution not found: " + f);
                    System.exit(1);
                }
                contributions.add(f.toURI().toString());
            }
        }

        DomainNode node = new DomainNode(configURI, contributions.toArray(new String[contributions.size()]));

        System.out.println("Hit enter to stop node...");
        if (System.in.read() == -1) {
            // no sysin so wait for ever letting caller do the terminate
            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }
        }

        node.stop();
    }
}
