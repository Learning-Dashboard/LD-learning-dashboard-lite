package com.upc.gessi.qrapids.app.config.security;

public class SecurityConstants {

	public static final long EXPIRATION_COOKIE_TIME = 900_000; // 15 minutes (in milliseconds)
	public static final long EXPIRATION_JWT_TOKEN_TIME = EXPIRATION_COOKIE_TIME;


	public static final String TOKEN_PREFIX = "Bearer "; // API header validation
	public static final String HEADER_STRING = "Authorization"; // Request header
	public static final String LD_API_KEY_HEADER = "X-LD-API-Key"; // Service-to-service API key header
	public static final String COOKIE_STRING = "xFOEto4jYAjdMeR3Pas6_"; // hashed name cookie

    /**
     * View Cosntants Handlers to display Login Window
     */
    public static final String WELCOME_VIEW_URL = "/";
    public static final String LOGIN_VIEW_URL   = "/login";

	/** Public URLs. */
	public static final String[] PUBLIC_MATCHERS = {

			// public resources

			"/icons/**",
			"/css/**",
			"/js/**",
            "/fonts/**",
			"/images/**",
			"/bootstrap.css",
			"/bootstrap.min.css",
			"/favicon.ico",
			"/styles.css",
			"/swagger-ui",
			"/swagger-ui.html",
			"/swagger-ui/**",
			"/v3/api-docs",
			"/v3/api-docs/**",
			"/swagger-resources/**",
			"/webjars/**",

			// Public routes

            "/signup",
            "/reset-password",
            "/reset-trigger",
            "/error",
            "/success",
            "/"

	};

    public static final String[] GLOBAL_MATCHERS = {
            LOGIN_VIEW_URL,
            "/home",
            "/setupUser",
            "/reset-password",
            "/logout_user",
            "/QRapids-0.0.1/CurrentEvaluation" // API CALL
    };

}
