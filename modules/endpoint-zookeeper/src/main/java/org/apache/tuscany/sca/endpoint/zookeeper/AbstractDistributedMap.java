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

package org.apache.tuscany.sca.endpoint.zookeeper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * 
 */
public class AbstractDistributedMap<V> extends AbstractMap<String, V> implements Map<String, V>, Watcher {
    protected ZooKeeper zooKeeper;
    protected ClassLoader classLoader;
    protected String root;

    /**
     * @param zooKeeper
     * @param root
     * @param classLoader
     */
    public AbstractDistributedMap(ZooKeeper zooKeeper, String root, ClassLoader classLoader) {
        super();
        this.zooKeeper = zooKeeper;
        this.root = root;
        this.classLoader = classLoader;
    }

    public void start() {
        // FIXME:
        this.zooKeeper.register(this);
        try {
            String path = getPath(root);
            Stat stat = zooKeeper.exists(path, false);
            if (stat == null) {
                zooKeeper.create(path, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Set<Map.Entry<String, V>> entrySet() {
        String path = getPath(root);
        List<String> children = Collections.emptyList();
        try {
            try {
                children = zooKeeper.getChildren(path, false);
            } catch (KeeperException e) {
                if (e.code() == Code.NONODE) {
                    return Collections.emptySet();
                } else {
                    throw e;
                }
            }
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }
        return new EntrySet(children);
    }

    protected String getName(Object key) {
        String name = String.valueOf(key);
        name = name.replace("$", "$$");
        return name.replace('/', '$');
    }

    public String getPath(String... uris) {
        StringBuffer buffer = new StringBuffer();
        for (String uri : uris) {
            buffer.append('/').append(getName(uri));
        }
        return buffer.toString();
    }

    protected byte[] serialize(V value) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(value);
        oos.close();
        return bos.toByteArray();
    }

    protected V deserialize(byte[] bytes, final ClassLoader classLoader) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis) {
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                ClassLoader loader = classLoader;
                if (loader == null) {
                    loader = Thread.currentThread().getContextClassLoader();
                }
                try {
                    return Class.forName(desc.getName(), false, loader);
                } catch (ClassNotFoundException e) {
                    return super.resolveClass(desc);
                }
            }
        };
        return (V)ois.readObject();
    }

    @Override
    public V get(Object key) {
        String path = getPath(root, getName(key));
        return getData(path);
    }

    protected V getData(String path) {
        try {
            Stat stat = new Stat();
            byte[] data = zooKeeper.getData(path, false, stat);
            return deserialize(data, classLoader);
        } catch (KeeperException e) {
            if (e.code() == Code.NONODE) {
                return null;
            } else {
                throw new ServiceRuntimeException(e);
            }
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }

    }

    @Override
    public V put(String key, V value) {
        try {
            String path = getPath(root, getName(key));
            Stat stat = new Stat();
            byte[] data = serialize(value);

            try {
                byte[] oldData = zooKeeper.getData(path, false, stat);
                zooKeeper.setData(path, data, -1);
                return deserialize(oldData, classLoader);
            } catch (KeeperException e) {
                if (e.code() == Code.NONODE) {
                    zooKeeper.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    return null;
                } else {
                    throw e;
                }
            }
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }

    }

    @Override
    public V remove(Object key) {
        try {
            String path = getPath(root, getName(key));
            try {
                Stat stat = new Stat();
                byte[] oldData = zooKeeper.getData(path, false, stat);
                zooKeeper.delete(path, -1);
                return deserialize(oldData, classLoader);
            } catch (KeeperException e) {
                if (e.code() == Code.NONODE) {
                    return null;
                } else {
                    throw e;
                }
            }
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }

    }

    private class MapEntry implements Map.Entry<String, V> {
        private String key;

        /**
         * @param key
         */
        public MapEntry(String key) {
            super();
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public V getValue() {
            try {
                try {
                    byte[] data = zooKeeper.getData(getPath(root, getName(key)), false, new Stat());
                    return deserialize(data, classLoader);
                } catch (KeeperException e) {
                    if (e.code() == Code.NONODE) {
                        return null;
                    } else {
                        throw e;
                    }
                }
            } catch (Throwable e) {
                throw new ServiceRuntimeException(e);
            }
        }

        public V setValue(V value) {
            return put(key, value);
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<String, V>> {
        private final int size;
        private final Iterator<String> childrenIterator;

        /**
         * @param size
         * @param childrenIterator
         */
        public EntrySet(Collection<String> children) {
            super();
            this.size = children.size();
            this.childrenIterator = children.iterator();
        }

        @Override
        public Iterator<Map.Entry<String, V>> iterator() {
            return new Iterator<Map.Entry<String, V>>() {
                private String path;

                public boolean hasNext() {
                    return childrenIterator.hasNext();
                }

                public Map.Entry<String, V> next() {
                    path = childrenIterator.next();
                    return new MapEntry(path);
                }

                public void remove() {
                    childrenIterator.remove();
                    try {
                        zooKeeper.delete(getPath(root, path), -1);
                    } catch (Throwable e) {
                        throw new ServiceRuntimeException(e);
                    }
                }
            };

        }

        @Override
        public int size() {
            return size;
        }
    }

    public void process(WatchedEvent event) {
        System.out.println(event);
    }
}
