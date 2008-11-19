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

/**
 * Transforms each object contained in the ItemList object.
 * 
 * 	If the object type is the same as the target type, it is not transformed.
 * 
 * 	If the object is a NodeInfo, it's transformed to a DataObject.
 * 
 * 	If the object is a Value, it's transformed to a Java object.
 * 
 * If the target type is not an array, the first transformed object is returned.
 * 
 * 	If the first transformed object type is not a target type, then a
 * 	TransformException is thrown
 * 
 * If the target type is an array, a array of this type is created containing
 * all transformed objects and it is returned
 * 
 * 	If any transformed object is not a target type, then a TransformException is
 * 	thrown
 * 
 * @version $Rev: 659284 $ $Date: 2008-05-22 14:26:18 -0800 (Thu, 22 May 2008) $
 */
public class ItemList2ObjectTransformer extends BaseTransformer<ItemList, Object> implements
    PullTransformer<ItemList, Object> {

    private NodeInfo2DataObjectTransformer nodeInfo2DataObjectTransformer = new NodeInfo2DataObjectTransformer();

    public ItemList2ObjectTransformer() {
    }

    @Override
    protected Class<ItemList> getSourceType() {
        return ItemList.class;
    }

    @Override
    protected Class<Object> getTargetType() {
        return Object.class;
    }

    @Override
    public int getWeight() {
        return 30 + nodeInfo2DataObjectTransformer.getWeight();
    }

    public Object transform(ItemList source, TransformationContext context) {

        if (source.size() == 0) {
            return null;
        }

        Class<?> targetType = context.getTargetDataType().getPhysical();

        if (targetType.isArray()) {
            int i = 0;
            Class<?> componentClass = targetType.getComponentType();
            Object[] result = (Object[])Array.newInstance(componentClass, source.size());

            try {

                if (componentClass.isAssignableFrom(NodeInfo.class) || componentClass.isAssignableFrom(Value.class)) {

                    for (Item item : source) {
                        result[i++] = item;
                    }

                } else {

                    for (Item item : source) {

                        if (item instanceof NodeInfo) {
                            result[i] = nodeInfo2DataObjectTransformer.transform((NodeInfo)item, context);

                        } else if (item instanceof Value) {
                            result[i] = Value.convert(item);

                        } else {
                            result[i] = item;
                        }

                        i++;

                    }

                }

            } catch (ArrayStoreException ex) {
                throw new TransformationException(ex);

            } catch (XPathException ex) {
                throw new TransformationException(ex);
            }

            return result;

        } else {
            Item item = source.iterator().next();

            if (item.getClass().isAssignableFrom(targetType)) {
                return item;

            } else if (item instanceof NodeInfo) {
                return nodeInfo2DataObjectTransformer.transform((NodeInfo)item, context);

            } else {

                try {
                    return Value.convert(item);

                } catch (XPathException e) {
                    throw new TransformationException(e);
                }

            }

        }

    }

}
