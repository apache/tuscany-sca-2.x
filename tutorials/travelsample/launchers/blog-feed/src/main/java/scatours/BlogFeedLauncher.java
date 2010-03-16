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

package scatours;

import static scatours.launcher.LauncherUtil.locate;

import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

public class BlogFeedLauncher {

    public static void main(String[] args) throws Exception {
        SCAContribution feedContribution = locate("blog-feed");

        SCANode node = SCANodeFactory.newInstance().createSCANode("blog-feed.composite", feedContribution);
        node.start();

        System.out.println("Node started - Press enter to shutdown.");
        System.out.println();
        System.out.println("To view the blog feed, use your Web browser to view:");
        System.out.println("  Feeds using Tuscany Data APIs");
        System.out.println("    http://localhost:8090/BlogAtom");
        System.out.println("    http://localhost:8090/BlogRSS");
        System.out.println("  Feeds using Atom APIs");
        System.out.println("    http://localhost:8090/BlogAtomAPIs");
        System.out.println("  Feeds using RSS APIs");
        System.out.println("    http://localhost:8090/BlogRSSAPIs");
        System.out.println();
        System.in.read();
        node.stop();
    }
}
