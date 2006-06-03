package org.apache.tuscany.core.component;

/**
 * Implementations enable lazy retrieval of a scope id associated with a request, i.e. an id (and presumably a context) do not
 * have to be generated if the scope is never accessed. Identifiers are associated with the current request thread and keyed on
 * scope type.
 *
 * @version $Rev: 395110 $ $Date: 2006-04-18 19:32:30 -0700 (Tue, 18 Apr 2006) $
 * @see org.apache.tuscany.spi.context.WorkContext
 */
public interface ScopeIdentifier {

    /**
     * Returns the scope id for the request.
     */
    public Object getIdentifier();
}
