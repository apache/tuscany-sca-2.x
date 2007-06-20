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
package org.apache.tuscany.implementation.osgi.invocation;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;


import org.apache.tuscany.implementation.osgi.OSGiImplementationInterface;
import org.apache.tuscany.implementation.osgi.context.OSGiPropertyValueObjectFactory;
import org.apache.tuscany.implementation.osgi.runtime.OSGiRuntime;
import org.apache.tuscany.implementation.osgi.xml.OSGiImplementation;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Multiplicity;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.core.invocation.JDKProxyService;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.factory.ObjectCreationException;
import org.apache.tuscany.sca.factory.ObjectFactory;
import org.apache.tuscany.sca.scope.Scope;
import org.apache.tuscany.sca.scope.InstanceWrapper;
import org.apache.tuscany.sca.scope.ScopedImplementationProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osoa.sca.annotations.AllowsPassByReference;

/**
 * The runtime instantiation of OSGi component implementations
 * 
 */
public class OSGiImplementationProvider  implements ScopedImplementationProvider {
	
	private static final String COMPONENT_NAME = "component.name";
	private static final String REFERENCE_NAME = "reference.name";
  
	private OSGiImplementation implementation;
    private BundleContext bundleContext;
    
    private Hashtable<RuntimeWire, Reference> referenceWires = new Hashtable<RuntimeWire,Reference>();
    private Hashtable<RuntimeWire, ComponentReference> componentReferenceWires 
                       = new Hashtable<RuntimeWire,ComponentReference>();
    private HashSet<RuntimeWire> resolvedWires = new HashSet<RuntimeWire>();   
    private boolean wiresResolved;
    

    private Hashtable<String, Object> componentProperties = new Hashtable<String, Object>();
    private RuntimeComponent runtimeComponent;
    
    private Bundle osgiBundle;
    private OSGiServiceListener osgiServiceListener;
    private PackageAdmin packageAdmin;
    
    
    private DataBindingExtensionPoint dataBindingRegistry;
    

    public OSGiImplementationProvider(RuntimeComponent definition,
            OSGiImplementationInterface impl,
            DataBindingExtensionPoint dataBindingRegistry) throws BundleException {
    	
    	
        this.implementation = (OSGiImplementation)impl;
        this.runtimeComponent = definition;
        this.dataBindingRegistry = dataBindingRegistry;

        bundleContext = getBundleContext();
        osgiBundle = installBundle();
        
        // Install and start all dependent  bundles
        String[] imports = implementation.getImports();
        ArrayList<Bundle>bundles = new ArrayList<Bundle>();
        for (int i = 0; i < imports.length; i++) {
        	String location = imports[i].trim();
        	if (location.length() > 0) {
        		Bundle bundle = bundleContext.installBundle(location);
        		bundles.add(bundle);
        	}        		
        }
        for (int i = 0; i < bundles.size(); i++) {
        	bundles.get(i).start();
        }
        
        // PackageAdmin is used to resolve bundles 
        org.osgi.framework.ServiceReference packageAdminReference = 
            bundleContext.getServiceReference("org.osgi.service.packageadmin.PackageAdmin");
        if (packageAdminReference != null) {
            packageAdmin = (PackageAdmin) bundleContext.getService(packageAdminReference);
        }
        
    }
    
    protected RuntimeComponent getRuntimeComponent() {
    	return runtimeComponent;
    }
    
    protected OSGiImplementation getImplementation() {
    	return implementation;
    }
    
    // Create a property table from the list of properties 
    // The source properties are properties read from <property/> elements
    // Create property values in the table of the appropriate class based 
    // on the property type specified.
    private void processProperties(List<?> props, Hashtable<String, Object> propsTable) {
        
        OSGiPropertyValueObjectFactory factory = new OSGiPropertyValueObjectFactory();
        if (props != null) {
            for (Object p : props) {
             
                Property prop = (Property)p;
                ObjectFactory<?> objFactory = factory.createValueFactory(prop, prop.getValue());
                Object value = objFactory.getInstance();           

                propsTable.put(prop.getName(), value);
            }
        }
    }

   
    private BundleContext getBundleContext() throws BundleException {

    	if (bundleContext == null)
    		bundleContext = OSGiRuntime.startAndGetBundleContext();   	
		
        return bundleContext;
    }
    
