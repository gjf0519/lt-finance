package com.lt.rules;

import com.lt.shape.MaLineType;

import java.util.List;

/**
 * @author gaijf
 * @description
 * @date 2021/1/14
 */
public interface MaLineRule<T,M,R> extends BaseRule<T,R> {
    R verify(T t,M m);
}
