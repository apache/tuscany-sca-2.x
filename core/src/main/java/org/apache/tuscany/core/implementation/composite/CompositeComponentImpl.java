package org.apache.tuscany.core.implementation.composite;

import java.util.Map;

import org.w3c.dom.Document;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.spi.component.CompositeComponent;

/**
 * The standard implementation of a composite component. Autowiring is performed by delegating to the parent composite.
 *
 * @version $Rev$ $Date$
 */
public class CompositeComponentImpl<T> extends AbstractCompositeComponent<T> {
    private String uri;

    public CompositeComponentImpl(String name,
                                  CompositeComponent parent,
                                  AutowireComponent autowireContext,
                                  Map<String, Document> propertyValues) {
        this(name, null, parent, autowireContext, propertyValues);
    }

    /**
     * Constructor specifying name and URI.
     *
     * @param name              the name of this Component
     * @param uri               the unique identifier for this component
     * @param parent            this component's parent
     * @param autowireComponent the component that should be used to resolve autowired references
     * @param propertyValues    this composite's Property values
     */
    public CompositeComponentImpl(String name,
                                  String uri,
                                  CompositeComponent parent,
                                  AutowireComponent autowireComponent,
                                  Map<String, Document> propertyValues) {
        super(name, parent, autowireComponent, propertyValues);
        this.uri = uri;
    }

    public String getURI() {
        return uri;
    }
}
