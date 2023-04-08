package io.heartpattern.javagpt;

import java.lang.reflect.Proxy;

public class JavaGpt {
    public static <T> T generate(String apiKey, Class<T> clazz) {
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new JavaGptInvocationHandler(apiKey)
        );
    }
}
