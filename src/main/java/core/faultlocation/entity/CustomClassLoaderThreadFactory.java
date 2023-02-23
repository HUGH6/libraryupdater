package core.faultlocation.entity;

import core.faultlocation.entity.runtestsuite.ClassFinder;

import java.util.concurrent.ThreadFactory;

public class CustomClassLoaderThreadFactory implements ThreadFactory {
    private ClassLoader customClassLoader;

    public CustomClassLoaderThreadFactory(ClassLoader customClassLoader) {
        this.customClassLoader = customClassLoader;
    }

    public ClassLoader customClassLoader() {
        return this.customClassLoader;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread newThread = new Thread(r);
        newThread.setDaemon(true);
        newThread.setContextClassLoader(customClassLoader());
        return newThread;
    }
}
