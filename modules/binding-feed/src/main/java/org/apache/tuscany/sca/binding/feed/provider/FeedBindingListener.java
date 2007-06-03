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
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.sca.binding.feed.Feed;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * A Feed binding listener, implemented as a servlet and register in a
 * servlet host provided by the SCA hosting runtime.
 */
public class FeedBindingListener extends HttpServlet {
    private static final long serialVersionUID = 1L;

    String serviceName;
    Class<?> serviceInterface;
    Object serviceInstance;
    String feedType;

    public FeedBindingListener(String serviceName, Class<?> serviceInterface, Object serviceInstance, String feedType) {
        this.serviceName = serviceName;
        this.serviceInterface = serviceInterface;
        this.serviceInstance = serviceInstance;
        this.feedType = feedType;
    }

    @Override
    public void init(ServletConfig config) {
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // The feedType parameter is used to override what type of feed is going to
        // be produced
        String requestFeedType = request.getParameter("feedType");
        if (requestFeedType == null)
            requestFeedType = feedType;

        System.out.println(">>> FeedEndPointServlet (" + requestFeedType + ") " + request.getRequestURI());

        // Assuming that the service provided by this binding implements the Feed
        // service interface, get the Feed from the service
        SyndFeed syndFeed = ((Feed)serviceInstance).get();
        syndFeed.setFeedType(requestFeedType);

        // Write the Feed to the servlet output
        OutputStream output = response.getOutputStream();
        SyndFeedOutput syndOutput = new SyndFeedOutput();
        try {
            syndOutput.output(syndFeed, new PrintWriter(output));
        } catch (FeedException e) {
            throw new ServletException(e);
        }

        output.flush();
        output.close();
    }
}
