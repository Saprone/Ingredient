package com.saprone.ingredient.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;
import static org.assertj.core.api.Assertions.assertThat;

public class AppConfigurationTest {

    @Test
    public void webClientBuilderBeanShouldExist() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
        WebClient.Builder webClientBuilder = context.getBean(WebClient.Builder.class);
        assertThat(webClientBuilder).isNotNull();
    }
}
