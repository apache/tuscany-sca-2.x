package org.apache.tuscany.spi.extension;

import org.apache.tuscany.model.Implementation;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.osoa.sca.annotations.Init;

/**
 * @version $$Rev$$ $$Date$$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public abstract class ComponentBuilderExtension<I extends Implementation<?>> implements ComponentBuilder<I> {

    protected BuilderRegistry builderRegistry;

    @Autowire
    public void setBuilderRegistry(BuilderRegistry registry) {
        this.builderRegistry = registry;
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(this);
    }

}
