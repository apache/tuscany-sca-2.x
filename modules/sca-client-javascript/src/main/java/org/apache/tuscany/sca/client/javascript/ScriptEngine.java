/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.tuscany.sca.client.javascript;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.client.rmi.SCAClientFactoryImpl;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.oasisopen.sca.NoSuchDomainException;

import sun.org.mozilla.javascript.internal.Context;
import sun.org.mozilla.javascript.internal.Function;
import sun.org.mozilla.javascript.internal.Scriptable;
import sun.org.mozilla.javascript.internal.ScriptableObject;
import sun.org.mozilla.javascript.internal.WrappedException;
/**
 * 
 * This class implements a Scripting Engine for the Tuscany runitme. This engine
 * uses the Rhino javaScript Engine.
 * 
 * <p> The default script functions supported by this engine are the following:
 * <ul>
 *   <li>echo</li>
 *   <li>version</li>
 *   <li>load</li>
 *   <li>help</li>
 *   <li>exit</li>
 *   <li>printStackTrace</li>
 *   <li>loadModule</li>
 *   <li>listComponents</li>
 *   <li>getComponent</li>
 *   <li>listServices</li>
 *   <li>getService</li>
 *  </ul>
 */
public class ScriptEngine extends ScriptableObject implements Runnable{
	private static final long serialVersionUID = 1L;

	/**
	 * The context with which this script engine will be run
	 */
	private Context jsContext = null;
    
	/**
	 * Set of parameters passes to the script engine
	 */
	private String[] params = null;
	
	/**
	 * instance of Tuscany runtime
	 */
	private SCAClientFactoryImpl tuscany;
	
	/**
	 * A reference to the old class loader
	 */
	private ClassLoader oldClassLoader = null;
	
	/**
	 * Flag to indicate, if the shoutdown has requested by the user
	 */
	private boolean shutdown;
	
	/**
	 * The current output stream 
	 */
	private static PrintStream out = System.out;

	/**
	 * The current error output stream 
	 */
	private PrintStream err = System.err;
	
	/**
	 * The current input stream 
	 */
	private InputStream in = System.in;
	
	/**
	 * The prompt used
	 */
	private String prompt = "js> ";
	
	/**
	 * The last exception that occured, used for debugging
	 */
	private Throwable lastException = null;
	
	/**
	 * Names of the global functions particular to the shell. Note that these functions 
	 * are not part of ECMA.
	 */
	public static final String[] SCRIPT_FUNCTIONS = { 
			"echo", 
			"version", 
			"load", 
			"help",
			"exit", 
			"printStackTrace",
			"loadModule",
			"listComponents",
			"getComponent",
			"listServices",
			"getService", };
	
	/**
	 * get the class name
	 * @return "global" 
	 */
	@Override
	public String getClassName() {
        return "global";
    }

	/**
	 * Construct a script engine
	 *
	 * @param command line arguments
	 */
	public ScriptEngine(String[] params) {
		this.params = params;
	}
	
	/**
	 * Run the script engine. Invoking this method will initialize the script engine,
	 * Tuscany Runtime and load any SCA modules if found in the classpath.
	 *
	 */
	public void run() {
		// Associate a new Context with this thread
        jsContext = Context.enter();
        try {
            // initialize standard objects
            jsContext.initStandardObjects(this);
            // define supported script functions
            this.defineFunctionProperties(SCRIPT_FUNCTIONS, 
            		ScriptEngine.class, 
            		ScriptableObject.DONTENUM);

            // init the tuscany runtime
            this.initTuscanyRuntime();
            // initialize the script engine
            this.startScriptEngine();
        } 
        finally {
//            tuscany.stop();
            Thread.currentThread().setContextClassLoader(oldClassLoader);
            Context.exit();
        }
	}

    /**
     * Print the help message.
     *
     * This method is defined as a script function.
     */
    public void help() {
    	final String helpMsg = 
    		"Command                Description \n" +
    		"=======                =========== \n" +
    		"help()                 Display this message. \n" + 
    		"load(['f1.js', ...])   Load and execute javaScript source files named f1.js, etc. \n" +
    		"echo([expr ...])       Evaluate and print a variable or an expressions. \n" +
    		"exit()                 Exit this shell. \n" +
    		"version([number])      Get or set the javaScript version. \n" +
    		"printStackTrace()      Print the stacktrace of the last exception \n" +
    		"loadModule(jar|war)    Not yet implemented. \n" +
    		"listComponents()       Not yet implemented. \n" +
    		"getComponent('name')   Get an instance of the component with the given name. \n" +
    		"listServices()         Not yet implemented. \n" +
    		"getService('name')     Not yet implemented. \n";
    	
    	out.println();
    	out.println(helpMsg);
    }

