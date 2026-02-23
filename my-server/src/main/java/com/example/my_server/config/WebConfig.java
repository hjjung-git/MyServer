package com.example.my_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        File uploadDirFile = new File(uploadDir);

        if (!uploadDirFile.exists()) { uploadDirFile.mkdirs(); }

        String location = uploadDirFile.toURI().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);

        System.out.println("File Upload Path : " + location);
    }
}
