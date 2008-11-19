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

package org.apache.tuscany.sca.binding.corba.impl.types.util;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.binding.corba.impl.types.TypeTreeNode;
import org.apache.tuscany.sca.binding.corba.impl.types.UnionAttributes;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

/**
 * @version $Rev$ $Date$
 */
public class UnionTypeHelper implements TypeHelper {

    private static final Logger logger = Logger.getLogger(UnionTypeHelper.class.getName());
    
    public Object read(TypeTreeNode node, InputStream is) {
        Object result = null;
        try {
            int discriminator = is.read_long();
            UnionAttributes attrs = (UnionAttributes)node.getAttributes();
            String childName = attrs.getOptionsMapping().get(discriminator);
            if (childName == null) {
                // get default if option numbers field not found
                childName = attrs.getDefaultOptionName();
            }
            result = node.getJavaClass().newInstance();
            Field discField = result.getClass().getDeclaredField(attrs.getDiscriminatorName());
            discField.setAccessible(true);
            discField.set(result, discriminator);
            for (int i = 0; i < node.getChildren().length; i++) {
                if (node.getChildren()[i].getName().equals(childName)) {
                    Object unionValue = TypeHelpersProxy.read(node.getChildren()[i], is);
                    Field childField = result.getClass().getDeclaredField(childName);
                    childField.setAccessible(true);
                    childField.set(result, unionValue);
                    break;
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception during reading CORBA union data", e);
        }
        return result;
    }

    public void write(TypeTreeNode node, OutputStream os, Object data) {
        try {
            UnionAttributes attrs = (UnionAttributes)node.getAttributes();
            Field discriminatorField = data.getClass().getDeclaredField(attrs.getDiscriminatorName());
            discriminatorField.setAccessible(true);
            int discriminator = discriminatorField.getInt(data);
            os.write_long(discriminator);
            String childName = attrs.getOptionsMapping().get(discriminator);
            if (childName == null) {
                // get default if option numbers field not found
                childName = attrs.getDefaultOptionName();
            }
            for (int i = 0; i < node.getChildren().length; i++) {
                if (node.getChildren()[i].getName().equals(childName)) {
                    Field childField = data.getClass().getDeclaredField(childName);
                    childField.setAccessible(true);
                    TypeHelpersProxy.write(node.getChildren()[i], os, childField.get(data));
                    break;
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception during writing CORBA union data", e);
        }
    }

}
