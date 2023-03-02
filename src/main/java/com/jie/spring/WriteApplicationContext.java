package com.jie.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spring容器
 */
public class WriteApplicationContext {

    private Class configClass;

    private Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private Map<String,Object> singletonObjects = new ConcurrentHashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public WriteApplicationContext(Class configClass){
        this.configClass = configClass;
        scan(configClass);
        beanDefinitionMap.forEach((k,v)->{
            if (v.getScope().equals("singleton")) {
                Object bean = createBean(v);
                singletonObjects.put(k,bean);
            }
        });
    }

    /**
     *
     * @param configClass
     */
    private void scan(Class configClass) {
        // 解析配置类
        // ComponentScan注解---> 扫描路径 --> 扫描
        ComponentScan componentScan = (ComponentScan)configClass.getDeclaredAnnotation(ComponentScan.class);
        String value = componentScan.value(); // 扫描路径
        ClassLoader classLoader = WriteApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(value.replace(".","/"));
        File file = new File(resource.getFile());
        if(file.isDirectory()){
            for (File listFile : file.listFiles()) {
                String fileName = listFile.getAbsolutePath();
                if(fileName.endsWith(".class")){
                    String classPath = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class")).replace("\\", ".");
                    try {
                        Class<?> aClass = classLoader.loadClass(classPath);
                        // 此类如果有component注解
                        if (aClass.isAnnotationPresent(Component.class)) {

                            // 是否继承是beanPostProcessor
                            if(BeanPostProcessor.class.isAssignableFrom(aClass)){
                                BeanPostProcessor instance = (BeanPostProcessor)aClass.getDeclaredConstructor().newInstance();
                                beanPostProcessorList.add(instance);
                            }

                            Component component = aClass.getDeclaredAnnotation(Component.class);
                            String beanName = component.value();
                            // bean定义对象
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(aClass);
                            beanDefinition.setBeanName(beanName);
                            if(aClass.isAnnotationPresent(Scope.class)){
                                Scope scope = aClass.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scope.value());
                            }else{
                                beanDefinition.setScope("singleton");
                            }

                            beanDefinitionMap.put(beanName,beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();

                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public Object getBean(String name){
        try {
            if (beanDefinitionMap.containsKey(name)) {
                BeanDefinition beanDefinition = beanDefinitionMap.get(name);
                if (beanDefinition.getScope().equals("singleton")) {
                    return singletonObjects.get(name);
                }else{
                    return createBean(beanDefinition);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Object createBean(BeanDefinition beanDefinition){
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.newInstance();
            // 依赖注入
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    Object bean = getBean(declaredField.getName());
                    boolean require = declaredField.getAnnotation(Autowired.class).require();
                    if(require && bean == null){
                        throw new RuntimeException(declaredField.getName() + "不存在");
                    }

                    declaredField.setAccessible(true);
                    declaredField.set(instance,bean);
                }
            }

            // beanNameAware,回调
            if(instance instanceof BeanNameAware){
                // 回调，将beanname回传
                ((BeanNameAware)instance).setBeanName(beanDefinition.getBeanName());
            }

            // 初始化前
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessBeforeInitialization(instance,beanDefinition.getBeanName());
            }

            // 初始化
            if(instance instanceof InitializingBean){
                ((InitializingBean)instance).afterPropertiesSet();
            }

            // 初始化后
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance,beanDefinition.getBeanName());
            }
            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("创建bean失败");
    }
}
