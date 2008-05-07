package org.apache.tuscany.sca.test.util;


import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.util.HashSet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * Load Tuscany into an OSGi runtime
 *
 */
public class TuscanyLoader {
    
    public enum TuscanyRuntimeBundleType {
        ITEST_4_BUNDLES,
        MODULE_BUNDLES
    };
    
    public enum Tuscany3rdPartyBundleType {
        ITEST_SINGLE_BUNDLE,
        VIRTUAL_BUNDLES
    };
    
    private static final String scaApiDir = "../sca-api";
    private static final String tuscanySpiDir = "../tuscany-spi";
    private static final String tuscanyRuntimeDir = "../tuscany-runtime";
    private static final String tuscanyExtensionsDir = "../tuscany-extensions";
    private static final String thirdPartyDir = "../tuscany-3rdparty";
    
    private static final String tuscanyManifestDir = "../tuscany-manifest";
    private static final String tuscanyManifestJar = "tuscany-sca-manifest.jar";
    
    private static final String tuscanyModulesDir = "../../../modules";
    private static final String[] tuscanyModulesToIgnore = {
        "node2-api",
        "node2-impl",
        "node2-launcher",
        "node2-launcher-webapp",
        "implementation-node-runtime", // uses node2
        "saxon",
        "runtime",
        "runtime-webapp",
        "runtime-tomcat",
        "runtime-war",
        "host-webapp",
        "host-tomcat",
        "policy-transaction",
        "implementation-bpel",
        "binding-ejb",
        "implementation-ejb",
        "implementation-ejb-xml",
        
    };
    
    private static final HashSet<String> ignoreTuscanyModules = new HashSet<String>();
    
    static {
        for (String ignoreModule : tuscanyModulesToIgnore) {
            ignoreTuscanyModules.add(ignoreModule);
        }
    }
    
    
    private static String findBundle(String subDirName, final String jarPrefix) throws Exception {
        
        File dir = new File(subDirName + "/target");
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar") && (jarPrefix == null || name.startsWith(jarPrefix));
                }
                
            });
            
            if (files != null && files.length > 0)
                return files[0].toURI().toURL().toString();
        }
        return null;
    }
    
    public static Bundle loadTuscanyIntoOSGi(BundleContext bundleContext) throws Exception {
        return loadTuscanyIntoOSGi(bundleContext, TuscanyRuntimeBundleType.MODULE_BUNDLES, Tuscany3rdPartyBundleType.VIRTUAL_BUNDLES);
    }
    
    public static Bundle loadTuscanyIntoOSGi(BundleContext bundleContext, 
                                             TuscanyRuntimeBundleType runtimeBundleType,
                                             Tuscany3rdPartyBundleType thirdPartyBundleType) 
    throws Exception {
        
        
        if (thirdPartyBundleType == Tuscany3rdPartyBundleType.ITEST_SINGLE_BUNDLE)
            loadCombinedThirdPartyBundle(bundleContext);
        else
            loadVirtualThirdPartyBundles(bundleContext);
        
        if (runtimeBundleType == TuscanyRuntimeBundleType.ITEST_4_BUNDLES)
            return load4BundleTuscanyRuntime(bundleContext);
        else
            return loadTuscanyModules(bundleContext);
        
    }
    
    public static void loadCombinedThirdPartyBundle(BundleContext bundleContext) throws Exception {
        
        String thirdPartyBundleName = findBundle(thirdPartyDir, null);
        Bundle thirdPartyBundle = bundleContext.installBundle(thirdPartyBundleName);        
        thirdPartyBundle.start();
    }
    

    public static void loadVirtualThirdPartyBundles(BundleContext bundleContext) throws Exception {
        
        String tuscanyManifestBundleName = new File(tuscanyManifestDir + "/target/" + tuscanyManifestJar).toURI().toURL().toString();
        Bundle tuscanyManifestBundle = bundleContext.installBundle(tuscanyManifestBundleName);        
        tuscanyManifestBundle.start();
    }
    
    
    /**
     * Load Tuscany module bundles
     * 
     * @param bundleContext
     */
    public static Bundle loadTuscanyModules(BundleContext bundleContext) throws Exception {
       
            
        Bundle tuscanyRuntimeBundle = null;
        
        File dir = new File(tuscanyModulesDir);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return true;
                }
                
            });
            
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (ignoreTuscanyModules.contains(file.getName()))
                        continue;
                    String bundleURL = findBundle(file.toString(), "tuscany");
                    if (bundleURL != null) {
                        Bundle bundle = bundleContext.installBundle(bundleURL);
                        if (bundle != null && file.getName().equals("osgi-runtime")) {
                            tuscanyRuntimeBundle = bundle;
                        }
                    }
                }
            }
        }
        
        return tuscanyRuntimeBundle;
    
    }
    
    

    /**
     * Load four Tuscany bundles (API, Core-SPI, Runtime, Extensions) 
     * 
     * @param bundleContext
     */
    public static Bundle load4BundleTuscanyRuntime(BundleContext bundleContext) throws Exception {
        

        String scaApiBundleName = findBundle(scaApiDir, null);
        Bundle scaApiBundle = bundleContext.installBundle(scaApiBundleName);            
        scaApiBundle.start();            

        String tuscanySpiBundleName = findBundle(tuscanySpiDir, null);
        Bundle tuscanySpiBundle = bundleContext.installBundle(tuscanySpiBundleName);    
        
        String tuscanyRuntimeBundleName = findBundle(tuscanyRuntimeDir, null);
        Bundle tuscanyRuntimeBundle = bundleContext.installBundle(tuscanyRuntimeBundleName);
        
        String tuscanyExtensionsBundleName = findBundle(tuscanyExtensionsDir, null);
        Bundle tuscanyExtensionsBundle = bundleContext.installBundle(tuscanyExtensionsBundleName);
        

        tuscanySpiBundle.start();      
        tuscanyExtensionsBundle.start();    
        
        return tuscanyRuntimeBundle;
    
    }
    
    public static void startTuscany(Bundle tuscanyRuntimeBundle) throws BundleException {
        if (tuscanyRuntimeBundle != null) {
            tuscanyRuntimeBundle.start();
            
            // Tuscany runtime is started on a different thread when previously cached bundle is used.
            // Set this thread's TCCL to the one used by the runtime.
            try {
                Class<?> runtimeClass = tuscanyRuntimeBundle.loadClass("org.apache.tuscany.sca.osgi.runtime.OSGiRuntime");
                Method getRuntimeMethod = runtimeClass.getMethod("findRuntime");
                Object runtime = getRuntimeMethod.invoke(runtimeClass);
                Method getTCCLMethod = runtimeClass.getMethod("getContextClassLoader");
                ClassLoader runtimeTCCL = (ClassLoader) getTCCLMethod.invoke(runtime);
                Thread.currentThread().setContextClassLoader(runtimeTCCL);
                
                
            } catch (Throwable e) {
            }
        }
    }
    
}
