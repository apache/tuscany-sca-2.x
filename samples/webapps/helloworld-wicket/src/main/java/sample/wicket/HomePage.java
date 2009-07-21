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
package sample.wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.oasisopen.sca.annotation.Reference;

/**
 * Everybody's favorite example (Hello World), modified to use Guice.
 * 
 * @author Alastair Maw
 */
public class HomePage extends WebPage
{
    @Reference
    IMyService service;

    private String labelValue = "<not yet initialized>";

    /**
     * Constructor
     */
    public HomePage()
    {
        add(new Link("link")
        {
            /**
             * @see org.apache.wicket.markup.html.link.Link#onClick()
             */
            @Override
            public void onClick()
            {
                labelValue = service.getHelloWorldText();
            }
        });
        add(new Label("message", new AbstractReadOnlyModel<String>()
        {

            @Override
            public String getObject()
            {
                return labelValue;
            }

        }));
    }
}