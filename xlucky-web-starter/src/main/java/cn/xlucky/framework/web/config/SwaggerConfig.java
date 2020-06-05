package cn.xlucky.framework.web.config;

import cn.xlucky.framework.web.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * swagger配置
 * @author xlucky
 * @date 2020/3/20
 * @version 1.0.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Value("${swagger.title:API接口}")
    private String title;
    @Value("${swagger.description:API接口文档}")
    private String description;
    @Value("${swagger.version:1.0}")
    private String version;
    @Value("${swagger.basePackage:cn.xlucky.**.controller}")
    private String basePackage;


    @Bean
    public Docket createRestApi() {
        if (SpringContextUtil.isProdEnv()) {
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfoProd())
                    .select()
                    .paths(PathSelectors.none())
                    .build();
        }
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .version(version)
                .description(description)
                .build();
    }

    private ApiInfo apiInfoProd() {
        return new ApiInfoBuilder()
                .title("")
                .description("")
                .license("")
                .licenseUrl("")
                .termsOfServiceUrl("")
                .version("")
                .build();
    }


}