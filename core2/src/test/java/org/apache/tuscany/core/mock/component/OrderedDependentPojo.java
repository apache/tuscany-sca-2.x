package org.apache.tuscany.core.mock.component;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface OrderedDependentPojo extends OrderedInitPojo{

    OrderedInitPojo getPojo();

    void setPojo(OrderedInitPojo pojo);
}
