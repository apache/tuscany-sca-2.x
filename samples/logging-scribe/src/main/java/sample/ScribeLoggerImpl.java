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
package sample;

import generated.scribe.thrift.scribe;
import generated.scribe.thrift.LogEntry;
import java.util.Collections;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Destroy;

@Scope("COMPOSITE")
public class ScribeLoggerImpl implements Logger {

    @Property
    public String host;

    @Property
    public int port;

    scribe.Client scribe;
    TTransport transport;

    @Init
    public void init() {
        try {
            final TSocket socket = new TSocket(host, port);
            socket.setTimeout(1000);
            transport = new TFramedTransport(socket);
            final TProtocol protocol = new TBinaryProtocol(transport);
            scribe = new scribe.Client(protocol);
            transport.open();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Destroy
    public void destroy() {
        transport.close();
    }

    public int log(String category, String message) {
        try {
            scribe.Log(Collections.singletonList(new LogEntry(category, message)));
            return 1;
        } catch (TException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