    private Bundle getBundle() throws Exception {
    	
    	Bundle[] bundles = bundleContext.getBundles();
        for (Bundle b : bundles) {
        	if (b.getLocation().equals(implementation.getBundleName())) {
        		return b;
        	}
        }
        return null;
    }

    
    // Install the bundle corresponding to this component.
    private Bundle installBundle() throws ObjectCreationException {
    	try {
    		
    		Bundle bundle = null;
            
            if ((bundle = getBundle()) == null) {
               
                String bundleName = implementation.getBundleLocation();
            	            	
			    bundle = bundleContext.installBundle(bundleName);
				
            }          

			osgiServiceListener = new OSGiServiceListener(bundle);
			
			bundleContext.addServiceListener(osgiServiceListener);	

			return bundle;
			
		} catch (Exception e) {
			throw new ObjectCreationException(e);
		}
    }
    
    private String getOSGiFilter(ComponentService service) {
        
        String filter = "";
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        processProperties(implementation.getServiceProperties(service.getName()), props);
        
        if (props != null && props.size() > 0) {
            for (String propName : props.keySet()) {
                filter = filter + "(" + propName + "=" + props.get(propName)  + ")";
            }
    
            if (props.size() > 1) filter = "(&" + filter + ")";
        }
        else
            filter = null;
        return filter;
    }
    
    /*
     * Return a matching service registered by the specified bundle.
     * If <implementation.osgi /> has the attribute filter defined, return a service
     * reference that matches the filter. Otherwise, return a service which has a component
     * name equal to this component's name. If not found, return a service which no
     * component name set.
     * 
     * Even though services registered by this bundle can be filtered using the
     * service listener, we use this method to filter all service references so that 
     * the service matching functionality of OSGi can be directly used.
     */
    private org.osgi.framework.ServiceReference getOSGiServiceReference(Bundle bundle, 
            String serviceName, String filter) 
        throws InvalidSyntaxException {
    	
        if (filter != null && filter.length() > 0) {
    	   org.osgi.framework.ServiceReference[] references = 
   			    bundleContext.getServiceReferences(serviceName, filter);
    	   
    	   if (references != null) {
    		   for (org.osgi.framework.ServiceReference ref : references) {
    			   if (ref.getBundle() == bundle)
    				   return ref;
    		   }
    	   }
    	
    	   return null;
    	   
        }
    	
    	filter = "(" + COMPONENT_NAME + "="+ runtimeComponent.getName() + ")";
        
		org.osgi.framework.ServiceReference[] references = 
			bundleContext.getServiceReferences(serviceName, filter);
		
		if (references != null) {			
			for (org.osgi.framework.ServiceReference ref : references) {
 			   if (ref.getBundle() == bundle)
 				   return ref;
 		    }
		}
		
		references = bundleContext.getServiceReferences(serviceName, null);
		
		org.osgi.framework.ServiceReference reference = null;
		
		if (references != null) {
		    for (org.osgi.framework.ServiceReference ref : references) {
		    	
		    	if (ref.getBundle() != bundle)
		    		continue;
			    Object compName = ref.getProperty(COMPONENT_NAME);
			    if (compName == null && reference == null)
			    	reference = ref;
			    if (runtimeComponent.getName().equals(compName)) {
				    reference = ref;
				    break;
			    }
		    }
		}
		
		return reference;
    }
    
    protected void startBundle(final Bundle bundle) 
            throws ObjectCreationException {

        try {
    
            if (bundle.getState() != Bundle.ACTIVE && bundle.getState() != Bundle.STARTING) {
        
                setBundleProperties(runtimeComponent.getName());
        
                resolveBundle(bundle);
            
                if (bundle.getState() != Bundle.ACTIVE && bundle.getState() != Bundle.STARTING) {

                    int retry = 0;
                
                    while (retry++ < 10) {
                        try {
                            bundle.start();
                            break;
                       } catch (BundleException e) {
                            // It is possible that the thread "Refresh Packages" is in the process of
                            // changing the state of this bundle. 
                            Thread.yield();
                        
                            if (retry == 10)
                            throw e;
                        }
                    }
                }                   
            }
   
        } catch (Exception e) {
            throw new ObjectCreationException(e);
        }
    }
    
    
    
