package com.jie.service;

import com.jie.spring.BeanPostProcessor;
import com.jie.spring.Component;
import org.springframework.beans.BeansException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component("beanPostProcessorService")
public class BeanPostProcessorService implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if("userService".equals(beanName)){
            System.out.println(String.format("%s 初始化前",beanName));
        }
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if("userService".equals(beanName)){
            System.out.println(String.format("%s 初始化后",beanName));
            // 利用jdk的动态代理，模拟aop
            Object instance = Proxy.newProxyInstance(BeanPostProcessorService.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("代理逻辑");
                    return method.invoke(bean, args);
                }
            });
            return instance;
        }
        return bean;
    }
}
