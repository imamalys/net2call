package com.atlasat.android_phone_sdk.utils;

import androidx.core.util.Function;

public class SingletonHolder<T, A> {
    private final Function<A, T> creator;
    private volatile T instance;

    public SingletonHolder(Function<A, T> creator) {
        this.creator = creator;
    }

    public boolean exists() {
        return instance != null;
    }

    public void destroy() {
        instance = null;
    }

    public T get() {
        // Will throw NPE if needed
        return instance;
    }

    public T create(A arg) {
        T i = instance;
        if (i != null) {
            return i;
        }

        synchronized (this) {
            T i2 = instance;
            if (i2 != null) {
                return i2;
            } else {
                T created = creator.apply(arg);
                instance = created;
                return created;
            }
        }
    }

    public T required(A arg) {
        return instance != null ? instance : create(arg);
    }
}
