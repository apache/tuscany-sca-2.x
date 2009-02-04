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
package workpool;

import java.util.EventObject;

public class WorkpoolEvent extends EventObject {

    private static final long serialVersionUID = -1273928009411948768L;

    public WorkpoolEvent(Object source) {
        super(source);
    }

    public WorkpoolEvent(WorkpoolEvent ev) {
        super(ev.source);
        type = ev.type;
        noWorker = ev.noWorker;
        nodeName = ev.nodeName;
    }

    public WorkpoolEvent(Object source, int typeEv, int worker) {
        super(source);
        type = typeEv;
        noWorker = worker;
        nodeName = "";
    }

    public WorkpoolEvent(Object source, int typeEv, int worker, String nodeName) {
        super(source);
        type = typeEv;
        noWorker = worker;
        this.nodeName = nodeName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public int getType() {
        return type;
    }

    public int workers() {
        return noWorker;
    }

    private int type;
    private int noWorker;
    private String nodeName;
    public static final int EVENT_MULTIPLE_ADD_WORKER = 0;
    public static final int EVENT_MULTIPLE_REMOVE_WORKER = 1;
    public static final int SINGLE_REMOVE_WORKER = 2;
    public static final int SINGLE_ADD_WORKER = 3;
}
