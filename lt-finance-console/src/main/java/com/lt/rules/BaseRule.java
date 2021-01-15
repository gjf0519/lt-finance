package com.lt.rules;

import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2021/1/14
 */
public interface BaseRule<T,R> {
    R verify(T t);
}
