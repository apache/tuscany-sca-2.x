package org.osoa.sca;

public final class LauncherCurrentCompositeContext {
    private LauncherCurrentCompositeContext() {
    }

    public static void setContext(CompositeContext context) {
        CurrentCompositeContext.setContext(context);
    }
}
