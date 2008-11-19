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
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

/**
 * @version $Rev$ $Date$
 */
public class StructTypeHelper implements TypeHelper {

    private static final Logger logger = Logger.getLogger(StructTypeHelper.class.getName());
    
    public Object read(TypeTreeNode node, InputStream is) {
        TypeTreeNode[] children = node.getChildren();
        Object result = null;
        if (children != null) {
            try {
                result = node.getJavaClass().newInstance();
                for (int i = 0; i < children.length; i++) {
                    Object childResult = TypeHelpersProxy.read(children[i], is);
                    Field childField = result.getClass().getField(children[i].getName());
                    childField.set(result, childResult);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception during reading CORBA struct data", e);
            }
        }
        return result;
    }

    public void write(TypeTreeNode node, OutputStream os, Object data) {
        TypeTreeNode[] children = node.getChildren();
        if (children != null) {
            try {
                for (int i = 0; i < children.length; i++) {
                    Field childField = node.getJavaClass().getField(children[i].getName());
                    TypeHelpersProxy.write(children[i], os, childField.get(data));
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception during writing CORBA struct data", e);
            }
        }
    }

}
