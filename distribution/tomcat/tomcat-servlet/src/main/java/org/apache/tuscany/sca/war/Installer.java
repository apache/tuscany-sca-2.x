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

package org.apache.tuscany.sca.war;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.tuscany.sca.tomcat.TuscanyLifecycleListener;
import org.codehaus.swizzle.stream.DelimitedTokenReplacementInputStream;
import org.codehaus.swizzle.stream.StringTokenHandler;

public class Installer {

    private static boolean restartRequired;
    private static boolean tuscanyHookRunning;
    static {
        try {
            tuscanyHookRunning = TuscanyLifecycleListener.isRunning();
        } catch (Throwable e) {
            tuscanyHookRunning = false;
        }
    }

    public static boolean isTuscanyHookRunning() {
        return tuscanyHookRunning;
    }

    public static boolean isRestartRequired() {
        return restartRequired;
    }

    private File tuscanyWAR;
    private File catalinaBase;
    private String status = "";

    public Installer(File tuscanyWAR, File catalinaBase) {
        this.tuscanyWAR = tuscanyWAR;
        this.catalinaBase = catalinaBase;
    }

    public static boolean isInstalled() {
        return false;
    }

    public String getStatus() {
        return status;
    }

    public boolean install() {
        try {

            doInstall();
            status = "Install successful, Tomcat restart required.";
            restartRequired = true;
            return true;

        } catch (Throwable e) {
            status = "Exception during install\n";
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(os);
            e.printStackTrace(pw);
            pw.close();
            status += new String(os.toByteArray());
            return false;
        }
    }

    public boolean uninstall() {
        try {

            doUnintsall();
            status = "Tuscany removed from server.xml, please restart Tomcat and manually remove Tuscany jars from Tomcat lib";
            restartRequired = true;
            return true;

        } catch (Throwable e) {
            status = "Exception during install";
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(os);
            e.printStackTrace(pw);
            status += "/n" + new String(os.toByteArray());
            return false;
        }
    }

    private void doUnintsall() {
        File serverXml = new File(catalinaBase, "/conf/server.xml");
        if (!(serverXml.exists())) {
            throw new IllegalStateException("conf/server.xml not found: " + serverXml.getAbsolutePath());
        }
        removeServerXml(serverXml);
        removeHostConfigXml(serverXml);
        removeContextXml();
    }

    private boolean doInstall() {
        // First verify all the file locations are as expected
        if (!tuscanyWAR.exists()) {
            throw new IllegalStateException("Tuscany war missing: " + tuscanyWAR.getAbsolutePath());
        }
        if (!catalinaBase.exists()) {
            throw new IllegalStateException("Catalina base does not exist: " + catalinaBase.getAbsolutePath());
        }
        File serverLib = new File(catalinaBase, "/lib");
        if (!(serverLib.exists())) {
            // try Tomcat 5 server/lib
            if (new File(catalinaBase, "/server").exists()) {
                serverLib = new File(new File(catalinaBase, "/server"), "/lib");
            }
        }
        if (!(serverLib.exists())) {
            throw new IllegalStateException("Tomcat lib not found: " + serverLib.getAbsolutePath());
        }
        File serverXml = new File(catalinaBase, "/conf/server.xml");
        if (!(serverXml.exists())) {
            throw new IllegalStateException("conf/server.xml not found: " + serverXml.getAbsolutePath());
        }

        File tuscanyTomcatJar = findTuscanyTomcatJar(tuscanyWAR);
        if (tuscanyTomcatJar == null || !tuscanyTomcatJar.exists()) {
            throw new IllegalStateException("Can't find tuscany-tomcat-*.jar in: " + tuscanyWAR.getAbsolutePath());
        }

        // Copy tuscany-tomcat jar from the tuscany webapp web-inf/lib to Tomcat server/lib
        copyFile(tuscanyTomcatJar, new File(serverLib, tuscanyTomcatJar.getName()));

        // Add Tuscany LifecycleListener to Tomcat server.xml
        updateServerXml(serverXml);

        // Add Tuscany HostConfig to Hosts definitions in server.xml
        updateHostConfigXml(serverXml);

        // Add Tuscany specific default web.xml and context.xml definitions
        addTuscanyWebXml();
        addTuscanyContextXml();
        
        return true;
    }

    private static final String tuscanyWebXML =
        "\r\n\r\n" + "  <!-- Tuscany Listener and Filter definitions -->\r\n" + 
        "  <listener>\r\n" + 
        "     <listener-class>org.apache.tuscany.sca.host.webapp.TuscanyContextListener</listener-class>\r\n" +
        "  </listener>\r\n" + 
        "\r\n" +
        "  <filter>\r\n" + 
        "     <filter-name>tuscany</filter-name>\r\n" + 
        "     <filter-class>org.apache.tuscany.sca.host.webapp.TuscanyServletFilter</filter-class>\r\n" +  
        "  </filter>\r\n" + 
        "\r\n" +
        "  <filter-mapping>\r\n" + 
        "     <filter-name>tuscany</filter-name>\r\n" +  
        "     <url-pattern>/*</url-pattern>\r\n" + 
        "  </filter-mapping>";

    private void addTuscanyWebXml() {
        File tuscanyWebXmlFile = new File(catalinaBase, "/conf/tuscany-web.xml");
        if (!(tuscanyWebXmlFile.exists())) {
            File webXmlFile = new File(catalinaBase, "/conf/web.xml");
            if (!(webXmlFile.exists())) {
                throw new IllegalStateException("conf/web.xml not found: " + webXmlFile.getAbsolutePath());
            }
            String webXML = readAll(webXmlFile);
            String newWebXml = replace(webXML, "<web-app", "<web-app", ">", ">" + tuscanyWebXML);
            writeAll(tuscanyWebXmlFile, newWebXml);
        }
    }
    
