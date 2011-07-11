package org.apache.tuscany.sca.binding.local;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.WireFormat;

/**
 * Represents a Local SCA Binding
 */
public class LocalSCABindingImpl implements LocalSCABinding {
    private String name;
    private String uri;
    private WireFormat wireFormat;
	
	/**
     * Constructs a new Local binding.
     */
	public LocalSCABindingImpl() {
	}
	
	@Override
	public QName getType() {
		return TYPE;
	}
	
	@Override
    public String getName() {
        return name;
    }

	@Override
    public void setName(String name) {
        this.name = name;
    }
	
    /**
     * Getters for the binding URI. The computed URI for the
     * service that the reference is targeting or which the service represents
     * depending on whether the biding is associated with a reference or
     * service
     *
     * @return the binding URI
     */
	@Override
    public String getURI() {
        return uri;
    }

	@Override
    public void setURI(String uri) {
        this.uri = uri;
    }
    
	@Override
    public boolean isUnresolved() {
        return false;
    }

	@Override
    public void setUnresolved(boolean unresolved) {
    }
		
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

	@Override
    public WireFormat getRequestWireFormat() {
        return wireFormat;
    }
    
	@Override
    public void setRequestWireFormat(WireFormat wireFormat) {  
		this.wireFormat = wireFormat;
    }
    
	@Override
    public WireFormat getResponseWireFormat() {
        return wireFormat;
    }
    
	@Override
    public void setResponseWireFormat(WireFormat wireFormat) {
		this.wireFormat = wireFormat;		
    }
    
	@Override
    public OperationSelector getOperationSelector() {
        return null;
    }

    @Override
    public void setOperationSelector(OperationSelector operationSelector) {
    }
}
