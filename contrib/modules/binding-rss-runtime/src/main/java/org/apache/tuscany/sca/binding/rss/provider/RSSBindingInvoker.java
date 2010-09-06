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
package org.apache.tuscany.sca.binding.rss.provider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.tuscany.sca.invocation.DataExchangeSemantics;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Invoker for the RSS binding.
 *
 * @version $Rev$ $Date$
 */
class RSSBindingInvoker implements Invoker , DataExchangeSemantics {
    private static final Logger logger = Logger.getLogger(RSSBindingInvoker.class.getName());
    
    private String uri;

    RSSBindingInvoker(String uri, String feedType) {
        this.uri = uri;
    }

    public Message invoke(Message msg) {
        try {
            logger.fine("invoke " + uri);

            // Read an RSS feed into a Synd feed
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new URL(uri)));
            
            //FIXME Support conversion to data-api entries
            
            msg.setBody(feed);

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

    public boolean allowsPassByReference() {
        return true;
    }
}
