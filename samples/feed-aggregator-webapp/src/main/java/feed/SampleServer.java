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
package feed;

import java.io.IOException;

import org.apache.tuscany.sca.host.embedded.SCADomain;

public class SampleServer {
    public static void main(String[] args) throws Exception {

        SCADomain scaDomain = SCADomain.newInstance("FeedAggregator.composite");

        try {
            System.out.println("Sample Feed server started (press enter to shutdown)");
            System.out.println();
            System.out.println("To read the aggregated feeds, point your Web browser to the following addresses:");
            System.out.println("http://localhost:8083/atomAggregator");
            System.out.println("http://localhost:8083/atomAggregator/atomsvc (for the Atom service document)");
            System.out.println("http://localhost:8083/rssAggregator");
            System.out.println("http://localhost:8083/atomAggregator?feedType=rss_2.0");
            System.out.println("http://localhost:8083/rssAggregator?feedType=atom_1.0");
            System.out.println();
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // RssFeed feedService = scaDomain.getService(RssFeed.class,
        // "RssAggregatorComponent");
        // SyndFeed syndFeed = feedService.get();
        // SyndFeedOutput output = new SyndFeedOutput();
        // output.output(syndFeed,new PrintWriter(System.out));

        scaDomain.close();
        System.out.println("Sample Feed server stopped");
    }
}
