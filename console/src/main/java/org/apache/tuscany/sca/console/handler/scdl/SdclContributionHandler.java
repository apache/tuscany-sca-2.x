package org.apache.tuscany.sca.console.handler.scdl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tuscany.host.deployment.AssemblyService;
import org.apache.tuscany.host.deployment.DeploymentException;
import org.apache.tuscany.spi.host.ServletHost;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

@SuppressWarnings("serial")
public class SdclContributionHandler extends TuscanyServlet {
    
    // SCDL query parameter
    private static final String SCDL_PARAM = "scdl";
    
    // Assembly service to use
    private AssemblyService assemblyService;
    
    /**
     * Injects the servlet host and path mapping.
     * 
     * @param servletHost Servlet host to use.
     * @param path Path mapping for the servlet.
     */
    public SdclContributionHandler(@Reference(name = "servletHost") ServletHost servletHost, 
                                   @Property(name = "path") String path,
                                   @Reference(name = "assemblyService") AssemblyService assemblyService) {
        super(servletHost, path);
        this.assemblyService = assemblyService;
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

        try {
            String scdl = req.getParameter(SCDL_PARAM);
            InputStream in = new ByteArrayInputStream(scdl.getBytes());
            assemblyService.include(in);
            PrintWriter writer = res.getWriter();
            writer.println("Processed SCDL ");
            writer.flush();
            writer.close();
        } catch(DeploymentException ex) {
            throw new ServletException(ex);
        }
        
    }

}
