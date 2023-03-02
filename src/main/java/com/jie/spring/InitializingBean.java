package com.jie.spring;

public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
