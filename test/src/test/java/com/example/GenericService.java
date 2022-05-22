package com.example;

public interface GenericService<T> {
    class _1 implements GenericService {}
    class _2 implements GenericService<String> {}
    class _3<E> implements GenericService<E> {}
    class _4 extends _3 {}
    class _5 extends _3<String> {}
    class _6<E> implements GenericService<String> {}
    class _7<E> implements GenericService {}
    class _8<T, E> implements GenericService<T> {}
}
