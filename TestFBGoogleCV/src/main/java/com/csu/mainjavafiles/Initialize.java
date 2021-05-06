package com.csu.mainjavafiles;


import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.csu.mainjavafiles.WebConfig;

public class Initialize extends AbstractAnnotationConfigDispatcherServletInitializer {
 
    @Override
    protected Class[] getServletConfigClasses() {
        return new Class[] { WebConfig.class };
    }
 
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
 
    @Override
    protected Class[] getRootConfigClasses() {
        return new Class[] {};
    }
}