    private static final String tuscanyContextXML =
        "\r\n\r\n    <!-- The Tuscany SCA default domain URI.\r\n" + 
        "    Individual contributions may used different domains by having their \r\n" +
        "    context.xml files overriding this parameter. -->\r\n" +
        "    <Parameter name=\"org.apache.tuscany.sca.defaultDomainURI\" value=\"vm:default\"/>";

    private void addTuscanyContextXml() {
        File contextXmlFile = new File(catalinaBase, "/conf/context.xml");
        if ((contextXmlFile.exists())) {
            String contextXML = readAll(contextXmlFile);
            String newcontextXml = replace(contextXML, "<Context>", "<Context>" + tuscanyContextXML, "<", "<");
            backup(contextXmlFile);
            writeAll(contextXmlFile, newcontextXml);
        }
    }
    private void removeContextXml() {
        File contextXmlFile = new File(catalinaBase, "/conf/context.xml");
        if ((contextXmlFile.exists())) {
            String contextXML = readAll(contextXmlFile);
            String oldContextXml = replace(contextXML, "<Context>" + tuscanyContextXML, "<Context>",  "<", "<");
            writeAll(contextXmlFile, oldContextXml);
        }
    }

    private File findTuscanyTomcatJar(File tuscanyWAR) {
        File lib = new File(tuscanyWAR, "/tomcat-lib");
        for (File f : lib.listFiles()) {
            if (f.getName().startsWith("tuscany-tomcat-hook-") && f.getName().endsWith(".jar")) {
                return f;
            }
        }
        return null;
    }

    private static final String tuscanyListener =
        "\r\n" + "  <!-- Tuscany plugin for Tomcat -->\r\n"
            + "  <Listener className=\"org.apache.tuscany.sca.tomcat.TuscanyLifecycleListener\" />";

    private void updateServerXml(File serverXmlFile) {
        String serverXML = readAll(serverXmlFile);
        if (!serverXML.contains(tuscanyListener)) {
            String newServerXml = replace(serverXML, "<Server", "<Server", ">", ">" + tuscanyListener);
            backup(serverXmlFile);
            writeAll(serverXmlFile, newServerXml);
        }
    }

    private void removeServerXml(File serverXmlFile) {
        String serverXML = readAll(serverXmlFile);
        if (serverXML.contains(tuscanyListener)) {
            String newServerXml = replace(serverXML, "<Server", "<Server", ">" + tuscanyListener, ">");
            writeAll(serverXmlFile, newServerXml);
        }
    }

    static final String tuscanyHostConfig = " hostConfigClass=\"org.apache.tuscany.sca.tomcat.TuscanyHostConfig\" >";

    private void updateHostConfigXml(File serverXmlFile) {
        String serverXML = readAll(serverXmlFile);
        String newServerXml = replace(serverXML, "<Host", "<Host", ">", tuscanyHostConfig);
        backup(serverXmlFile);
        writeAll(serverXmlFile, newServerXml);
    }

    private void removeHostConfigXml(File serverXmlFile) {
        String serverXML = readAll(serverXmlFile);
        if (serverXML.contains(tuscanyHostConfig)) {
            String newServerXml = replace(serverXML, "<Host", "<Host", tuscanyHostConfig, ">");
            writeAll(serverXmlFile, newServerXml);
        }
    }

    private String replace(String inputText, String begin, String newBegin, String end, String newEnd) {
        BeginEndTokenHandler tokenHandler = new BeginEndTokenHandler(newBegin, newEnd);

        ByteArrayInputStream in = new ByteArrayInputStream(inputText.getBytes());

        InputStream replacementStream = new DelimitedTokenReplacementInputStream(in, begin, end, tokenHandler, true);
        String newServerXml = readAll(replacementStream);
        close(replacementStream);
        return newServerXml;
    }

    private boolean backup(File source) {
        File backupFile = new File(source.getParent(), source.getName() + ".b4Tuscany");
        if (!backupFile.exists()) {
            copyFile(source, backupFile);
        }
        return true;
    }

    private String readAll(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            String text = readAll(in);
            return text;
        } catch (Exception e) {
            return null;
        } finally {
            close(in);
        }
    }

    private String readAll(InputStream in) {
        try {
            // SwizzleStream block read methods are broken so read byte at a time
            StringBuilder sb = new StringBuilder();
            int i = in.read();
            while (i != -1) {
                sb.append((char)i);
                i = in.read();
            }
            return sb.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void copyFile(File source, File destination) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(destination);
            writeAll(in, out);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            close(in);
            close(out);
        }
    }

    private boolean writeAll(File file, String text) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            writeAll(new ByteArrayInputStream(text.getBytes()), fileOutputStream);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            close(fileOutputStream);
        }
    }

    private void writeAll(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int count;
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        out.flush();
    }

    private void close(Closeable thing) {
        if (thing != null) {
            try {
                thing.close();
            } catch (Exception ignored) {
            }
        }
    }

    private class BeginEndTokenHandler extends StringTokenHandler {
        private final String begin;
        private final String end;

        public BeginEndTokenHandler(String begin, String end) {
            this.begin = begin;
            this.end = end;
        }

        public String handleToken(String token) throws IOException {
            String result = begin + token + end;
            return result;
        }
    }

}
