package com.makeen.patientcare.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanChecker implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

        // Check if the ElasticsearchClient bean is present
        if (applicationContext.containsBean("elasticsearchClient")) {
            System.out.println("ElasticsearchClient bean is created.");
        } else {
            System.out.println("ElasticsearchClient bean is NOT created.");
        }
    }
}
