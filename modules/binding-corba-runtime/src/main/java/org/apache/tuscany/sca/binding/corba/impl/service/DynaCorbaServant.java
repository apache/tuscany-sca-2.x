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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.corba.impl.exceptions.RequestConfigurationException;
import org.apache.tuscany.sca.binding.corba.impl.types.TypeTree;
import org.apache.tuscany.sca.binding.corba.impl.types.TypeTreeCreator;
import org.apache.tuscany.sca.binding.corba.impl.types.util.TypeHelpersProxy;
import org.apache.tuscany.sca.binding.corba.impl.types.util.Utils;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

/**
 * General servant which provides target component implementation via CORBA
 */
public class DynaCorbaServant extends ObjectImpl implements InvokeHandler {

	private static String[] DEFAULT_IDS = { "IDL:default:1.0" };
	private RuntimeComponentService service;
	private Binding binding;
	private String[] ids = DEFAULT_IDS;

	public DynaCorbaServant(RuntimeComponentService service, Binding binding) {
		this.service = service;
		this.binding = binding;
	}

	public void setIds(String[] ids) {
		if (ids != null) {
			this.ids = ids;
		} else {
			this.ids = DEFAULT_IDS;
		}
	}

	public OutputStream _invoke(String method, InputStream in,
			ResponseHandler rh) {

		DataType outputType = null;
		DataType<List<DataType>> inputType = null;
		Operation operation = null;

		List<Operation> operations = service.getInterfaceContract()
				.getInterface().getOperations();
		// searching for proper operation
		for (Operation oper : operations) {
			if (oper.getName().equals(method)) {
				outputType = oper.getOutputType();
				inputType = oper.getInputType();
				operation = oper;
				break;
			}
		}
		if (operation == null) {
			// operation wasn't found
			throw new org.omg.CORBA.BAD_OPERATION(0,
					org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
		} else {
			List<Object> inputInstances = new ArrayList<Object>();
			try {
				// retrieving in arguments
				for (DataType type : inputType.getLogical()) {
					Class<?> forClass = type.getPhysical();
					TypeTree tree = TypeTreeCreator.createTypeTree(forClass);
					Object o = TypeHelpersProxy.read(tree.getRootNode(), in);
					inputInstances.add(o);

				}
			} catch (RequestConfigurationException e) {
				// TODO: raise remote exception, BAD_PARAM exception maybe?
				e.printStackTrace();
			}
			try {
				// invocation and sending result
				Object result = service.getRuntimeWire(binding).invoke(
						operation, inputInstances.toArray());
				if (outputType != null) {
					OutputStream out = rh.createReply();
					TypeTree tree = TypeTreeCreator.createTypeTree(outputType
							.getPhysical());
					TypeHelpersProxy.write(tree.getRootNode(), out, result);
					return out;
				}
			} catch (InvocationTargetException ie) {
				// handling user exception
				try {
					OutputStream out = rh.createExceptionReply();
					Class<?> exceptionClass = ie.getTargetException()
							.getClass();
					TypeTree tree = TypeTreeCreator
							.createTypeTree(exceptionClass);
					String exceptionId = Utils.getExceptionId(exceptionClass);
					out.write_string(exceptionId);
					TypeHelpersProxy.write(tree.getRootNode(), out, ie
							.getTargetException());
					return out;
				} catch (RequestConfigurationException e) {
					// TODO: raise remote exception - exception while handling
					// target exception
					e.printStackTrace();
				}
			} catch (Exception e) {
				// TODO: raise remote exception
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String[] _ids() {
		return ids;
	}

}