    /**
     * Load a given module with the tuscany runtime
     * 
     * This method is defined as a script function.
     * 
     * @param moduleName absolute path of the module
     */
    public void loadModule(final String moduleName) {
        try {
            File file = new File(moduleName);
            URL[] urls = new URL[] {file.toURL()};
            final ClassLoader newCL = new URLClassLoader(urls);
            Thread.currentThread().setContextClassLoader(newCL);
        } catch (Exception e) {
                err.println(e.getMessage());
                lastException = e;
        }
    	// TODO complete this method
    	/*
    	ModuleContext context = CurrentModuleContext.getContext();
    	out.println(context.toString());
    	tuscany.stop();
    	
    	try {
	    	File file = new File(moduleName);
	    	URL[] urls = new URL[] {file.toURL(), };
	    	final ClassLoader newCL = new URLClassLoader(urls);
	    	Thread.currentThread().setContextClassLoader(newCL);
	    	
	    	//Class c = Class.forName("customerinfo.CustomerInfoClient", true, newCL);
	    	//out.println("c = " + c);
	    	
	    	tuscany = new TuscanyRuntime(moduleName, null);
	        tuscany.start();
	        
	        context = CurrentModuleContext.getContext();
	    	out.println(context.toString());
	    	
	    	//Object component = context.locateService("CustomerInfoServiceComponent");
	    	//out.println(component);
    	}
    	catch (Exception e) {
    		err.println(e.getMessage());
    		lastException = e;
    	}
    	*/
    }
    
    /**
     * List the components available within the current module
     * 
     * This method is defined as a script function. 
     */
    public void listComponents() {
    	// TODO complete this method
    }
    
    /**
     * Get the component available within the current module with a 
     * given name
     * 
     * This method is defined as a script function.
     * 
     * @param name name of the component
     * @return service component object represented by the given name 
     */
    public Object getComponent(String name) {
//        ModuleContext tuscanyContext = CurrentModuleContext.getContext();
//        Object component = tuscanyContext.locateService(name);
        
//        return component;
        return null;
    }
    
    /**
     * List the external services available within the current module
     * 
     * This method is defined as a script function. 
     */
    public void listServices() {
        // FIXME
//        EndpointRegistry epr = tuscany.getEndpointRegistry();
//        for (Endpoint e : epr.getEndpoints()) {
//            out.println(e.getURI());
//        }
    }
    
    public Object getService(String serviceName) {
        // FIXME
//        ExtensionPointRegistry extensionsRegistry = tuscany.getExtensionsRegistry();
//        FactoryExtensionPoint factories = extensionsRegistry.getExtensionPoint(FactoryExtensionPoint.class);
//        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
//
//        EndpointReference endpointReference = assemblyFactory.createEndpointReference();
//        endpointReference.setReference(assemblyFactory.createComponentReference());
//        Endpoint targetEndpoint = assemblyFactory.createEndpoint();
//        targetEndpoint.setURI(serviceName);
//        endpointReference.setTargetEndpoint(targetEndpoint);
//        EndpointRegistry epr = tuscany.getEndpointRegistry();
//        List<Endpoint> er = epr.findEndpoint(endpointReference);
//        if (er.size() < 1) {
//            err.println("service not found " + serviceName);
//            return null;
//        }
//        JavaInterface ifac = (JavaInterface)er.get(0).getComponentServiceInterfaceContract().getInterface();
//        return getService(serviceName, ifac.getName());
        return null;
    }

