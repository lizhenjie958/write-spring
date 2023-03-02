package com.jie.spring;

import lombok.Data;

/**
 * bean定义对象
 */
@Data
public class BeanDefinition {
    private Class clazz;
    private String scope;
    private String beanName;
}
