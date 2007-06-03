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
package org.apache.tuscany.sca.binding.feed.provider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Invoker for the Feed binding.
 */
public class FeedBindingInvoker implements Invoker {

    private String uri;

    public FeedBindingInvoker(String uri, String feedType) {
        this.uri = uri;
    }

    public Message invoke(Message msg) {
        try {
            URL feedUrl = new URL(uri);

            // Read the configured feed into a Feed object
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));
            msg.setBody(feed);

            System.out.println(">>> FeedBindingInvoker (" + feed.getFeedType() + ") " + uri);
            
        } catch (MalformedURLException e) {
            msg.setFaultBody(e);
        } catch (IllegalArgumentException e) {
            msg.setFaultBody(e);
        } catch (FeedException e) {
            msg.setFaultBody(e);
        } catch (IOException e) {
            msg.setFaultBody(e);
        }
        return msg;
    }

}