    /**
     * Get an external service defined within the current module with a given name
     * 
     * This method is defined as a script function.
     * 
     * @param name name of the service
     * @return service object represented by the given name 
     */
    protected Object getService(String serviceName, String type) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?> serviceInterface = cl.loadClass(type);
            Object service = tuscany.getService(serviceInterface, serviceName);
            printService(serviceInterface);
            return service;
        } catch (Exception e) {
            err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            lastException = e;
            return null;
        }
    }

    private void printService(Class<?> serviceInterface) {
        out.println(serviceInterface.getName());
        for (Method m  : serviceInterface.getMethods()) {
            out.print("   " + m.getReturnType().getName() + " " + m.getName() + "(");
            boolean one = false;
            for (Class arg  : m.getParameterTypes()) {
                if (one) {
                    out.print(", ");
                }
                out.print(arg.getName());
                one = true;
            }
            out.println(")");
        }
    }
    
    /**
     * Echo a variable or a message.
     *
     * This method is defined as a script function.
     *
     * @param jsContext
     * @param thisObj
     * @param args
     * @param function
     */
    public static void echo(Context jsContext, Scriptable thisObj, Object[] args, Function function)
    {
    	if (args.length > 0) {
	    	out.println(args[0]);
	        for (int i=1; i < args.length; i++) {
	            out.print(" " + Context.toString(args[i]));
	        }
    	}
        out.println();
    }

    /**
     * print the stacktrace of the last exception
     *
     * This method is defined as a script function.
     */
    public void printStackTrace() {
    	if (lastException == null) {
    		out.println("No stacktrace available");
    	}
    	else {
    		err.println(getStackTrace(lastException));
    	}
    }
    
    /**
     * Exit the shell when it is in the interactive mode.
     *
     * This method is defined as a script function.
     */
    public void exit(){
        shutdown = true;
    }

    /**
     * Get and set the language version.
     *
     * This method is defined as a JavaScript function.
     * 
     * @param jsContext
     * @param thisObj
     * @param args
     * @param function
     */
    public static double version(Context jsContext, Scriptable thisObj, Object[] args, Function function)
    {
        if (args.length > 0) {
            int i = (int)Context.toNumber(args[0]);
            jsContext.setLanguageVersion(i);
        }
        return (double) jsContext.getLanguageVersion();
    }

    /**
     * Load and execute a set of JavaScript source files.
     *
     * This method is defined as a JavaScript function.
     *
     * @param jsContext
     * @param thisObj
     * @param args
     * @param function
     */
    public static void load(Context jsContext, Scriptable thisObj, Object[] args, Function function)
    {
    	ScriptEngine engine = (ScriptEngine)getTopLevelScope(thisObj);
        for (int i = 0; i < args.length; i++) {
           engine.processFile(Context.toString(args[i]));
        }
    }

    /**
     * Get the current output stream
     * 
     * @return current output stream
     */
	public PrintStream getOut() {
		return out;
	}

	/**
	 * Set the current output stream
	 * @param out the new output stream
	 */
	public void setOut(PrintStream out) {
		ScriptEngine.out = out;
	}

	/**
	 * Get the current error output stream
	 * 
	 * @return Returns the current error output stream.
	 */
	public PrintStream getErr() {
		return err;
	}

	/**
	 * Set the current error output stream
	 * @param err The new error output stream to set.
	 */
	public void setErr(PrintStream err) {
		this.err = err;
	}

	/**
	 * Get the current input stream
	 * 
	 * @return Returns the current input stream.
	 */
	public InputStream getIn() {
		return in;
	}

	/**
	 * Set the current input stream
	 * @param in The new input stream to set.
	 */
	public void setIn(InputStream in) {
		this.in = in;
	}

	/**
	 * initialize the Tuscany runtime
	 *
	 */
	protected void initTuscanyRuntime() {
		
        // Required to allow the SDO runtime to use the correct classloader
        oldClassLoader = Thread.currentThread().getContextClassLoader();
//        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        try {
            tuscany = new SCAClientFactoryImpl(URI.create("tuscany:foo"));
        } catch (NoSuchDomainException e) {
            throw new RuntimeException(e);
        }        
        
