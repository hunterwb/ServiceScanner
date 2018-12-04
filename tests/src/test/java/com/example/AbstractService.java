package com.example;

public abstract class AbstractService {

    public static class _1 extends AbstractService {}
    public static class _2 extends AbstractService {
        public _2() {}
    }
    public static class _3 extends A {}
    public static class _4 extends B {}
    public final static class _5 extends _1 {}

    public static abstract class A extends AbstractService {}
    public static class B extends AbstractService {
        B() {}
    }
    static class C extends AbstractService {}
    private static class D extends AbstractService {}
    public class E extends AbstractService {}
    static class F extends AbstractService {
        public F() {}
    }
    public static class G {}
}
