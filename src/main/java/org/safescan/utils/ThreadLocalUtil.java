package org.safescan.utils;

public class ThreadLocalUtil {
    //提供ThreadLocal对象
    public static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    //根据键获取值
    public static <T> T get(){
        return (T) THREAD_LOCAL.get();
    }

    //储存键值对
    public static void set(Object value) {
        THREAD_LOCAL.set(value);
    }

    //清除ThreadLocal防止内存泄漏
    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
