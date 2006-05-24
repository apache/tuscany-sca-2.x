package org.apache.tuscany.container.spring.mock;

/**
 * @version $$Rev$$ $$Date$$
 */
public class TestBeanImpl implements TestBean{

    private TestBean bean;

    public String echo(String msg) {
        return msg;
    }

    public TestBean getBean() {
        return bean;
    }

    public void setBean(TestBean bean) {
        this.bean = bean;
    }
}
