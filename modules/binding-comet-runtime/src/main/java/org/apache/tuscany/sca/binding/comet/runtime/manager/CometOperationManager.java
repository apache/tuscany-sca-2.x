package org.apache.tuscany.sca.binding.comet.runtime.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.tuscany.sca.interfacedef.Operation;

public class CometOperationManager {

	private static final ConcurrentMap<String, Operation> operations = new ConcurrentHashMap<String, Operation>();

	private CometOperationManager() {
	}

	public static void add(String url, Operation operation) {
		operations.put(url, operation);
	}

	public static Operation get(String url) {
		return operations.get(url);
	}

	public static void remove(String url) {
		operations.remove(url);
	}

	public static void clear() {
		operations.clear();
	}
}