    protected org.osgi.framework.ServiceReference getOSGiServiceReference(final Bundle bundle, 
            RuntimeComponentService service) 
            throws ObjectCreationException {
        
    	try {
    		
            String filter = getOSGiFilter(service);
            Interface serviceInterface = service.getInterfaceContract().getInterface();
            String serviceInterfaceName = null;

            org.osgi.framework.ServiceReference osgiServiceReference = null;
            
            if (serviceInterface instanceof JavaInterface) {
                serviceInterfaceName = ((JavaInterface)serviceInterface).getJavaClass().getName();
                                  
    			if ((osgiServiceReference = getOSGiServiceReference(bundle, serviceInterfaceName, filter)) == null) {
        			
    				// The service listener for our bundle will notify us when the service is registered.
    				synchronized (implementation) {							
    					    					
    					// When declarative services are used, the component is started asynchronously
    					// So this thread has to wait for the service to be registered by the component
    					// activate method
    					// For regular bundle activators, bundle.start activates the bundle synchronously
    					// and hence the service would probably have been started by the bundle activator
    					while ((osgiServiceReference = getOSGiServiceReference(bundle, serviceInterfaceName, filter)) == null) {
    		    							
    					    // Wait for the bundle to register the service
    				        implementation.wait(100);
    					}
    				}   
                    
    					
        		}
            }
    			
			
    		
    		
    		return osgiServiceReference;
    		
		} catch (Exception e) {
			throw new ObjectCreationException(e);
		}
    }
    
    
    private Bundle installDummyBundle(Class<?> interfaceClass, boolean includeInterface)
            throws Exception {
        
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	    	
    	String EOL = "\n";
    	
    	String packageName = interfaceClass.getPackage().getName();
    	String bundleName = interfaceClass.getName();
    	
    	
    	String manifestStr = "Manifest-Version: 1.0" + EOL +
						"Bundle-ManifestVersion: 2" + EOL +
						"Bundle-Name: " + bundleName + EOL +
						"Bundle-SymbolicName: " + bundleName + EOL +
						"Bundle-Version: " + "1.0.0" + EOL +
						"Bundle-Localization: plugin" + EOL;
    	
						
    	StringBuilder manifestBuf = new StringBuilder();
    	manifestBuf.append(manifestStr);
    	manifestBuf.append("Export-Package: " + packageName + EOL);
       
    	ByteArrayInputStream manifestStream = new ByteArrayInputStream(manifestBuf.toString().getBytes());
    	Manifest manifest = new Manifest();
    	manifest.read(manifestStream);
    	
    	JarOutputStream jarOut = new JarOutputStream(out, manifest);
    	
        if (includeInterface) {
    	    String interfaceClassName = interfaceClass.getName().replaceAll("\\.", "/") + ".class";
    	
    	    URL url = interfaceClass.getClassLoader().getResource(interfaceClassName);
    	    String path = url.getPath();
    	
    	    ZipEntry ze = new ZipEntry(interfaceClassName);
    
    	    jarOut.putNextEntry(ze);
    	    FileInputStream file = new FileInputStream(path);
    	    byte[] fileContents = new byte[file.available()];
    	    file.read(fileContents);
            jarOut.write(fileContents);
        
            file.close();    	
        }
        
    	jarOut.close();
    	out.close();
    	
    	
    	ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        
        
        Bundle bundle = bundleContext.installBundle(
                "file://" + interfaceClass.getName() + ".jar",
        		in);
        bundle.start();
        
        return bundle;
        
    }
    
    
    
    public InstanceWrapper<?> createInstanceWrapper() throws ObjectCreationException {
    	
        return new OSGiInstanceWrapper<Object>(this, bundleContext, osgiBundle);
    }
    
   
    
