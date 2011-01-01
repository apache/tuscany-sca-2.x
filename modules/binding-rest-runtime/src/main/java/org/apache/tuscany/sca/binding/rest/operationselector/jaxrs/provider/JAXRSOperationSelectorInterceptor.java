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

package org.apache.tuscany.sca.binding.rest.operationselector.jaxrs.provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.List;

import javax.activation.DataSource;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.apache.tuscany.sca.common.http.HTTPContext;
import org.apache.tuscany.sca.common.http.HTTPUtils;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaOperation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * JAXRS operation selector Interceptor.
 * 
 * @version $Rev$ $Date$
*/
public class JAXRSOperationSelectorInterceptor implements Interceptor {
    private ExtensionPointRegistry extensionPoints;
    private RuntimeEndpoint endpoint;

    private RuntimeComponentService service;
    private InterfaceContract interfaceContract;
    private List<Operation> serviceOperations;

    private Invoker next;

    public JAXRSOperationSelectorInterceptor(ExtensionPointRegistry extensionPoints, RuntimeEndpoint endpoint) {
        this.extensionPoints = extensionPoints;
        this.endpoint = endpoint;

        this.service = (RuntimeComponentService)endpoint.getService();
        this.interfaceContract = service.getInterfaceContract();
        this.serviceOperations = service.getInterfaceContract().getInterface().getOperations();
    }

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }

    public Message invoke(Message msg) {
        try {
            HTTPContext bindingContext = (HTTPContext)msg.getBindingContext();
            
            // By-pass the operation selector
            if (bindingContext == null) {
                return getNext().invoke(msg);
            }

            String path = URLDecoder.decode(HTTPUtils.getRequestPath(bindingContext.getHttpRequest()), "UTF-8");

            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            List<Operation> operations =
                filterOperationsByHttpMethod(interfaceContract, bindingContext.getHttpRequest().getMethod());

            Operation operation = findOperation(path, operations);

            final JavaOperation javaOperation = (JavaOperation)operation;
            final Method method = javaOperation.getJavaMethod();

            if (path != null && path.length() > 0) {
                if (method.getAnnotation(Path.class) != null) {
                    msg.setBody(new Object[] {path});
                }
            }

            // FIXME: [rfeng] We should follow JAX-RS rules to identify the entity parameter
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length == 1) {
                Class<?> type = paramTypes[0];
                InputStream is = (InputStream)((Object[])msg.getBody())[0];
                Object target = convert(is, bindingContext.getHttpRequest().getContentType(), type);
                msg.setBody(new Object[] {target});
            } else if (paramTypes.length == 0) {
                msg.setBody(null);
            }

            msg.setOperation(operation);

            return getNext().invoke(msg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object convert(InputStream content, String contentType, Class<?> type) {
        if (type == DataSource.class) {
            return type.cast(new InputStreamDataSource(content, contentType));
        } else if (type == InputStream.class) {
            return type.cast(content);
        } else if (type == String.class) {
            try {
                StringWriter sw = new StringWriter();
                InputStreamReader reader = new InputStreamReader(content, "UTF-8");
                char[] buf = new char[8192];
                while (true) {
                    int size = reader.read(buf);
                    if (size < 0) {
                        break;
                    }
                    sw.write(buf, 0, size);
                }
                return type.cast(sw.toString());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        } else if (type == byte[].class) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                while (true) {
                    int size = content.read(buf);
                    if (size < 0) {
                        break;
                    }
                    bos.write(buf, 0, size);
                }
                return type.cast(bos.toByteArray());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            return content;
        }
    }
    
    public static final class InputStreamDataSource implements DataSource {

        public static final String DEFAULT_TYPE = "application/octet-stream";

        private final InputStream in;
        private final String ctype;

        public InputStreamDataSource(InputStream in) {
            this(in, null);
        }

        public InputStreamDataSource(InputStream in, String ctype) {
            this.in = in;
            this.ctype = (ctype != null) ? ctype : DEFAULT_TYPE;
        }

        public String getContentType() {
            return ctype;
        }

        public String getName() {
            return null;
        }

        public InputStream getInputStream() throws IOException {
            return in;
        }

        public OutputStream getOutputStream() throws IOException {
            return null;
        }

    }

    /**
     * Find the operation from the component service contract
     * @param componentService
     * @param http_method
     * @return
     */
    private static List<Operation> filterOperationsByHttpMethod(InterfaceContract interfaceContract, String http_method) {
        List<Operation> operations = null;

        if (http_method.equalsIgnoreCase("get")) {
            operations = (List<Operation>)interfaceContract.getInterface().getAttributes().get(GET.class);
        } else if (http_method.equalsIgnoreCase("put")) {
            operations = (List<Operation>)interfaceContract.getInterface().getAttributes().get(PUT.class);
        } else if (http_method.equalsIgnoreCase("post")) {
            operations = (List<Operation>)interfaceContract.getInterface().getAttributes().get(POST.class);
        } else if (http_method.equalsIgnoreCase("delete")) {
            operations = (List<Operation>)interfaceContract.getInterface().getAttributes().get(DELETE.class);
        }

        return operations;
    }

    /**
     * Find the operation from the component service contract
     * @param componentService
     * @param http_method
     * @return
     */
    private Operation findOperation(String path, List<Operation> operations) {
        Operation operation = null;

        for (Operation op : operations) {
            final JavaOperation javaOperation = (JavaOperation)op;
            final Method method = javaOperation.getJavaMethod();

            if (path != null && path.length() > 0) {
                if (method.getAnnotation(Path.class) != null) {
                    operation = op;
                    break;
                }
            } else {
                if (method.getAnnotation(Path.class) == null) {
                    operation = op;
                    break;
                }
            }
        }

        return operation;
    }
}
