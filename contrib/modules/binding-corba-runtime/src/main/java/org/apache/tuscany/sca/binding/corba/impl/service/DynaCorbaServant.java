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

package org.apache.tuscany.sca.binding.corba.impl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.binding.corba.impl.exceptions.RequestConfigurationException;
import org.apache.tuscany.sca.binding.corba.impl.types.TypeTree;
import org.apache.tuscany.sca.binding.corba.impl.types.TypeTreeCreator;
import org.apache.tuscany.sca.binding.corba.impl.types.util.TypeHelpersProxy;
import org.apache.tuscany.sca.binding.corba.impl.types.util.Utils;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

/**
 * @version $Rev$ $Date$
 * General servant which provides target component implementation via CORBA
 */
public class DynaCorbaServant extends ObjectImpl implements InvokeHandler {

    private static final Logger logger = Logger.getLogger(DynaCorbaServant.class.getName());
    
    private String[] ids;
    private InvocationProxy invocationProxy;
    private String typeId;
    
    /**
     * Creates servant object
     * @param invocationProxy
     * @param typeId
     * @throws RequestConfigurationException
     */
    public DynaCorbaServant(InvocationProxy invocationProxy, String typeId) throws RequestConfigurationException {
        this.invocationProxy = invocationProxy;
        this.typeId = typeId;
        setDefaultIds();
    }

    /**
     * Sets CORBA object ID
     * @param ids
     */
    public void setIds(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == null || ids[i].length() == 0) {
                // if invalid id was passed then set to default 
                setDefaultIds();
                return;
            }
        }
        this.ids = ids;
    }

    public OutputStream _invoke(String operationName, InputStream in, ResponseHandler rh) {
        OperationTypes types = invocationProxy.getOperationTypes(operationName);
        if (types == null) {
            // operation wasn't found
            throw new org.omg.CORBA.BAD_OPERATION(0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
        } else {
            List<Object> inputInstances = new ArrayList<Object>();
            try {
                // retrieving in arguments
                for (TypeTree tree : types.getInputType()) {
                    Object o = TypeHelpersProxy.read(tree.getRootNode(), in);
                    inputInstances.add(o);
                }
            } catch (MARSHAL e) {
                // parameter passed by user was not compatible with Java to
                // Corba mapping
                throw new org.omg.CORBA.BAD_PARAM(0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
            }
            try {
                // invocation and sending result
                Object result = invocationProxy.invoke(operationName, inputInstances);
                OutputStream out = rh.createReply();
                if (types.getOutputType() != null) {
                    TypeTree tree = types.getOutputType();
                    TypeHelpersProxy.write(tree.getRootNode(), out, result);
                }
                return out;
            } catch (InvocationException ie) {
                // handling user exception
                try {
                    OutputStream out = rh.createExceptionReply();
                    Class<?> exceptionClass = ie.getTargetException().getClass();
                    TypeTree tree = TypeTreeCreator.createTypeTree(exceptionClass, null);
                    String exceptionId = Utils.getTypeId(exceptionClass);
                    out.write_string(exceptionId);
                    TypeHelpersProxy.write(tree.getRootNode(), out, ie.getTargetException());
                    return out;
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Exception during handling invocation exception", e);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Unexpected exception during sending CORBA result to client", e);
            }
        }
        return null;
    }

    @Override
    public String[] _ids() {
        return ids;
    }

    /**
     * Sets servant ID to default, based on Java class name
     */
    private void setDefaultIds() {
        this.ids = new String[] {typeId};
    }

}
