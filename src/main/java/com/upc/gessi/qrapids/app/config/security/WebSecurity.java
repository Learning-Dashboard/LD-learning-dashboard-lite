package com.upc.gessi.qrapids.app.config.security;

import com.upc.gessi.qrapids.app.config.libs.AuthTools;
import com.upc.gessi.qrapids.app.domain.controllers.UsersController;
import com.upc.gessi.qrapids.app.domain.repositories.AppUser.UserRepository;
import com.upc.gessi.qrapids.app.domain.repositories.Route.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.upc.gessi.qrapids.app.config.security.SecurityConstants.LOGIN_VIEW_URL;
import static com.upc.gessi.qrapids.app.config.security.SecurityConstants.WELCOME_VIEW_URL;
import static com.upc.gessi.qrapids.app.config.security.SecurityConstants.PUBLIC_MATCHERS;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

	private UserDetailsService userDetailsService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
	UsersController usersController;

	@Autowired
	private RouteRepository routeRepository;

	@Autowired
	private AuthTools authTools;

	@Value("${security.enable}")
	private boolean securityEnable;

    @Value("${security.api.enable}")
    private boolean apiEnable;

	@Value("${cors.allowed.origins:}")
	private String corsAllowedOrigins;

	@Value("${cors.allowed.methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
	private String corsAllowedMethods;

	@Value("${cors.allowed.headers:Authorization,Content-Type,X-Requested-With,Accept,Origin,X-LD-API-Key}")
	private String corsAllowedHeaders;

	@Value("${cors.exposed.headers:Authorization,Location}")
	private String corsExposedHeaders;

	@Value("${cors.allow.credentials:false}")
	private boolean corsAllowCredentials;

	public WebSecurity(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	protected void configure( HttpSecurity http ) throws Exception {

		String public_and_secure = ( this.securityEnable )? "/resources/**" : "/**";
        String public_api = ( this.apiEnable )?  "/api/**" : "/fonts/**";


		http.cors().and().csrf().disable().authorizeRequests()

				// View Filter's exception
				.antMatchers(HttpMethod.GET, WELCOME_VIEW_URL).permitAll()
				.antMatchers(HttpMethod.GET, LOGIN_VIEW_URL).permitAll()
				.antMatchers(PUBLIC_MATCHERS).permitAll()

                .antMatchers(public_api).permitAll()
				.antMatchers(public_and_secure).permitAll()

				.anyRequest().authenticated()
				.and()

				.addFilter(new JWTAuthenticationFilter(authenticationManager(),usersController, authTools))
				.addFilter(new JWTAuthorizationFilter(authenticationManager(), userRepository, routeRepository, authTools ))

				// this disables session creation on Spring Security
				.sessionManagement().enableSessionUrlRewriting(false)
				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", buildCorsConfiguration(
				corsAllowedOrigins,
				corsAllowedMethods,
				corsAllowedHeaders,
				corsExposedHeaders,
				corsAllowCredentials));

		return source;
	}

	static CorsConfiguration buildCorsConfiguration(String allowedOrigins,
													String allowedMethods,
													String allowedHeaders,
													String exposedHeaders,
													boolean allowCredentials) {
		List<String> origins = splitCommaSeparatedValues(allowedOrigins);
		if (allowCredentials && origins.contains("*")) {
			throw new IllegalStateException("cors.allowed.origins cannot contain '*' when cors.allow.credentials=true");
		}

		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(origins);
		configuration.setAllowedMethods(splitCommaSeparatedValues(allowedMethods));
		configuration.setAllowedHeaders(splitCommaSeparatedValues(allowedHeaders));
		configuration.setExposedHeaders(splitCommaSeparatedValues(exposedHeaders));
		configuration.setAllowCredentials(allowCredentials);
		configuration.setMaxAge(3600L);
		return configuration;
	}

	static List<String> splitCommaSeparatedValues(String value) {
		if (value == null || value.trim().isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.stream(value.split(","))
				.map(String::trim)
				.filter(item -> !item.isEmpty())
				.collect(Collectors.toList());
	}
}