    /*
     * This method checks if the wires can be resolved, but it is not sufficient to check
     * if proxies have been installed if necessary. Hence this has been replaced by a 
     * simple check which guarantees that each wire gets processed once.
     */
    private boolean isWireResolved(String interfaceClassName, RuntimeWire wire) throws Exception {
    	
    	boolean wireResolved = false; 
    	String componentName = null;
        
    	org.osgi.framework.ServiceReference[] refs = null;


        if (wire.getTarget().getComponent() != null)
            componentName = wire.getTarget().getComponent().getName();
        
    	// Properties specified in the reference element should be matched exactly
    	// Target should be optionally matched against component.name
    	// Reference name should be optionally matched against reference.name
		ComponentReference scaRef = componentReferenceWires.get(wire);
        
		Hashtable<String, Object> props = new Hashtable<String, Object>();
        processProperties(implementation.getReferenceProperties(scaRef.getName()), props);
        
		if (props.size() > 0) {
			String filter = "";
			
			int numElements = 0;
            // FIXME: Perhaps we should do a strict match using property type as well.
            for (String propName : props.keySet()) {
                if (propName.equals("component.name")) {
                    componentName = (String)props.get(propName);
                } else {
                    filter = filter + "(" + propName + "=" + props.get(propName)  + ")";
                    numElements++;
                }
            }

			if (numElements > 1) filter = "(&" + filter + ")";
			else if (numElements == 0) filter = null;
			
			refs = bundleContext.getServiceReferences(interfaceClassName, filter);
			
		} 
		
		if (refs != null) {
    		for (org.osgi.framework.ServiceReference ref : refs) {
   	    		Object refName = ref.getProperty(REFERENCE_NAME);
   	    		Object compName = ref.getProperty(COMPONENT_NAME);
			    if ((refName == null || refName.equals(scaRef.getName()))&&
			    	(compName == null || compName.equals(componentName))){
			    	
			    	wireResolved = true;			   
				    break;
			    }
			}
    		
    	}
		return wireResolved;
    }
    
    private  void resolveWireCreateDummyBundles(Bundle bundle, Class interfaceClass, RuntimeWire wire) throws Exception {
        
        
        try {
                    
            bundle.loadClass(interfaceClass.getName());
                            
        } catch (ClassNotFoundException e) {
                        
            // The interface used by the proxy is not in the source bundle
            // A dummy bundle needs to be installed to create the proxy
                     
            installDummyBundle(interfaceClass, true);
                                
            if (bundle.getState() == Bundle.INSTALLED && packageAdmin != null) {
                                    
                packageAdmin.resolveBundles(new Bundle[]{bundle});
                              
            } 

        }
    }
    
