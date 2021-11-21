package com.lt.rules;

/**
 * @author gaijf
 * @description
 * @date 2021/1/14
 */
public interface MaLineRule<T,M,R> extends BaseRule<T,R> {
    R verify(T t,M m);
}
