package pl.kostrzynski.tfa.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import pl.kostrzynski.tfa.api.FirstFactorApi;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@ComponentScan(basePackageClasses = {FirstFactorApi.class} )
@EnableSwagger2
@PropertySource("classpath:swagger.properties")
@Configuration
public class SwaggerConfig {
    private static final String SWAGGER_API_VERSION = "1.0";
    private static final String title = "2FA";
    private static final String description = "BEng Thesis Project about the usage of Public-Key Cryptography in the two factor authentication";

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(SWAGGER_API_VERSION)
                .build();
    }

    @Bean
    public Docket TwoFactorAuthorizationAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .pathMapping("")
                .ignoredParameterTypes()
                .select()
                .paths(PathSelectors.regex("/tfa/service/rest/v1/.*"))
                .build();
    }
}