    /**
     * For OSGi->Java wires, create a proxy corresponding to the Java interfaces
     * and register the proxy with the OSGi registry, so that the source OSGi bundle can 
     * locate the target Java instance from the registry like a regular OSGi service.
     * 
     * For OSGi->OSGi wires, start the target OSGi bundle, so that references of the
     * target are resolved before the source OSGi bundle is started. If the reference
     * has properties specified, create a Proxy and register a service with highest
     * possible ranking. The Proxy should wire to the correct OSGi instance specified
     * in the SCA composite.
     * 
     * The first phase determines whether a proxy should be installed. It also registers
     * a dummy bundle if necessary to resolve the bundle. When phase1 is completed on all
     * wires of the component, the bundle should be resolved. Phase2 registers the proxy service.
     */
    private boolean resolveWireResolveReferences(Bundle bundle, Class interfaceClass, RuntimeWire wire,
            boolean isOSGiToOSGiWire) throws Exception {
    	
    	boolean createProxy = false;
       
	    ComponentReference scaRef = componentReferenceWires.get(wire);
        Hashtable<String, Object> targetProperties = new Hashtable<String, Object>();
        processProperties(implementation.getReferenceProperties(scaRef.getName()), targetProperties);
            
            
		if (bundle.getState() != Bundle.ACTIVE) {

			if (isOSGiToOSGiWire) {
                    
				OSGiImplementationProvider implProvider = (OSGiImplementationProvider)wire.getTarget().getComponent().getImplementationProvider();
					
				// This is an OSGi->OSGi wire
				isOSGiToOSGiWire = true;
					
				// If the target component is stateless, use a proxy to create a new service each time 
				if (!implProvider.getScope().equals(Scope.COMPOSITE)) createProxy = true;
					
				// If any of the operations in the target interface is non-blocking, create a proxy
				List<Operation> ops = wire.getTarget().getInterfaceContract().getInterface().getOperations();
				for (Operation op : ops) {
					if (op.isNonBlocking())
						createProxy = true;
			    }
                    
                if (targetProperties.size() > 0) {
                    createProxy = true;
                }
                    
                    
				// Since this is an OSGi->OSGi wire, start the target bundle before starting the
				// source bundle if there is no proxy. For direct wiring without a proxy, this ordering 
			    // is irrelevant in terms of class resolution, but the target needs to be started at some 
				// point. But there is no opportunity later on to start the target OSGi bundle without a proxy.	
				// When a Proxy is used, the target bundle needs to be resolved for the source bundle
				// to be resolved so that the interface is visible to the source. In this case the bundle
				// will be started when an instance is needed.					
				if (!createProxy) {	
					implProvider.startBundle(implProvider.osgiBundle);
				}
				else {
					implProvider.resolveBundle(implProvider.osgiBundle);
				}
		    }
            else {
                createProxy = true;
            }
        }
            
        return createProxy;
    }
    
    
    // Register proxy service 
    private void resolveWireRegisterProxyService(Bundle bundle, Class interfaceClass, RuntimeWire wire) throws Exception {
          
        ComponentReference scaRef = componentReferenceWires.get(wire);
        Hashtable<String, Object> targetProperties = new Hashtable<String, Object>();
        processProperties(implementation.getReferenceProperties(scaRef.getName()), targetProperties);
        targetProperties.put(Constants.SERVICE_RANKING, Integer.MAX_VALUE);
        
        RuntimeComponent targetComponent = wire.getTarget().getComponent();
        if (targetProperties.get(COMPONENT_NAME) == null && targetComponent != null)
            targetProperties.put(COMPONENT_NAME, targetComponent.getName());
        
           
        JDKProxyService proxyService = new JDKProxyService();
              
        Class<?> proxyInterface = bundle.loadClass(interfaceClass.getName());
                

        Object proxy = proxyService.createProxy(proxyInterface, wire);
            
        bundleContext.registerService(proxyInterface.getName(), proxy, targetProperties);
            
        
    }
    
    
    private void resolveBundle(Bundle bundle) throws ObjectCreationException {
    	
        
		try {
			
			if (!wiresResolved) {
            	wiresResolved = true;
				    
                boolean[] createProxyService = new boolean[referenceWires.size()];
                Class<?>[] interfaceClasses = new Class<?>[referenceWires.size()] ;
                boolean[] isOSGiToOSGiWire = new boolean[referenceWires.size()];
                boolean[] wireResolved = new boolean[referenceWires.size()];
                int index = 0;
				for (RuntimeWire wire : referenceWires.keySet()) {
				
					Reference reference = referenceWires.get(wire);
                    
                    isOSGiToOSGiWire[index] = wire.getTarget().getComponent() != null &&
                            wire.getTarget().getComponent().getImplementationProvider() 
                            instanceof OSGiImplementationProvider;
                            
					Interface refInterface = reference.getInterfaceContract().getInterface();
					if (refInterface instanceof JavaInterface) {
						interfaceClasses[index] = ((JavaInterface)refInterface).getJavaClass();
					
                        if (!isOSGiToOSGiWire[index])
                            resolveWireCreateDummyBundles(bundle, interfaceClasses[index], wire);

					}
                    if (interfaceClasses[index] != null &&
                            !isWireResolved(interfaceClasses[index].getName(), wire)) {
                        
                        resolvedWires.add(wire);                       
                    }
                    else
                        wireResolved[index] = true;
                    
                    index++;
				}
                
                index = 0;
                for (RuntimeWire wire : referenceWires.keySet()) {
                    
                    if (!wireResolved[index]) {
                        createProxyService[index] = resolveWireResolveReferences(bundle, 
                            interfaceClasses[index], 
                            wire,
                            isOSGiToOSGiWire[index]);
                    }
                    index++;
                }
                
                index = 0;
                for (RuntimeWire wire : referenceWires.keySet()) {
                    
                    if (createProxyService[index] && !wireResolved[index])
                        resolveWireRegisterProxyService(bundle, interfaceClasses[index], wire);
                    index++;
                }
            }
            else if (bundle.getState() == Bundle.INSTALLED && packageAdmin != null) {
                packageAdmin.resolveBundles(new Bundle[] {bundle});
            }
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ObjectCreationException(e);
		} 		
    }
    
   
    