//        // Obtain and start the Tuscany runtime
//        try {
//	        tuscany = new TuscanyRuntime("TuscanyClient", null);
//	        tuscany.start();
//	        	        
//	        // this will get the module name; either specified by user or from the classpath
//	        String moduleName = getCurrentModuleName();
//	        // TODO how do we get all the components, services exposed by this module ? 
//	        
//        }
//        catch (ConfigurationException ce) {
//        	err.println("Failed to start Tuscany runtime: " + ce.getMessage());
//        	//ce.printStackTrace(err);
//        	lastException = ce;
//        }
	}


	/**
	 * start the script engine
	 *
	 */
	protected void startScriptEngine() {
		
		out.println("Tuscany SCA Shell 0.1, type help() for a list of supported functions.");
		
		// set up "arguments" in the global scope 
         if (params.length > 1) {
            int length = params.length - 1;
            Object obj[] = new Object[length];
            System.arraycopy(params, 1, obj, 0, length);
            
            Scriptable argsObj = jsContext.newArray(this, obj);
            this.defineProperty("arguments", argsObj, ScriptableObject.DONTENUM);
        }

        if (params.length == 0) {
        	// no file name specified, go to interactive mode
        	this.startInteractiveMode();
        }
        else {
        	// TODO this could be a jar file or a script file
        	// process the specified file
        	this.processFile(params[0]);
        }
        System.gc();
	}
	


    /**
     * Evaluate JavaScript source in the interactive mode.
     *
     */
    private void startInteractiveMode()
    {
    	// see if org.mozilla.javascript.tools.shell.Main.processSource() can handle this
    	//Main.setErr(this.getErr());
    	//Main.setOut(this.getOut());
    	//Main.setIn(this.getIn());
    	//Main.processSource(jsContext, filename);
    	
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        int currentLine = 1;
        boolean done = false;
        while (!shutdown && !done) {
            out.print(prompt);
            out.flush();
            int lineToCompile = currentLine;
            try {
                StringBuffer source = new StringBuffer();
                // read the next line entered by user
                boolean compilable = false;
                while(!compilable) {
                    String nextLine = reader.readLine();
                    if (nextLine != null) {
                        currentLine += 1;
                    	source.append(nextLine).append("\n");
                    	compilable = jsContext.stringIsCompilableUnit(source.toString());
                    }
                    else {
                    	// TODO check if this can handle Ctrl+C
                    	System.out.println("=== Ctrl+C pressed ==");
                    	// break both the loops
                    	done = true;
                    	compilable = true;
                    }
                }
                Object result = jsContext.evaluateString(this, source.toString(), "<stdin>", lineToCompile, null);
                if (result != Context.getUndefinedValue()) {
                    out.println(Context.toString(result));
                }
            }
            catch (Exception e) {
            	if (e instanceof WrappedException) {
            		WrappedException we = (WrappedException)e;
            		err.println(we.getWrappedException().getMessage());
            	}
            	err.println(e.getMessage());
            	//e.printStackTrace(err);
            	lastException = e;
            }
        }
        out.println();
    }

    /**
     * Read the contents from a script file and execute it.
     *
     * @param filename the name of the file to compile
     */
    private void processFile(String fileName) {
    	BufferedReader reader = null;
        try {
        	reader = new BufferedReader(new FileReader(fileName));
        	jsContext.evaluateReader(this, reader, fileName, 1, null);
        }
        catch (Exception e) {
        	if (e instanceof WrappedException) {
        		WrappedException we = (WrappedException)e;
        		err.println(we.getWrappedException().getMessage());
        	}
        	err.println(e.getLocalizedMessage());
        	//e.printStackTrace(err);
        	lastException = e;
        }
        finally {
        	try {
        		reader.close();
            }
            catch (IOException ioe) {
            	err.println(ioe.getLocalizedMessage());
                //ioe.printStackTrace(err);
            	lastException = ioe;
            }
        }
    }

	/**
	 * Find the SCA module name. If the user have specified one, use that,
	 * otherwise, if there is a module definition in the path, pick it up. 
	 * 
	 * @return name of the module
	 */
	private String getCurrentModuleName() {
		String moduleName = "";
		
		// TODO parse the arguments to see if there is an SCA module name
		return moduleName;
	}
	
	/**
	 * Get the stacktrace for a give exception
	 * 
	 * @param t Throwable from which the stacktrace need to be obtained
	 * @return char array containing the stack trace
	 */
	private char[] getStackTrace(Throwable t) {
		CharArrayWriter charWriter = new CharArrayWriter(2048);
		PrintWriter writer = new PrintWriter(charWriter);
		t.printStackTrace(writer);

		return charWriter.toCharArray();
	}
}
