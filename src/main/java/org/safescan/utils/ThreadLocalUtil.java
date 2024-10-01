package org.safescan.utils;

public class ThreadLocalUtil {
    // Providing ThreadLocal Objects
    public static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    // Get value according to key
    public static <T> T get(){
        return (T) THREAD_LOCAL.get();
    }

    // Storing key-value pairs
    public static void set(Object value) {
        THREAD_LOCAL.set(value);
    }

    // Clear ThreadLocal to prevent memory leaks
    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
