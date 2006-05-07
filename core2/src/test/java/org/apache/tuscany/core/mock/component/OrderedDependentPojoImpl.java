package org.apache.tuscany.core.mock.component;

/**
 * @version $$Rev$$ $$Date$$
 */
public class OrderedDependentPojoImpl extends OrderedInitPojoImpl implements OrderedDependentPojo {

    private OrderedInitPojo pojo;

    public OrderedInitPojo getPojo() {
        return pojo;
    }

    public void setPojo(OrderedInitPojo pojo) {
        this.pojo = pojo;
    }

}
