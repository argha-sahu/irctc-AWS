package io.argha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@Configuration
public class SwaggerConfig {
	
	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("io.argha"))
				.paths(regex("/irctc/.*")).build().apiInfo(metaInfo());
	}
	
	private ApiInfo metaInfo()
	{
		ApiInfo apiInfo = new ApiInfo("Mini IRCTC(Backend assignment)", "Basic APIs for an IRCTC like system(Train lookup)", "1.0", "Terms of service",
				new Contact("Argha Sahu", "8961201536", "argha.sahu@teradata.com"), "Non licensed version", "*****************************");
		return apiInfo;
	}
}
