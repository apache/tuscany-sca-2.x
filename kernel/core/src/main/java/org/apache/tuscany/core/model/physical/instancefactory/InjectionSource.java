package org.apache.tuscany.core.model.physical.instancefactory;

/**
 * Represents a value source.
 *
 * @version $Revision$ $Date$
 */
public class InjectionSource {

    // Type
    private ValueSourceType valueType;

    // Name
    private String name;

    // Type enumeration
    public static enum ValueSourceType {
        CALLBACK,
        REFERENCE,
        PROPERTY
    }

    public InjectionSource() {
    }

    public InjectionSource(ValueSourceType valueType, String name) {
        this.valueType = valueType;
        this.name = name;
    }

    /**
     * Sets the type (callback, reference, property
     *
     * @return Type.
     */
    public ValueSourceType getValueType() {
        return valueType;
    }

    /**
     * Gets the type (callback, reference, property.
     *
     * @param valueType Type.
     */
    public void setValueType(ValueSourceType valueType) {
        this.valueType = valueType;
    }

    /**
     * Gets the name.
     *
     * @return Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name Name.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int result = 31 + ((name == null) ? 0 : name.hashCode());
        result = 31 * result + ((valueType == null) ? 0 : valueType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final InjectionSource other = (InjectionSource)obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (valueType == null) {
            if (other.valueType != null)
                return false;
        } else if (valueType != other.valueType)
            return false;
        return true;
    }

}
