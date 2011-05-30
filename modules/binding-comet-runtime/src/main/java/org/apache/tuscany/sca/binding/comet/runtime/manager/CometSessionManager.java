package org.apache.tuscany.sca.binding.comet.runtime.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.atmosphere.cpr.Broadcaster;

public class CometSessionManager {

    private static final ConcurrentMap<String, Broadcaster> broadcasters = new ConcurrentHashMap<String, Broadcaster>();

    private CometSessionManager() {
    }

    public static void add(String sessionId, Broadcaster broadcaster) {
        broadcasters.put(sessionId, broadcaster);
    }

    public static Broadcaster get(String sessionId) {
        return broadcasters.get(sessionId);
    }

    public static void remove(String sessionId) {
        broadcasters.remove(sessionId);
    }

    public static void clear() {
        broadcasters.clear();
    }
}
