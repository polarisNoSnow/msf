package com.msf.gateway.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger文档
 * @author polaris
 * @date 2019年11月11日
 */
@Configuration
@EnableSwagger2
@Profile({ "dev", "test" }) //只在dev、test环境开启
public class Swagger2Config{
    @Bean
    public Docket createRestApi() {
    	new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).build();
    	 
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //为当前包路径
                .apis(RequestHandlerSelectors.basePackage("com.msf.gateway.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    
    /**
     * api详情
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("MSF网关服务API")
                //创建人
                .contact(new springfox.documentation.service.Contact("polaris", "https://polarisnosnow.github.io/", "787225863@qq.com"))
                //版本号
                .version("V1.0")
                //描述
                .description("API 描述：此文档用于测试")
                .build();
    }
}
