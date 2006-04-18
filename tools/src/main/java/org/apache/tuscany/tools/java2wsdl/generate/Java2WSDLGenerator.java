package org.apache.tuscany.tools.java2wsdl.generate;

import java.io.OutputStream;
import java.util.Map;

/**
 * This is the Java2WSDL Generator facade that will be used by Tuscany
 * components for java to wsdl conversion.
 * 
 */
public interface Java2WSDLGenerator {
	public void generateWSDL(String[] args);

	public void generateWSDL(Map commandLineOptions);

	public void addWSDLGenListener(WSDLGenListener l);

	public void removeWSDLGenListener(WSDLGenListener l);

	public Map getCommandLineOptions();

	public void setCommandLineOptoins(Map cmdLineOpts);

	public WSDLModel getWSDLModel();

	public OutputStream getOutputStream();

	public void setOutputStream(OutputStream outStream);

}
