package org.apache.tuscany.container.spring.mock;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface TestBean {
    String echo(String msg);

    TestBean getBean();

    void setBean(TestBean bean);

}
