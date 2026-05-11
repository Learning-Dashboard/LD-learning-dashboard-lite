package com.upc.gessi.qrapids.app.config.security;

import com.upc.gessi.qrapids.app.config.libs.AuthTools;
import com.upc.gessi.qrapids.app.config.libs.RouteFilter;
import com.upc.gessi.qrapids.app.domain.models.AppUser;
import com.upc.gessi.qrapids.app.domain.repositories.AppUser.UserRepository;
import com.upc.gessi.qrapids.app.domain.models.Route;
import com.upc.gessi.qrapids.app.domain.repositories.Route.RouteRepository;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.upc.gessi.qrapids.app.config.security.SecurityConstants.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	private final AuthTools authTools;

	private RouteFilter routeFilter;

    private SessionTimer sessionTimer;

    private UserRepository userRepository;

    private RouteRepository routeRepository;

	private boolean DEBUG = false;

	private Logger oldlogger = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    private java.util.logging.Logger logger = java.util.logging.Logger.getLogger("navigation");

    public JWTAuthorizationFilter(AuthenticationManager authManager, UserRepository userRepository, RouteRepository routeRepository, AuthTools authTools) {
        super(authManager);
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.authTools = authTools;
    }

	/**
	 * Extraemos datos de la petición, Cabecera de autenticación
	 * @param req
	 * @param res
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest req,
									HttpServletResponse res,
									FilterChain chain) throws IOException, ServletException {

        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        this.routeFilter = new RouteFilter();
        // is an External request? true -> WebPage : false -> external
        // boolean origin = this.authTools.originRequest( req );



        // Authorization object
		UsernamePasswordAuthenticationToken authentication;

		// Get header & cookie auth string variables
        String header = req.getHeader(HEADER_STRING);
        String cookie_token = this.authTools.getCookieToken( req, COOKIE_STRING );
        String token = "";
        String username = "";
        boolean usingCookieToken = false;
        this.sessionTimer = SessionTimer.getInstance();

        if ( cookie_token != null && cookie_token != "" && !cookie_token.isEmpty() ) {
            // WeaApp Client internal application

            try {
                authentication = this.authTools.tokenValidation( cookie_token );
                token = cookie_token;

                username = this.authTools.getUser(cookie_token);
                sessionTimer.cancelTimer(cookie_token);
                usingCookieToken = true;
            } catch (JwtException | IllegalArgumentException e) {
                logoutInvalidJwt(req, res, cookie_token, true);
                return;
            }

            logMessage(" Origin - WebApp ");

        } else {

            // External application API Access
            if( header == null || !header.startsWith(TOKEN_PREFIX) ){

                logMessage(" No token API ");

                chain.doFilter(req, res);

                return;
            }

            try {
                authentication = getAuthentication( req );
                token = req.getHeader( HEADER_STRING );
            } catch (JwtException | IllegalArgumentException e) {
                logoutInvalidJwt(req, res, token, false);
                return;
            }

            logMessage(" Origin - ApiCall ");

        }

        /** --[ Filter implementation ]-- */

        // Flag valitation
        boolean isAllowed = false;

        // Global route
        String origin_request = req.getRequestURI();

        // User container
        AppUser user = null;

        // List of routes container
        List<Route> routes = new ArrayList<>();

        // Public resources
        isAllowed = this.routeFilter.publicURLAttemp( origin_request );

        if (! isAllowed )
            isAllowed = this.routeFilter.globalURLAttemp( origin_request );

        // We verify if route is a public resource
        if( ! isAllowed ) {

            // User data from DB
            try {
                user = this.userRepository.findByUsername( this.authTools.getUserToken( token ) );
            } catch (JwtException | IllegalArgumentException e) {
                logoutInvalidJwt(req, res, token, usingCookieToken);
                return;
            }


            if ( user!=null && user.getAdmin() )
                isAllowed = true;


            // Test elements and try to verify if the current route is allowed for the current user
            else{
                //isAllowed = true;
                // Cast set object ot List of AppUSers
                // Old version that checks if the route is allowed for a not admin user
                //routes.addAll( user.getUserGroup().getRoutes() );
                //isAllowed = this.routeFilter.filterShiled( origin_request, token, routes );
                // New version.
                isAllowed = !this.routeFilter.filterShiled( origin_request, token, routeRepository.findAll());
            }

        }

        /** [ Route Filtering ] */

        // Verfiy an redirect if user does not have permission to use the current route.
        if ( ! isAllowed ){

            res.sendRedirect( "/login?error=User+does+not+have+permission" );

        } else {

            if (usingCookieToken) {
                clearAuthCookie(res);

                // Web Application
                // Set token auth in HTTP Only cookie client.
                Cookie qrapids_token_client = new Cookie(COOKIE_STRING, token);

                // Configuration
                // Changed HttpOnly to false to read it from the application
                qrapids_token_client.setHttpOnly(true);
                qrapids_token_client.setMaxAge((int) EXPIRATION_COOKIE_TIME / 1000);
                qrapids_token_client.setPath("/");

                sessionTimer.startTimer(username, token, (int) EXPIRATION_COOKIE_TIME / 1000);
                res.addCookie(qrapids_token_client);
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            logMessage(origin_request + " <- -> [Final status] : " + isAllowed);
            if(user!=null)logger.info(user.getUsername() + " goes to " + origin_request+ " " + now);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(req, res);

        }

	}

	private void logMessage (String message) {
        if (this.DEBUG)
            oldlogger.info(message);
    }

    private void logoutInvalidJwt(HttpServletRequest req,
                                  HttpServletResponse res,
                                  String token,
                                  boolean redirectToLogin) throws IOException {
        SecurityContextHolder.clearContext();
        clearAuthCookie(res);

        if (token != null && !token.isEmpty()) {
            sessionTimer.cancelTimer(token);
        }

        if (req.getSession(false) != null) {
            req.getSession(false).invalidate();
        }

        if (redirectToLogin) {
            res.sendRedirect("/login?error=Session+expired");
        } else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Invalid JWT token\"}");
        }
    }

    private void clearAuthCookie(HttpServletResponse res) {
        Cookie cookie = new Cookie(COOKIE_STRING, null); // Not necessary, but saves bandwidth.
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Don't set to -1 or it will become a session cookie!
        cookie.setPath("/");
        res.addCookie(cookie);
    }

	/**
	 * Obtención de token de la cabecera previamente obtenida en doFilterInternal
	 * Obteción de datos de usuario serializados
	 * @param request
	 * @return
	 */
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

		String token = request.getHeader(HEADER_STRING);

		return this.authTools.tokenValidation( token );

	}

}
