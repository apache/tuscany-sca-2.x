package org.apache.tuscany.container.spring.mock;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface TestBean {
    public String echo(String msg);

    public TestBean getBean();

    public void setBean(TestBean bean);

}
