package com.programming.customer.util;

import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DozerMapperUtil {

    @Value("${mapping.filepath}")
    private String mappingFilePath;

    @Bean
    public Mapper mapper() {
        List<String> mappingFiles = Collections.singletonList(mappingFilePath);

        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        dozerBeanMapper.setMappingFiles(mappingFiles);
        return dozerBeanMapper;
    }
}
