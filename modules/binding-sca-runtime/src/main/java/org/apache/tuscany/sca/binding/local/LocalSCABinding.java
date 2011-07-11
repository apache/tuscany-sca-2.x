package org.apache.tuscany.sca.binding.local;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;

/**
 * Represents a Local SCA Binding
 */
public interface LocalSCABinding extends Binding {
    QName TYPE = new QName(Base.SCA11_TUSCANY_NS, "binding.local");
}
