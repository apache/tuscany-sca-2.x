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

package calculator.dosgi.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.Constants;

import calculator.dosgi.CalculatorService;
import calculator.dosgi.impl.CalculatorActivator;
import calculator.dosgi.impl.CalculatorServiceDSImpl;
import calculator.dosgi.impl.CalculatorServiceImpl;
import calculator.dosgi.operations.AddService;
import calculator.dosgi.operations.DivideService;
import calculator.dosgi.operations.MultiplyService;
import calculator.dosgi.operations.SubtractService;
import calculator.dosgi.operations.impl.AddServiceImpl;
import calculator.dosgi.operations.impl.DivideServiceImpl;
import calculator.dosgi.operations.impl.MultiplyServiceImpl;
import calculator.dosgi.operations.impl.OperationsActivator;
import calculator.dosgi.operations.impl.SubtractServiceImpl;

/**
 *
 * Utility class to create OSGi bundles
 *
 * @version $Rev$ $Date$
 */
public class OSGiTestBundles {
    private static class InvocationHandlerImpl implements InvocationHandler {
        private Object instance;

        public InvocationHandlerImpl(Object instance) {
            super();
            this.instance = instance;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method m = instance.getClass().getMethod(method.getName(), method.getParameterTypes());
            return m.invoke(instance, args);
        }

    }

    private static String getPackageName(Class<?> cls) {
        String name = cls.getName();
        int index = name.lastIndexOf('.');
        return index == -1 ? "" : name.substring(0, index);
    }

    public static URL createBundle(String jarName, String mfFile, String[][] resources, Class<?>... classes)
        throws IOException {
        InputStream is = OSGiTestBundles.class.getClassLoader().getResourceAsStream(mfFile);
        Manifest manifest = new Manifest(is);
        is.close();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JarOutputStream jarOut = new JarOutputStream(out, manifest);

        for (Class<?> cls : classes) {
            addClass(jarOut, cls);
        }

        if (resources != null) {
            for (String resource[] : resources) {
                if (resource.length >= 1) {
                    String r1 = resource[0];
                    String r2 = resource.length > 1 ? resource[1] : r1;
                    addResource(jarOut, OSGiTestBundles.class.getClassLoader(), r1, r2);
                }
            }
        }

        jarOut.close();
        out.close();

        File jar = new File(jarName);
        FileOutputStream fileOut = new FileOutputStream(jar);
        fileOut.write(out.toByteArray());
        fileOut.close();

        return jar.toURI().toURL();
    }

