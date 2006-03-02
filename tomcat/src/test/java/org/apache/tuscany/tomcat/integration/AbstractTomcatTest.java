package org.apache.tuscany.tomcat.integration;

import junit.framework.TestCase;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardEngine;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: jboynes
 * Date: Mar 2, 2006
 * Time: 12:20:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractTomcatTest extends TestCase {
    protected Host host;
    protected Request request;
    protected Response response;
    protected StandardEngine engine;

    protected void setupTomcat(File baseDir, Host host) throws Exception {
        File appBase = new File(baseDir, "webapps").getCanonicalFile();

        // Configure a Tomcat Engine
        engine = new StandardEngine();
        engine.setName("Catalina");
        engine.setDefaultHost("localhost");
        engine.setBaseDir(baseDir.getAbsolutePath());

        this.host = host;
        host.setName("localhost");
        host.setAppBase(appBase.getAbsolutePath());
        engine.addChild(host);

        // build a empty request/response
        Connector connector = new Connector("HTTP/1.1");
        request = connector.createRequest();
        org.apache.coyote.Request coyoteRequest = new org.apache.coyote.Request();
        request.setCoyoteRequest(coyoteRequest);
        response = connector.createResponse();
        org.apache.coyote.Response coyoteResponse = new org.apache.coyote.Response();
        response.setCoyoteResponse(coyoteResponse);
    }
}
