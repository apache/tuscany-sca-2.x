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
package org.apache.tuscany.sca.databinding.saxon;

import java.lang.reflect.Array;

import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.Value;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.saxon.collection.ItemList;

import commonj.sdo.DataObject;

/**
 * Transforms a NodeInfo object to a DataObject and return it in an array.
 * 
 * @version $Rev: 659284 $ $Date: 2008-05-22 14:26:18 -0800 (Thu, 22 May 2008) $
 */
public class NodeInfo2JavaArrayTransformer extends
		BaseTransformer<NodeInfo, Object[]> implements
		PullTransformer<NodeInfo, Object[]> {

	private NodeInfo2DataObjectTransformer nodeInfo2DataObjectTransformer = new NodeInfo2DataObjectTransformer();

	public NodeInfo2JavaArrayTransformer() {}

	@Override
	protected Class<NodeInfo> getSourceType() {
		return NodeInfo.class;
	}

	@Override
	protected Class<Object[]> getTargetType() {
		return Object[].class;
	}

	@Override
	public int getWeight() {
		return 30 + nodeInfo2DataObjectTransformer.getWeight();
	}

	public Object[] transform(NodeInfo source, TransformationContext context) {
		Object object = nodeInfo2DataObjectTransformer.transform(source, context);
		
		Class<?> componentClass = context.getTargetDataType().getPhysical().getComponentType();
		Object[] result = (Object[]) Array.newInstance(componentClass, 1);
		
		result[0] = object;
		
		return result;

	}

}
