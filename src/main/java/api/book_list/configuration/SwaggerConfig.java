package api.book_list.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Book List API")
                        .version("1.0")
                        .description("API documentation for the Book List application"));
    }

    @Bean
    public OpenApiCustomizer customOpenApi() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation ->
                        operation.getResponses().forEach((statusCode, apiResponse) -> {
                            if ("500".equals(statusCode) || "404".equals(statusCode) || "400".equals(statusCode)) {
                                apiResponse.setContent(null);
                            }
                        })
                )
        );
    }

}
