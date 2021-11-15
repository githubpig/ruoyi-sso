package com.ruoyi.test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author piggy
 * @desciption
 * @date 2021/11/13 - 22:10
 */
public class Test {
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(13);
     /*   atomicInteger.addAndGet(1);
        System.out.println(atomicInteger.get());*/
        System.out.println(atomicInteger.getAndIncrement());
        System.out.println(atomicInteger.get());
    }
}
