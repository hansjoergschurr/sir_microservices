package com.example.Gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}


	@Value("${service.authentication}")
	private String auth_service_url;

	@Value("${service.profile}")
	private String profile_service_url;

	@Bean
	public RouteLocator serviceRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
			.route(p -> p
				.path("/profiles")
				.and()
				.method(HttpMethod.GET)
				.filters(f -> f.prefixPath("/PS"))
				.uri(profile_service_url))
			.route(p-> p
				.path("/profiles/{id}/token")
				.filters(f-> f.rewritePath("/profile", "/AS/user"))
				.uri(auth_service_url))
			.route(p -> p
				.path("/profiles/{id}/{endpoint}")
				.filters(f -> f.prefixPath("/PS"))
				.uri(profile_service_url))
			.build();
	}

}
