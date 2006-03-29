package org.apache.tuscany.binding.jsonrpc.handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ScriptGetterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        URL url = getClass().getResource("sca.js");
        InputStream is = url.openStream();
        ServletOutputStream os = response.getOutputStream();
        int i;
        while ((i = is.read()) != -1) {
            os.write(i);
        }
    }

}
