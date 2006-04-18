package org.apache.tuscany.tools.java2wsdl.generate;

public interface WSDLGenListener {
	public static int UNKNOWN = 0;

	public static int INPUT_ARGS_PARSING = 1;

	public static int INPUT_ARGS_VALIDATION = 2;

	public static int WSDL_MODEL_CREATION = 3;

	public static int WSDL_MODEL_WRITING = 4;

	public static String[] phaseAsString = { "Unknown",
			"Input Arguments Parsing", "Input Arguments Validation",
			"WSDL Model Creation", "WSDL Model Writing" };

	public void WSDLGenPhaseStarted(WSDLGenEvent event);

	public void WSDLGenPhaseCompleted(WSDLGenEvent event);
}
