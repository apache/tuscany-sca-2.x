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
package org.apache.tuscany.sca.binding.notification.util;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @version $Rev$ $Date$
 */
public class IOUtils {

    public static final String  Notification_Source    = "Notification-Source";
    public static final String  Notification_Target    = "Notification-Target";
    public static final String  Notification_Operation = "Notification-Operation";

    public static final int DEF_BLOCK_SIZE = 512;

    public static Object sendHttpRequest(URL targetURL,
                                         String opName,
                                         Writeable wbody,
                                         ReadableContinuation rcont) throws Exception {
        if (opName == null) {
            opName = "";
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(Notification_Operation, opName);
        return sendHttpRequest(targetURL, headers, wbody, rcont);
    }
    
    public static Object sendHttpRequest(URL targetURL,
                                         Map<String, String> headers,
                                         Writeable wbody,
                                         ReadableContinuation rcont) throws Exception {
        
        String targetUri = targetURL.toString();
        String sourceUri = "";
        
        final HttpURLConnection con = (HttpURLConnection) targetURL.openConnection();
        con.setRequestMethod("POST");
        //con.setRequestProperty("Content-Length", Integer.toString(sbody.getBytes().length));
        con.setAllowUserInteraction(false);
        con.setInstanceFollowRedirects(false);
        if (targetUri != null) {
            con.setRequestProperty(Notification_Target, targetUri);
        }
        
        if (sourceUri != null) {
            con.setRequestProperty(Notification_Source, sourceUri);
        }
        
        for (String key : headers.keySet()) {
            con.setRequestProperty(key, headers.get(key));
        }
        con.setDoOutput(true);
        con.setDoInput(true);
        con.connect();
        Object response = null;
        try {
            if (wbody != null) {
                OutputStream ost = con.getOutputStream();
                wbody.write(ost);
            }
            else {
                throw new IOUtilsException("Missing writeable body");
            }
            final int rc = con.getResponseCode();
            switch (rc) {
                case HttpURLConnection.HTTP_OK:
                    if (rcont != null) {
                        InputStream ist = con.getInputStream();
                        response = rcont.read(ist);
                    }
                    break;
                case HttpURLConnection.HTTP_NO_CONTENT:
                    break;
                default:
                    throw new RuntimeException("Unexpected response code: " + rc);
            }
        }
        finally
        {
            con.disconnect();
        }
        return response;
    }
    
    public interface Writeable {
        void write(OutputStream os) throws IOUtilsException;
    }
    
    public interface ReadableContinuation {
        Object read(InputStream is) throws IOUtilsException;
    }
    
    @SuppressWarnings("serial")
    public static class IOUtilsException extends Exception {
        
        public IOUtilsException(String message) {
            super(message);
        }

        public IOUtilsException(Throwable cause) {
            super(cause);
        }
    }
    
    public static byte [] readFully(final InputStream ist, int len) throws IOException {
        ByteArrayOutputStream baost = new ByteArrayOutputStream();
        copyStream(ist,baost,len);
        return baost.toByteArray();
    }

    public static int copyStream(final InputStream ist, final OutputStream ost) throws IOException {
        return copyStream(ist, ost, -1, 0);
    }

    public static int copyStream(final InputStream ist, final OutputStream ost, int length) throws IOException {
        return copyStream(ist, ost, length, 0);
    }

    public static int copyStream(final InputStream ist, final OutputStream ost, final int length, int blockSize) throws IOException {

        int cbCopied = 0;
        if (blockSize <= 0) {
            blockSize = DEF_BLOCK_SIZE;
        }

        final byte[] block = new byte[blockSize];
        boolean done = length == 0;
        while (!done) {
            try {
                // determine how many bytes to read
                final int cbToRead = length == -1 ? block.length : (Math.min(length - cbCopied, block.length));
                final int cbRead = ist.read(block, 0, cbToRead);
                if (cbRead == -1) {
                    done = true;
                }
                else {
                    ost.write(block, 0, cbRead);
                    cbCopied += cbRead;
                    done = cbCopied == length;
                }
            } catch (final EOFException e) {
                done = true;
            }
        }
        ost.flush();
        return cbCopied;
    }
}
