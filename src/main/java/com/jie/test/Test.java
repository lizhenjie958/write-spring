package com.jie.test;

import com.jie.service.UserService;
import com.jie.spring.WriteApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
public class Test {
    public static void main(String[] args) {
        WriteApplicationContext writeApplicationContext = new WriteApplicationContext(AppConfig.class);
        UserService userService = (UserService)writeApplicationContext.getBean("userService");
//        UserService userService1 = (UserService)writeApplicationContext.getBean("userService");
//
//        System.out.println(userService == userService1);

        userService.test();

    }
}
