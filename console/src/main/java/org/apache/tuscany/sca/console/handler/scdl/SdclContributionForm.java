package org.apache.tuscany.sca.console.handler.scdl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.spi.host.ServletHost;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

@SuppressWarnings("serial")
public class SdclContributionForm extends TuscanyServlet {

    /**
     * Injects the servlet host and path mapping.
     * 
     * @param servletHost Servlet host to use.
     * @param path Path mapping for the servlet.
     */
    public SdclContributionForm(@Reference(name = "servletHost") ServletHost servletHost, 
                                @Property(name = "path") String path) {
        super(servletHost, path);
    }

    /**
     * Processes the request.
     * 
     * @param req Servlet request.
     * @param res Servlet response.
     * @throws ServletException Servlet exception.
     * @throws IOException IO Exception.
     */
    protected void process(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        PrintWriter writer = res.getWriter();
        writer.println("<form name='' action='scdlSubmit'>");
        writer.println("<textarea name='scdl' cols='60' rows='40'></textarea>");
        writer.println("<br/>");
        writer.println("<input type='submit' value='Contribute SCDL'/>");
        writer.println("</form>");
        writer.flush();
        writer.close();

    }

}
