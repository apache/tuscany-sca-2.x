package org.apache.tuscany.sca.binding.rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.binding.rest.provider.RESTServiceServlet;

import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

import com.sun.jersey.tuscany.common.CommonInterface;

public class RESTBindingForJersey implements CommonInterface{

	/**
	 * This map will save the mappings between the serviceInterface
	 * and the HttpServlet instance which will be passed by RESTServiceServlet  
	 *
	 */
	private static HashMap<String, RESTServiceServlet> interfacenameServletMap = new HashMap<String, RESTServiceServlet>();

	/**
	 * This is used to store the contextPath for the web application.
	 * It will be set in RESTServiceServlet
	 * It is currently being used by org.apache.tuscany.sca.implementation.java.invocation.JavaImplementationInvoker
	 * 
	 */
	private static String contextPath;

	/**
	 * updateInterfacenameServletMap() is called from RESTServiceServlet.
	 */
	public static void updateInterfacenameServletMap(String interfaceName, RESTServiceServlet restServiceServlet){

		//check if the class name already exists. if it does, do not add it.
		Set set = interfacenameServletMap.entrySet();
		Iterator hashIt = set.iterator();
		boolean classFoundFlag = false;
		while(hashIt.hasNext()){ 
			Map.Entry me = (Map.Entry)hashIt.next();
			if(interfaceName.equals( (String)me.getKey() )){
				classFoundFlag = true;
			}
		}

		//if it is not already in map, add it to the map
		if(classFoundFlag == false){
			interfacenameServletMap.put(interfaceName, restServiceServlet);
		}

	}

	/**
	 * @param clazz The class whose method has been invoked
	 * @return restServiceServlet
	 * Find the matching interface for the class in the hashMap and return the corresponding RESTServiceServlet instance
	 */
	public static RESTServiceServlet findRESTServiceServletFromClass(Class clazz){
		RESTServiceServlet restServiceServlet = null;
		Set set = interfacenameServletMap.entrySet();
		Iterator hashIt = set.iterator();
		while(hashIt.hasNext()){ 
			Map.Entry me = (Map.Entry)hashIt.next();
			if( (clazz.getSimpleName()).equals( (String)me.getKey() ) )
			{
				restServiceServlet = (RESTServiceServlet)me.getValue();
				break;
			}
		}
		return restServiceServlet;
	}

	/**
	 * @param interfaceList This will contain the list of all the interfaces the class (which declares the method) implements
	 * @return restServiceServlet
	 * Go through the interfaceList array and find the matching interface in the hashMap and return the corresponding RESTServiceServlet instance
	 */

	public static RESTServiceServlet findRESTServiceServletFromInterfaceList(Class[] interfaceList){
		RESTServiceServlet restServiceServlet = null;
		Set set = interfacenameServletMap.entrySet();
		Iterator hashIt = set.iterator();
		//label the outer for loop so that when a match is found, just break out of the whole thing
		outerForLoop:
			for(int i=0; i<interfaceList.length; i++){
				//if the name of the interface matches any key in the map, return the corresponding servlet
				while(hashIt.hasNext()){ 
					Map.Entry me = (Map.Entry)hashIt.next();
					if( (interfaceList[i].getSimpleName()).equals( (String)me.getKey() ) )
					{
						restServiceServlet = (RESTServiceServlet)me.getValue();
						break outerForLoop;
					}
				}
			}
		return restServiceServlet;
	}


	/**
	 * Prateek: This class implements CommonInterface. 
	 * com.sun.jersey.impl.model.method.dispatch.EntityParamDispatchProvider (in Jersey) 
	 * also implements this CommonInterface.
	 * EntityParamDispatchProvider invokes returnResult using a handle to the interface.
	 */
	public Object returnResult(Method meth, Object[] params) throws InvocationTargetException{
		
		//from the meth object, get the name of the class and then the interface
		// look up interfacenameServletMap to get the HttpServlet instance and use the 
		//'componentService, binding, serviceContract from there
		Class clazz = meth.getDeclaringClass();

		Class[] interfaceList = clazz.getInterfaces();
		RESTServiceServlet restServiceServlet = findRESTServiceServletFromInterfaceList(interfaceList);

		//if the SCA service interface is not a Java Interface but a Java class, then restServiceServlet above will be null
		//In that case the map will have the mapping of the Java Class and the RESTServiceServlet instance. 
		if(restServiceServlet == null){
			restServiceServlet = findRESTServiceServletFromClass(clazz);
		}

		Binding binding = restServiceServlet.getBinding();
		RuntimeComponentService componentService = restServiceServlet.getComponentService();
		InterfaceContract serviceContract = restServiceServlet.getServiceContract();

		String method = "Service." + meth.getName();

		//invoke the request
		RuntimeWire wire = componentService.getRuntimeWire(binding, serviceContract);
		Operation restOperation = findOperation(method, serviceContract);

		Object result = null;

		try {
			result = wire.invoke(restOperation, params);
			return result;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}       
	}

	private static Operation findOperation(String method,InterfaceContract serviceContract) {
		if (method.contains(".")) {
			method = method.substring(method.lastIndexOf(".") + 1);
		}

		List<Operation> operations = serviceContract.getInterface().getOperations();
		//componentService.getBindingProvider(binding).getBindingInterfaceContract().getInterface().getOperations();


		Operation result = null;
		for (Operation o : operations) {
			if (o.getName().equalsIgnoreCase(method)) {
				result = o;
				break;
			}
		}

		return result;
	}

	public static String getContextPath() {
		return contextPath;
	}

	public static void setContextPath(String contextPath) {
		RESTBindingForJersey.contextPath = contextPath;
	}

}

