package org.apache.tuscany.core.component;

/**
 * Implementations enable lazy retrieval of a scope id associated with a request, i.e. an id (and presumably a context)
 * do not have to be generated if the scope is never accessed. Identifiers are associated with the current request
 * thread and keyed on scope type.
 *
 * @version $Rev: 415032 $ $Date: 2006-06-17 10:28:07 -0700 (Sat, 17 Jun 2006) $
 * @see org.apache.tuscany.spi.component.WorkContext
 */
public interface ScopeIdentifier {

    /**
     * Returns the scope id for the request.
     */
    Object getIdentifier();
}
