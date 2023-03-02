package com.jie.service;

import com.jie.spring.*;

@Component("userService")
//@Scope("prototype")
public class UserServiceImpl implements InitializingBean,UserService {

    @Autowired
    private OrderService orderService;

    private String beanName;

    public void setBeanName(String beanName) {
        // 设置name的回调
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("userService 初始化");
    }

    @Override
    public void test() {
        System.out.println(orderService);
    }
}
