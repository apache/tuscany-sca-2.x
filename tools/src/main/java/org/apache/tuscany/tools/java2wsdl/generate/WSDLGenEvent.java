package org.apache.tuscany.tools.java2wsdl.generate;

import java.util.EventObject;

public class WSDLGenEvent extends EventObject {
	private int generationPhase = WSDLGenListener.UNKNOWN;

	public WSDLGenEvent(Object source, int genPhase) {
		super(source);
		this.generationPhase = genPhase;
	}

	public int getGenerationPhase() {
		return generationPhase;
	}

	public void setGenerationPhase(int generationPhase) {
		this.generationPhase = generationPhase;
	}
}
