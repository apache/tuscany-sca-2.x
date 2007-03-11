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
package org.apache.tuscany.hessian.channel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;

import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.apache.tuscany.hessian.Channel;
import org.apache.tuscany.hessian.InvocationException;

/**
 * A channel implementation that uses HTTP as the transport protocol
 *
 * @version $Rev$ $Date$
 */
public class HttpChannel implements Channel {
    private URL destinationUrl;
    private boolean chunkedPost;
    private String basicAuth;
    private String user;
    private String password;
    private int readTimeout;

    public HttpChannel(URL url) {
        this.destinationUrl = url;
    }

    public Message send(String operation, Class<?> returnType, Message message) throws InvocationException {
        URLConnection conn;
        try {
            conn = openConnection(destinationUrl);
        } catch (IOException e) {
            throw new InvocationException(e);
        }
        if (chunkedPost && conn instanceof HttpURLConnection) {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setChunkedStreamingMode(8 * 1024);
        }

        OutputStream os;
        try {
            os = conn.getOutputStream();
        } catch (IOException e) {
            throw new HessianRuntimeException(e);
        }

        try {
            HessianOutput out = new HessianOutput(os);
            Object[] args;
            Object payload = message.getBody();
            if (payload != null && !payload.getClass().isArray()) {
                args = new Object[]{payload};
            } else {
                args = (Object[]) payload;
            }
            out.call(operation, args);
            out.flush();

            InputStream is = null;
            if (conn instanceof HttpURLConnection) {
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                int code = httpConn.getResponseCode();
                if (code != 200) {
                    StringBuffer sb = new StringBuffer();
                    int ch;

                    try {
                        is = httpConn.getInputStream();

                        if (is != null) {
                            while ((ch = is.read()) >= 0) {
                                sb.append((char) ch);
                            }

                            is.close();
                        }

                        is = httpConn.getErrorStream();
                        if (is != null) {
                            while ((ch = is.read()) >= 0) {
                                sb.append((char) ch);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        throw new InvocationException(e);
                    } catch (IOException e) {
                        if (is == null) {
                            throw new InvocationException("Invocation exception", String.valueOf(code), e);
                        }
                    }

                    if (is != null) {
                        is.close();
                    }
                    throw new InvocationException("Invocation exception", sb.toString());
                }
            }
            is = conn.getInputStream();

            HessianInput in = new HessianInput(is);
            // FIXME handle faults
            Object reply = in.readReply(returnType);
            Message msg = new MessageImpl();
            msg.setBody(reply);
            return msg;
        } catch (RuntimeException e) {
            if (conn instanceof HttpURLConnection) {
                ((HttpURLConnection) conn).disconnect();
            }
            throw e;
        } catch (Throwable e) {
            if (conn instanceof HttpURLConnection) {
                ((HttpURLConnection) conn).disconnect();
            }

            throw new InvocationException(e);
        }
    }

    protected URLConnection openConnection(URL url) throws IOException {
        URLConnection conn = url.openConnection();

        conn.setDoOutput(true);

        if (readTimeout > 0) {
            conn.setReadTimeout(readTimeout);
        }

        conn.setRequestProperty("Content-Type", "x-application/hessian");

        if (basicAuth != null)
            conn.setRequestProperty("Authorization", basicAuth);
        else if (user != null && password != null) {
            basicAuth = "Basic " + base64(user + ":" + password);
            conn.setRequestProperty("Authorization", basicAuth);
        }

        return conn;
    }

    private String base64(String value) {
        StringBuffer cb = new StringBuffer();

        int i = 0;
        for (i = 0; i + 2 < value.length(); i += 3) {
            long chunk = (int) value.charAt(i);
            chunk = (chunk << 8) + (int) value.charAt(i + 1);
            chunk = (chunk << 8) + (int) value.charAt(i + 2);

            cb.append(encode(chunk >> 18));
            cb.append(encode(chunk >> 12));
            cb.append(encode(chunk >> 6));
            cb.append(encode(chunk));
        }

        if (i + 1 < value.length()) {
            long chunk = (int) value.charAt(i);
            chunk = (chunk << 8) + (int) value.charAt(i + 1);
            chunk <<= 8;

            cb.append(encode(chunk >> 18));
            cb.append(encode(chunk >> 12));
            cb.append(encode(chunk >> 6));
            cb.append('=');
        } else if (i < value.length()) {
            long chunk = (int) value.charAt(i);
            chunk <<= 16;

            cb.append(encode(chunk >> 18));
            cb.append(encode(chunk >> 12));
            cb.append('=');
            cb.append('=');
        }

        return cb.toString();
    }

    public static char encode(long d) {
        d &= 0x3f;
        if (d < 26)
            return (char) (d + 'A');
        else if (d < 52)
            return (char) (d + 'a' - 26);
        else if (d < 62)
            return (char) (d + '0' - 52);
        else if (d == 62)
            return '+';
        else
            return '/';
    }


}
