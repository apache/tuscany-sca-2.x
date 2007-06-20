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

package org.apache.tuscany.sca.binding.feed.module;

import org.apache.tuscany.sca.binding.feed.DefaultFeedBindingFactory;
import org.apache.tuscany.sca.binding.feed.FeedBindingFactory;
import org.apache.tuscany.sca.binding.feed.provider.FeedBindingProviderFactory;
import org.apache.tuscany.sca.binding.feed.xml.AtomBindingProcessor;
import org.apache.tuscany.sca.binding.feed.xml.RssBindingProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

/**
 * A module activator for the Feed binding extension.
 */
public class FeedModuleActivator implements ModuleActivator {

    public Object[] getExtensionPoints() {
        // No extensionPoints being contributed here
        return null;
    }

    public void start(ExtensionPointRegistry registry) {

        // Create the Feed model factory
        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        FeedBindingFactory factory = new DefaultFeedBindingFactory();
        factories.addFactory(factory);

        // Add the AtomBindingProcessor and RSSBindingProcessor extensions
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        AtomBindingProcessor atomBindingProcessor = new AtomBindingProcessor(factory);
        processors.addArtifactProcessor(atomBindingProcessor);
        RssBindingProcessor rssBindingProcessor = new RssBindingProcessor(factory);
        processors.addArtifactProcessor(rssBindingProcessor);

        // Add the Feed binding provider factory extension
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        ServletHost servletHost = registry.getExtensionPoint(ServletHost.class);
        MessageFactory messageFactory = factories.getFactory(MessageFactory.class);
        providerFactories.addProviderFactory(new FeedBindingProviderFactory(servletHost, messageFactory));
    }

    public void stop(ExtensionPointRegistry registry) {
    }
}
