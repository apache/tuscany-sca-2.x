package org.apache.tuscany.core.mock.component;

/**
 * @version $$Rev$$ $$Date$$
 */
public class AsyncSourceImpl implements AsyncSource{

    private AsyncTarget target;

    public AsyncTarget getTarget() {
        return target;
    }

    public void setTarget(AsyncTarget target) {
        this.target = target;
    }
}
