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

package org.apache.tuscany.sca.host.corba;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @version $Rev$ $Date$
 * Class partially copied from eclipse wst project
 * (http://repo1.maven.org/maven2/org/eclipse/wst/server/core/1.0.205-v20070829b/).
 * Finally we should use jar from maven dependency. Problem described in
 * http://www.fornax-platform.org/cp/display/fornax/2.+Hello+World+Tutorial+(CSC)?replyToComment=2098#comment-2098
 * needs to be fixed.
 */
public class SocketUtil {

    protected static final Object lock = new Object();

    private static Set<String> localHostCache;
    private static Set<String> notLocalHostCache = new HashSet<String>();
    private static Map<String, CacheThread> threadMap = new HashMap<String, CacheThread>();

    private static Set<InetAddress> addressCache;

    static class CacheThread extends Thread {
        private Set<InetAddress> currentAddresses;
        private Set<String> addressList;
        private String host;
        private Set<String> nonAddressList;
        private Map<String, CacheThread> threadMap2;

        public CacheThread(String host,
                           Set<InetAddress> currentAddresses,
                           Set<String> addressList,
                           Set<String> nonAddressList,
                           Map<String, CacheThread> threadMap2) {
            super("Caching localhost information");
            this.host = host;
            this.currentAddresses = currentAddresses;
            this.addressList = addressList;
            this.nonAddressList = nonAddressList;
            this.threadMap2 = threadMap2;
        }

        public void run() {
            if (currentAddresses != null) {
                Iterator<InetAddress> iter2 = currentAddresses.iterator();
                while (iter2.hasNext()) {
                    InetAddress addr = iter2.next();
                    String hostname = addr.getHostName();
                    String hostname2 = addr.getCanonicalHostName();
                    synchronized (lock) {
                        if (hostname != null && !addressList.contains(hostname))
                            addressList.add(hostname);
                        if (hostname2 != null && !addressList.contains(hostname2))
                            addressList.add(hostname2);
                    }
                }
            }

            try {
                InetAddress[] addrs = InetAddress.getAllByName(host);
                int length = addrs.length;
                for (int j = 0; j < length; j++) {
                    InetAddress addr = addrs[0];
                    String hostname = addr.getHostName();
                    String hostname2 = addr.getCanonicalHostName();
                    synchronized (lock) {
                        if (addr.isLoopbackAddress()) {
                            if (hostname != null && !addressList.contains(hostname))
                                addressList.add(hostname);
                            if (hostname2 != null && !addressList.contains(hostname2))
                                addressList.add(hostname2);
                        } else {
                            if (hostname != null && !nonAddressList.contains(hostname))
                                nonAddressList.add(hostname);
                            if (hostname2 != null && !nonAddressList.contains(hostname2))
                                nonAddressList.add(hostname2);
                        }
                    }
                }
            } catch (UnknownHostException e) {
                synchronized (lock) {
                    if (host != null && !nonAddressList.contains(host))
                        nonAddressList.add(host);
                }
            }
            synchronized (lock) {
                threadMap2.remove(host);
            }
        }
    }

    public static boolean isLocalhost(final String host) {
        if (host == null || host.equals(""))
            return false;

        if ("localhost".equals(host) || "127.0.0.1".equals(host))
            return true;

        // check simple cases
        try {
            InetAddress localHostaddr = InetAddress.getLocalHost();
            if (localHostaddr.getHostName().equals(host) || host.equals(localHostaddr.getCanonicalHostName())
                || localHostaddr.getHostAddress().equals(host))
                return true;
        } catch (Exception e) {

        }

        // check for current thread and wait if necessary
        boolean currentThread = false;
        try {
            Thread t = null;
            synchronized (lock) {
                t = threadMap.get(host);
            }
            if (t != null && t.isAlive()) {
                currentThread = true;
                t.join(30);
            }
        } catch (Exception e) {

        }

        // check if cache is still ok
        boolean refreshedCache = false;
        try {
            // get network interfaces
            final Set<InetAddress> currentAddresses = new HashSet<InetAddress>();
            currentAddresses.add(InetAddress.getLocalHost());
            Enumeration<?> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface inter = (NetworkInterface)nis.nextElement();
                Enumeration<InetAddress> ias = inter.getInetAddresses();
                while (ias.hasMoreElements())
                    currentAddresses.add(ias.nextElement());
            }

            // check if cache is empty or old and refill it if necessary
            if (addressCache == null || !addressCache.containsAll(currentAddresses)
                || !currentAddresses.containsAll(addressCache)) {
                CacheThread cacheThread = null;
                refreshedCache = true;

                synchronized (lock) {
                    addressCache = currentAddresses;
                    notLocalHostCache = new HashSet<String>();
                    localHostCache = new HashSet<String>(currentAddresses.size() * 3);

                    Iterator<InetAddress> iter = currentAddresses.iterator();
                    while (iter.hasNext()) {
                        InetAddress addr = iter.next();
                        String a = addr.getHostAddress();
                        if (a != null && !localHostCache.contains(a))
                            localHostCache.add(a);
                    }

                    cacheThread = new CacheThread(host, currentAddresses, localHostCache, notLocalHostCache, threadMap);
                    threadMap.put(host, cacheThread);
                    cacheThread.setDaemon(true);
                    cacheThread.setPriority(Thread.NORM_PRIORITY - 1);
                    cacheThread.start();
                }
                cacheThread.join(200);
            }
        } catch (Exception e) {
        }

        synchronized (lock) {
            if (localHostCache.contains(host))
                return true;
            if (notLocalHostCache.contains(host))
                return false;
        }

        // if the cache hasn't been cleared, maybe we still need to lookup the
        // host
        if (!refreshedCache && !currentThread) {
            try {
                CacheThread cacheThread = null;
                synchronized (lock) {
                    cacheThread = new CacheThread(host, null, localHostCache, notLocalHostCache, threadMap);
                    threadMap.put(host, cacheThread);
                    cacheThread.setDaemon(true);
                    cacheThread.setPriority(Thread.NORM_PRIORITY - 1);
                    cacheThread.start();
                }
                cacheThread.join(75);

                synchronized (lock) {
                    if (localHostCache.contains(host))
                        return true;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

}
