package org.apache.tuscany.container.java.scopes;

/**
 * @version $$Rev$$ $$Date$$
 */
public class OrderedDependentPojo extends OrderedInitPojo {

    private OrderedDependentPojo pojo;

    public OrderedDependentPojo getPojo() {
        return pojo;
    }

    public void setPojo(OrderedDependentPojo pojo) {
        this.pojo = pojo;
    }

}
