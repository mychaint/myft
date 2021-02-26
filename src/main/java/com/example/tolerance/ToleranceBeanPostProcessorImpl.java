package com.example.tolerance;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ToleranceBeanPostProcessorImpl implements BeanDefinitionRegistryPostProcessor, BeanFactoryPostProcessor, BeanPostProcessor {

    private BeanDefinitionRegistry registry;
    private ConfigurableListableBeanFactory factory;
    private Set<String> processSet = new HashSet<>();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.registry = beanDefinitionRegistry;
        ConcurrentHashMap<String, ConcurrentHashMap<String, BeanDefinition>> definitionsMap = new ConcurrentHashMap<>();
        for (var name : this.registry.getBeanDefinitionNames()) {
            BeanDefinition definition = this.registry.getBeanDefinition(name);
            if (definition instanceof ScannedGenericBeanDefinition
                    && ((ScannedGenericBeanDefinition) definition).getMetadata()
                    .hasAnnotation(Fallback.class.getName())
            ) {
                String interfaceName = ((ScannedGenericBeanDefinition) definition).getMetadata().getInterfaceNames()[0];
                if (!definitionsMap.contains(interfaceName)) {
                    definitionsMap.put(interfaceName, new ConcurrentHashMap<>());
                }
                definitionsMap.get(interfaceName).put(name, definition);
            }
        }
        ConcurrentHashMap<String, ConcurrentHashMap<String, BeanDefinition>> deMaps = new ConcurrentHashMap<>();
        for (var interfaceName : definitionsMap.keySet()) {
            for (var name : this.registry.getBeanDefinitionNames()) {
                BeanDefinition definition = this.registry.getBeanDefinition(name);
                if (definition instanceof ScannedGenericBeanDefinition
                        && Arrays.stream(((ScannedGenericBeanDefinition) definition)
                        .getMetadata()
                        .getInterfaceNames())
                        .anyMatch(x -> x.equals(interfaceName))) {
                    if (!deMaps.contains(interfaceName)) {
                        deMaps.put(interfaceName, new ConcurrentHashMap<>());
                    }
                    if (!((ScannedGenericBeanDefinition) definition).getMetadata().hasAnnotation(Fallback.class.getName())) {
                        deMaps.get(interfaceName).put("PRIMARY", definition);
                        this.registry.removeBeanDefinition(name);
                        this.registry.registerBeanDefinition(definition.getBeanClassName(), definition);
                        try {
                            this.registry.registerBeanDefinition(name, BeanDefinitionBuilder.genericBeanDefinition(Class.forName(definition.getBeanClassName())).getBeanDefinition());
                        }catch (ClassNotFoundException e) {

                        }
                    } else {
                        deMaps.get(interfaceName).put("Optional", definition);
                    }
                }
            }
        }

        System.out.println("dsfs");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.factory = configurableListableBeanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Fallback annotation = bean.getClass().getAnnotation(Fallback.class);
        if (annotation != null) {
            String primaryBeanName = annotation.serviceQualifier();
            Object primary = this.factory.getBean(primaryBeanName);
            Object secondary = bean;
            var invocationHandler = new ToleranceBeanInvocationHandler(primary, secondary);
            Object proxy = Proxy.newProxyInstance(
                    bean.getClass().getClassLoader(),
                    bean.getClass().getInterfaces(),
                    invocationHandler
            );
            ((BeanDefinitionRegistry) this.factory).removeBeanDefinition(primaryBeanName);
            ((BeanDefinitionRegistry) this.factory).registerBeanDefinition(primaryBeanName, BeanDefinitionBuilder.genericBeanDefinition(proxy.getClass()).getBeanDefinition());
            this.factory.registerSingleton(primaryBeanName, proxy);
            return proxy;
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
}
