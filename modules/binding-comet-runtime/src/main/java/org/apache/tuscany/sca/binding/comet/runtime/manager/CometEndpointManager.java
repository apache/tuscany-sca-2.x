package org.apache.tuscany.sca.binding.comet.runtime.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

public class CometEndpointManager {

	private static final ConcurrentMap<String, RuntimeEndpoint> endpoints = new ConcurrentHashMap<String, RuntimeEndpoint>();

	private CometEndpointManager() {
	}

	public static void add(String url, RuntimeEndpoint endpoint) {
		endpoints.put(url, endpoint);
	}

	public static RuntimeEndpoint get(String url) {
		return endpoints.get(url);
	}

	public static void remove(String url) {
		endpoints.remove(url);
	}

	public static void clear() {
		endpoints.clear();
	}
}
