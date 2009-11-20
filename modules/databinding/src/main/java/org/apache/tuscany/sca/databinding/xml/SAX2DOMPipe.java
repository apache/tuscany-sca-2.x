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
package org.apache.tuscany.sca.databinding.xml;

import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.DataPipe;
import org.apache.tuscany.sca.databinding.DataPipeTransformer;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class SAX2DOMPipe extends BaseTransformer<ContentHandler, Node> implements DataPipeTransformer<ContentHandler, Node> {
    private DOMHelper domHelper;
    /**
     * 
     */
    public SAX2DOMPipe(ExtensionPointRegistry registry) {
        super();
        domHelper = DOMHelper.getInstance(registry);
    }

    /**
     * @see org.apache.tuscany.sca.databinding.DataPipeTransformer#newInstance()
     */
    public DataPipe<ContentHandler, Node> newInstance() {
        return new Pipe();
    }

    @Override
    protected Class<Node> getTargetType() {
        return Node.class;
    }

    @Override
    protected Class<ContentHandler> getSourceType() {
        return ContentHandler.class;
    }

    @Override
    public int getWeight() {
        return 30;
    }

    private class Pipe implements DataPipe<ContentHandler, Node> {
        private DOMHelper.NodeContentHandler pipe;

        public Pipe() {
            super();
            this.pipe = domHelper.createContentHandler(null);
        }

        public Node getResult() {
            return pipe.getNode();
        }

        public ContentHandler getSink() {
            return pipe;
        }

    }
}