    public static URL createBundle(String jarName,
                                   String bundleName,
                                   String exports,
                                   String imports,
                                   String[] resources,
                                   Class<?>... classes) throws IOException {

        Class<?> activator = null;
        Set<String> packages = new HashSet<String>();
        StringBuffer exportPackages = new StringBuffer();
        if (exports != null) {
            exportPackages.append(exports);
        }
        for (Class<?> cls : classes) {
            if (BundleActivator.class.isAssignableFrom(cls)) {
                activator = cls;
            }
            if (exports == null && cls.isInterface()) {
                String pkg = getPackageName(cls);
                if (packages.add(pkg)) {
                    exportPackages.append(pkg).append(",");
                }
            }
        }
        int len = exportPackages.length();
        if (len > 0 && exportPackages.charAt(len - 1) == ',') {
            exportPackages.deleteCharAt(len - 1);
        }

        Manifest manifest = new Manifest();
        // This attribute Manifest-Version is required so that the MF will be added to the jar
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        manifest.getMainAttributes().putValue(Constants.BUNDLE_MANIFESTVERSION, "2");
        manifest.getMainAttributes().putValue(Constants.BUNDLE_SYMBOLICNAME, bundleName);
        manifest.getMainAttributes().putValue(Constants.BUNDLE_VERSION, "1.0.0");
        manifest.getMainAttributes().putValue(Constants.BUNDLE_NAME, bundleName);
        manifest.getMainAttributes().putValue(Constants.EXPORT_PACKAGE, exportPackages.toString());
        StringBuffer importPackages = new StringBuffer();
        if (imports != null) {
            importPackages.append(imports).append(",org.osgi.framework");
        } else {
            importPackages.append("org.osgi.framework");
        }
        manifest.getMainAttributes().putValue(Constants.IMPORT_PACKAGE, importPackages.toString());

        if (activator != null) {
            manifest.getMainAttributes().putValue(Constants.BUNDLE_ACTIVATOR, activator.getName());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JarOutputStream jarOut = new JarOutputStream(out, manifest);

        for (Class<?> cls : classes) {
            addClass(jarOut, cls);
        }

        if (resources != null) {
            for (String resource : resources) {
                addResource(jarOut, OSGiTestBundles.class.getClassLoader(), resource, null);
            }
        }

        jarOut.close();
        out.close();

        File jar = new File(jarName);
        FileOutputStream fileOut = new FileOutputStream(jar);
        fileOut.write(out.toByteArray());
        fileOut.close();

        return jar.toURI().toURL();
    }

    private static void addClass(JarOutputStream jarOut, Class<?> javaClass) throws IOException, FileNotFoundException {
        String classFile = javaClass.getName().replace('.', '/') + ".class";
        URL url = javaClass.getResource(javaClass.getSimpleName() + ".class");
        addEntry(jarOut, url, classFile);
    }

    private static void addResource(JarOutputStream jarOut, ClassLoader cl, String resourceName, String entryName)
        throws IOException, FileNotFoundException {
        URL url = cl.getResource(resourceName);
        if (entryName == null) {
            entryName = resourceName;
        }
        addEntry(jarOut, url, entryName);
    }

    private static void addEntry(JarOutputStream jarOut, URL url, String resourceName) throws IOException,
        FileNotFoundException {
        String path = url.getPath();

        ZipEntry ze = new ZipEntry(resourceName);

        jarOut.putNextEntry(ze);
        FileInputStream file = new FileInputStream(path);
        byte[] fileContents = new byte[file.available()];
        file.read(fileContents);
        jarOut.write(fileContents);
        jarOut.closeEntry();
    }

    static URL generateCalculatorBundle() throws IOException {
        return createBundle("target/test-classes/calculator-bundle.jar",
                            "calculator/dosgi/META-INF/MANIFEST.MF",
                            new String[][] {
                                            {
                                             "calculator/dosgi/OSGI-INF/remote-service/calculator-service-descriptions.xml",
                                             "OSGI-INF/remote-service/calculator-service-descriptions.xml"},
                                            {"calculator/dosgi/OSGI-INF/calculator-component.xml",
                                             "OSGI-INF/calculator-component.xml"},
                                             /*
                                            {"calculator/dosgi/bundle.componentType",
                                             "OSGI-INF/sca/bundle.componentType"},
                                            {"calculator/dosgi/calculator.composite", "OSGI-INF/sca/bundle.composite"},
                                            {"calculator/dosgi/META-INF/sca-contribution.xml",
                                             "META-INF/sca-contribution.xml"}
                                             */
                                            },
                            CalculatorService.class,
                            // Package the interfaces so that the operations bundle can be remote
                            AddService.class,
                            SubtractService.class,
                            MultiplyService.class,
                            DivideService.class,
                            CalculatorServiceImpl.class,
                            CalculatorServiceDSImpl.class,
                            CalculatorActivator.class);
    }

    /**
     * Create the OSGi bundle for calculator SCA
     * @return
     * @throws IOException
     */
    static URL generateCalculatorSCABundle() throws IOException {
        return createBundle("target/test-classes/calculator-sca-bundle.jar",
                            "calculator/dosgi/sca/META-INF/MANIFEST.MF",
                            new String[][] {
                                            {"calculator/dosgi/sca/OSGI-INF/sca/calculator.composite",
                                             "OSGI-INF/sca/bundle.composite"},
                                            {"calculator/dosgi/sca/META-INF/sca-contribution.xml",
                                             "META-INF/sca-contribution.xml"},
                                             {"calculator/dosgi/bundle.componentType",
                                             "OSGI-INF/sca/calculator.dosgi/bundle.componentType"},
                                             {"calculator/dosgi/operations/bundle.componentType",
                                              "OSGI-INF/sca/calculator.dosgi.operations/bundle.componentType"},
                                             });
    }

    /**
     * Create the OSGi bundle for calculator operations
     * @return
     * @throws IOException
     */
    static URL generateOperationsBundle() throws IOException {
        return createBundle("target/test-classes/operations-bundle.jar",
                            "calculator/dosgi/operations/META-INF/MANIFEST.MF",
                            new String[][] {
                                            {"calculator/dosgi/operations/OSGI-INF/add-component.xml",
                                             "OSGI-INF/add-component.xml"},
                                            {"calculator/dosgi/operations/OSGI-INF/subtract-component.xml",
                                             "OSGI-INF/subtract-component.xml"},
                                            {"calculator/dosgi/operations/OSGI-INF/multiply-component.xml",
                                             "OSGI-INF/multiply-component.xml"},
                                            {"calculator/dosgi/operations/OSGI-INF/divide-component.xml",
                                             "OSGI-INF/divide-component.xml"},
                                             /*
                                            {"calculator/dosgi/operations/bundle.componentType",
                                             "OSGI-INF/sca/bundle.componentType"},
                                            {"calculator/dosgi/operations/operations.composite",
                                             "OSGI-INF/sca/bundle.composite"},
                                            {"calculator/dosgi/operations/META-INF/sca-contribution.xml",
                                             "META-INF/sca-contribution.xml"}
                                             */
                                             },
                            OperationsActivator.class,
                            AddService.class,
                            AddServiceImpl.class,
                            SubtractService.class,
                            SubtractServiceImpl.class,
                            MultiplyService.class,
                            MultiplyServiceImpl.class,
                            DivideService.class,
                            DivideServiceImpl.class);
    }

    /**
     * Returns a string representation of the given bundle.
     *
     * @param b
     * @param verbose
     * @return
     */
    public static String bundleStatus(Bundle bundle, boolean verbose) {
        StringBuffer sb = new StringBuffer();
        sb.append(bundle.getBundleId()).append(" ").append(bundle.getSymbolicName());
        int s = bundle.getState();
        if ((s & Bundle.UNINSTALLED) != 0) {
            sb.append(" UNINSTALLED");
        }
        if ((s & Bundle.INSTALLED) != 0) {
            sb.append(" INSTALLED");
        }
        if ((s & Bundle.RESOLVED) != 0) {
            sb.append(" RESOLVED");
        }
        if ((s & Bundle.STARTING) != 0) {
            sb.append(" STARTING");
        }
        if ((s & Bundle.STOPPING) != 0) {
            sb.append(" STOPPING");
        }
        if ((s & Bundle.ACTIVE) != 0) {
            sb.append(" ACTIVE");
        }

        if (verbose) {
            sb.append(" ").append(bundle.getLocation());
            sb.append(" ").append(bundle.getHeaders());
        }
        return sb.toString();
    }

    /**
     * A utility to cast the object to the given interface. If the class for the object
     * is loaded by a different classloader, a proxy will be created.
     *
     * @param <T>
     * @param obj
     * @param cls
     * @return
     */
    public static <T> T cast(Object obj, Class<T> cls) {
        if (cls.isInstance(obj)) {
            return cls.cast(obj);
        } else {
            return cls.cast(Proxy.newProxyInstance(cls.getClassLoader(),
                                                   new Class<?>[] {cls},
                                                   new InvocationHandlerImpl(obj)));
        }
    }
}
