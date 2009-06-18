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

package org.apache.tuscany.sca.node.equinox.launcher;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * An Equinox console command extension for Tuscany
 * http://www.ibm.com/developerworks/library/os-ecl-osgiconsole/index.html
 */
public class NodeLauncherCommand implements CommandProvider, BundleActivator {

    public String getHelp() {
        return "---Apache Tuscany Commands for Equinox---\n"
            + "\ttuscany - Launch an SCA node\n"
            + "\tAgruments:\n"
            // + " [-config <equinoxConfiguration>]: The configuration folder for Equinox\n"
            // + " [-bundles <osgiBundles>]: A list of bundles to be installed\n"
            + "\t[-c <compositeURI>]: The composite URI\n"
            + "\t[-t <ttl>]: Time to live in milliseconds before the node is stopped\n"
            + "\tcontribution1 ... contributionN: A list of contribution files or URLs\n";
    }

    public void _tuscany(CommandInterpreter ci) throws Exception {
        List<String> args = new ArrayList<String>();
        while (true) {
            String arg = ci.nextArgument();
            if (arg == null) {
                break;
            }
            args.add(arg);
        }
        NodeLauncher.main(args.toArray(new String[args.size()]));
    }

    public void start(BundleContext context) throws Exception {
        context.registerService(CommandProvider.class.getName(), this, new Hashtable());
    }

    public void stop(BundleContext context) throws Exception {
    }

}
