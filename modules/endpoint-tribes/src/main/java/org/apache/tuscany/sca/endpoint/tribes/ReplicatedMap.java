/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tuscany.sca.endpoint.tribes;

import java.io.Serializable;

import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.group.RpcCallback;

/**
 * This file is copied from:
 * https://svn.apache.org/repos/asf/tomcat/tc6.0.x/tags/TOMCAT_6_0_20/java/org/apache/catalina/tribes/tipis/ReplicatedMap.java
 *
 */
public class ReplicatedMap extends AbstractReplicatedMap implements RpcCallback, ChannelListener, MembershipListener {
    private static final long serialVersionUID = -6318779627600581121L;
    protected static org.apache.juli.logging.Log log = org.apache.juli.logging.LogFactory.getLog(ReplicatedMap.class);

    //------------------------------------------------------------------------------
    //              CONSTRUCTORS / DESTRUCTORS
    //------------------------------------------------------------------------------
    /**
     * Creates a new map
     * @param channel The channel to use for communication
     * @param timeout long - timeout for RPC messags
     * @param mapContextName String - unique name for this map, to allow multiple maps per channel
     * @param initialCapacity int - the size of this map, see HashMap
     * @param loadFactor float - load factor, see HashMap
     */
    public ReplicatedMap(MapOwner owner,
                         Channel channel,
                         long timeout,
                         String mapContextName,
                         int initialCapacity,
                         float loadFactor,
                         ClassLoader[] cls) {
        super(owner, channel, timeout, mapContextName, initialCapacity, loadFactor, Channel.SEND_OPTIONS_DEFAULT, cls);
    }

    /**
     * Creates a new map
     * @param channel The channel to use for communication
     * @param timeout long - timeout for RPC messags
     * @param mapContextName String - unique name for this map, to allow multiple maps per channel
     * @param initialCapacity int - the size of this map, see HashMap
     */
    public ReplicatedMap(MapOwner owner,
                         Channel channel,
                         long timeout,
                         String mapContextName,
                         int initialCapacity,
                         ClassLoader[] cls) {
        super(owner, channel, timeout, mapContextName, initialCapacity, AbstractReplicatedMap.DEFAULT_LOAD_FACTOR,
              Channel.SEND_OPTIONS_DEFAULT, cls);
    }

    /**
     * Creates a new map
     * @param channel The channel to use for communication
     * @param timeout long - timeout for RPC messags
     * @param mapContextName String - unique name for this map, to allow multiple maps per channel
     */
    public ReplicatedMap(MapOwner owner, Channel channel, long timeout, String mapContextName, ClassLoader[] cls) {
        super(owner, channel, timeout, mapContextName, AbstractReplicatedMap.DEFAULT_INITIAL_CAPACITY,
              AbstractReplicatedMap.DEFAULT_LOAD_FACTOR, Channel.SEND_OPTIONS_DEFAULT, cls);
    }

    //------------------------------------------------------------------------------
    //              METHODS TO OVERRIDE
    //------------------------------------------------------------------------------
    protected int getStateMessageType() {
        return AbstractReplicatedMap.MapMessage.MSG_STATE_COPY;
    }

    /**
     * publish info about a map pair (key/value) to other nodes in the cluster
     * @param key Object
     * @param value Object
     * @return Member - the backup node
     * @throws ChannelException
     */
    protected Member[] publishEntryInfo(Object key, Object value) throws ChannelException {
        if (!(key instanceof Serializable && value instanceof Serializable))
            return new Member[0];
        //select a backup node
        Member[] backup = getMapMembers();

        if (backup == null || backup.length == 0)
            return null;

        //publish the data out to all nodes
        MapMessage msg =
            new MapMessage(getMapContextName(), MapMessage.MSG_COPY, false, (Serializable)key, (Serializable)value,
                           null, channel.getLocalMember(false), backup);

        getChannel().send(getMapMembers(), msg, getChannelSendOptions());

        return backup;
    }

}