    private void setBundleProperties(String pid) {
    	   	
    	try {

    		if (componentProperties.size() == 0)
    			return;
    		
			org.osgi.framework.ServiceReference configAdminReference = bundleContext.getServiceReference("org.osgi.service.cm.ConfigurationAdmin");
			if (configAdminReference != null) {
				
				Object cm = bundleContext.getService(configAdminReference);
				Class cmClass = cm.getClass().getClassLoader().loadClass("org.osgi.service.cm.ConfigurationAdmin");
				Method m = cmClass.getMethod("getConfiguration", String.class, String.class);
				
				Object config = m.invoke(cm, pid, null);
				Class configClass = cm.getClass().getClassLoader().loadClass("org.osgi.service.cm.Configuration");
				
				Method getMethod = configClass.getMethod("getProperties");
				Dictionary props = (Dictionary)getMethod.invoke(config);
				if (props == null)
					props = new Hashtable<String, Object>();
				
				for (String propertyName : componentProperties.keySet()) {
					props.put(propertyName, componentProperties.get(propertyName));
				}
				
				Method updateMethod = configClass.getMethod("update", Dictionary.class);
				updateMethod.invoke(config, props);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

    
    public boolean isOptimizable() {
        return false;
    }
    
    public Scope getScope() {
        return implementation.getScope();
    }

    public boolean isEagerInit() {
        return implementation.isEagerInit();
    }

    public long getMaxAge() {
        return implementation.getMaxAge();
    }

    public long getMaxIdleTime() {
        return implementation.getMaxIdleTime();
    }
    
    
    public Invoker createTargetInvoker(RuntimeComponentService service, Operation operation)  {
       
        
        Interface serviceInterface = operation.getInterface();
        boolean passByValue = serviceInterface.isRemotable() && !implementation.isAllowsPassByReference(operation);


        Invoker invoker = new OSGiTargetInvoker(operation, runtimeComponent, service);
        if (passByValue) {
            return new OSGiPassByValueInvoker(dataBindingRegistry, operation, runtimeComponent, service);
        } else {
            return invoker;
        }
        
    }
    
    public Invoker createCallbackInvoker(Operation operation) {
    	return createTargetInvoker(null, operation);
	}

	public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
		return createTargetInvoker(service, operation);
	}

	public void start() {
        
		
		for (Reference ref: implementation.getReferences()) {
			List<RuntimeWire> wireList = null;
			ComponentReference compRef = null;
            for (ComponentReference cRef : runtimeComponent.getReferences()) {
                if (cRef.getName().equals(ref.getName())) {
                    wireList = ((RuntimeComponentReference)cRef).getRuntimeWires();
                    compRef = cRef;
                    break;
                }
            }
                
            if (ref.getMultiplicity() == Multiplicity.ONE_N || ref.getMultiplicity() == Multiplicity.ZERO_N) {
             	for (RuntimeWire wire : wireList) {
                    referenceWires.put(wire, ref);
                	componentReferenceWires.put(wire, compRef);
                }
                
            } else {
                if (wireList == null && ref.getMultiplicity() == Multiplicity.ONE_ONE) {
                    throw new IllegalStateException("Required reference is missing: " + ref.getName());
                }
                if (wireList != null && !wireList.isEmpty()) {
                    RuntimeWire wire = wireList.get(0);
                    referenceWires.put(wire, ref);
                    componentReferenceWires.put(wire, compRef);
                }
                
            }
			
		}
        
        processProperties(implementation.getProperties(), componentProperties);
		
	}
    
    
	public void stop() {
        bundleContext.removeServiceListener(osgiServiceListener);
	}

    
    private class OSGiServiceListener implements ServiceListener {
    	
    	private Bundle bundle;
    	
    	OSGiServiceListener(Bundle bundle) {
    		this.bundle = bundle;
    	}
    	
    	public void serviceChanged(org.osgi.framework.ServiceEvent event) {
    		

			org.osgi.framework.ServiceReference reference = event.getServiceReference();
			
			if (event.getType() == ServiceEvent.REGISTERED && reference.getBundle() == bundle) {
				
			    synchronized (implementation) {
			    	
					implementation.notifyAll();
			    }
			}
			
            if (event.getType() == ServiceEvent.UNREGISTERING && reference.getBundle() == bundle) {
            	// TODO: Process deregistering of OSGi services.
            }
		}
    }
}
