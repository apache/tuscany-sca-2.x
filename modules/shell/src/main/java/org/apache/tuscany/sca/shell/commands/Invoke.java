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

package org.apache.tuscany.sca.shell.commands;

import static java.lang.System.out;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import jline.Completor;
import jline.NullCompletor;

import org.apache.tuscany.sca.shell.Command;
import org.apache.tuscany.sca.shell.Shell;
import org.apache.tuscany.sca.shell.jline.ServiceCompletor;
import org.apache.tuscany.sca.shell.jline.ServiceOperationCompletor;

public class Invoke implements Command {

    private Shell shell;
    
    public Invoke(Shell shell) {
        this.shell = shell;
    }

    @Override
    public String getName() {
        return "invoke";
    }

    @Override
    public String getShortHelp() {
        return "invoke <component>[/<service>] <operation> [<arg0> <arg1> ...]";
    }

    @Override
    public String getHelp() {
        StringBuilder helpText = new StringBuilder();
        helpText.append("   Invokes an operation of a component service.\n");
        helpText.append("   (presently parameters are limited to simple types)\n");
        helpText.append("\n");
        helpText.append("   Arguments:\n");
        helpText.append("      component - (required) the name of the component\n");
        helpText.append("      service   - (optional) the name of the component service, which may be omitted\n");
        helpText.append("                             when the component has a single service.\n");
        helpText.append("      operation - (required) the name of the operation\n");
        helpText.append("      args      - (optional) the operation arguments\n");
        return helpText.toString();
    }

    @Override
    public Completor[] getCompletors() {
        return new Completor[]{new ServiceCompletor(shell), new ServiceOperationCompletor(shell), new NullCompletor()};
    }

    @Override
    public boolean invoke(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Wrong number of args");
            System.err.println(getShortHelp());
            return true;
        }
        String endpointName = args[0];
        String operationName = args[1];
        String params[] = new String[args.length - 2];
        System.arraycopy(args, 2, params, 0, params.length);
        Object proxy = shell.getNode().getService(null, endpointName);
        invoke(proxy, operationName, params);
        return true;
    }

    static void invoke(Object proxy, String operationName, String... params) throws IllegalAccessException, InvocationTargetException {
        Method foundMethod = null;
        for (Method m : proxy.getClass().getMethods()) {
            if (m.getName().equals(operationName)) {
                if (m.getParameterTypes().length == params.length) {
                    Object parameters[] = new Object[params.length];
                    int i = 0;
                    for (Class<?> type : m.getParameterTypes()) {
                        if (type == byte.class || type == Byte.class) {
                            parameters[i] = Byte.valueOf(params[i]);
                        } else if (type == char.class || type == Character.class) {
                            parameters[i] = params[i].charAt(0);
                        } else if (type == boolean.class || type == Boolean.class) {
                            parameters[i] = Boolean.valueOf(params[i]);
                        } else if (type == short.class || type == Short.class) {
                            parameters[i] = Short.valueOf(params[i]);
                        } else if (type == int.class || type == Integer.class) {
                            parameters[i] = Integer.valueOf(params[i]);
                        } else if (type == long.class || type == Long.class) {
                            parameters[i] = Long.valueOf(params[i]);
                        } else if (type == float.class || type == Float.class) {
                            parameters[i] = Float.valueOf(params[i]);
                        } else if (type == double.class || type == Double.class) {
                            parameters[i] = Double.valueOf(params[i]);
                        } else if (type == String.class) {
                            parameters[i] = params[i];
                        } else {
                            throw new IllegalArgumentException("Parameter type is not supported: " + type);
                        }
                        i++;
                    }
                    Object result = m.invoke(proxy, parameters);

                    if (result != null && result.getClass().isArray()) {
                        out.println(Arrays.toString((Object[])result));
                    } else {
                        out.println(result);
                    }

                    return;
                    
                } else {
                    foundMethod = m;
                }
            }
        }
        if (foundMethod != null) {
            System.err.println("Service operation " + foundMethod.getName() + " expects " + foundMethod.getParameterTypes().length + " arguments");
        } else {
            System.err.println("Operation not found: " + operationName);
        }
    }
}
