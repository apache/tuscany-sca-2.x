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
package org.apache.tuscany.sca.databinding.saxon.module;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.saxon.DataObject2NodeInfoTransformer;
import org.apache.tuscany.sca.databinding.saxon.Node2NodeInfoTransformer;
import org.apache.tuscany.sca.databinding.saxon.NodeInfo2DataObjectTransformer;
import org.apache.tuscany.sca.databinding.saxon.NodeInfo2NodeTransformer;
import org.apache.tuscany.sca.databinding.saxon.Object2ValueTransformer;
import org.apache.tuscany.sca.databinding.saxon.SaxonNodeDataBinding;
import org.apache.tuscany.sca.databinding.saxon.SaxonValueDataBinding;
import org.apache.tuscany.sca.databinding.saxon.SimpleType2ValueTransformer;
import org.apache.tuscany.sca.databinding.saxon.Value2ObjectTransformer;
import org.apache.tuscany.sca.databinding.saxon.Value2SimpleTypeTransformer;

/**
 * This class activates the value and node info data bindings as well as several transformers
 * @version $Rev$ $Date$
 */
public class SaxonDataBindingModuleActivator implements ModuleActivator {

	public Object[] getExtensionPoints() {
		return null;
	}

	public void start(ExtensionPointRegistry registry) {
        
        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        dataBindings.addDataBinding(new SaxonNodeDataBinding());
        dataBindings.addDataBinding(new SaxonValueDataBinding());
        
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);
        Node2NodeInfoTransformer node2NodeInfoTransformer = new Node2NodeInfoTransformer();
        transformers.addTransformer(node2NodeInfoTransformer);
        NodeInfo2NodeTransformer nodeInfo2NodeTransformer = new NodeInfo2NodeTransformer();
        transformers.addTransformer(nodeInfo2NodeTransformer);
        transformers.addTransformer(new Object2ValueTransformer());
        transformers.addTransformer(new Value2ObjectTransformer());
        transformers.addTransformer(new SimpleType2ValueTransformer());
        transformers.addTransformer(new Value2SimpleTypeTransformer());
        transformers.addTransformer(new NodeInfo2DataObjectTransformer(nodeInfo2NodeTransformer));
        transformers.addTransformer(new DataObject2NodeInfoTransformer(node2NodeInfoTransformer));
	}

	public void stop(ExtensionPointRegistry registry) {
		// TODO Auto-generated method stub

	}

}
