/*
 * (c) Copyright BEA Systems, Inc., Cape Clear Software, International Business Machines Corp, Interface21, IONA Technologies,
 * Oracle, Primeton Technologies, Progress Software, Red Hat, Rogue Wave Software, SAP AG., Siemens AG., Software AG., Sybase
 * Inc., TIBCO Software Inc., 2005, 2007. All rights reserved.
 * 
 * see http://www.osoa.org/display/Main/Service+Component+Architecture+Specifications
 */
package org.osoa.sca;

/**
 * Exception thrown to indicate the conversation being used for a stateful interaction has been ended.
 *
 * @version $Rev$ $Date$
 */
public class ConversationEndedException extends ServiceRuntimeException {
    private static final long serialVersionUID = 3734864942222558406L;

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @see ServiceRuntimeException
     */
    public ConversationEndedException() {
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param message passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public ConversationEndedException(String message) {
        super(message);
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param message passed to ServiceRuntimeException
     * @param cause   passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public ConversationEndedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param cause passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public ConversationEndedException(Throwable cause) {
        super(cause);
    }
}
