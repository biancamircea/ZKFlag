package ro.mta.toggleserverapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ro.mta.toggleserverapi.security.JWTAuthenticationFilter;


@Configuration
public class SecurityConfig  {

    private final JwtAuthEntryPoint authEntryPoint;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthEntryPoint authEntryPoint, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService, JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.authEntryPoint = authEntryPoint;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,"/client/evaluate").permitAll()
                        .requestMatchers(HttpMethod.POST,"/client/constraints").permitAll()
                        .requestMatchers(HttpMethod.POST,"/client/evaluateZKP").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/logout").permitAll()
                        .requestMatchers("/auth/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()

                        .requestMatchers("/events/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/users/emails").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users/{userId}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/{userId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users/{userId}/admin-projects").hasAuthority("ProjectAdmin")
                        .requestMatchers(HttpMethod.GET, "/users/{userId}/admin-instances").hasAuthority("InstanceAdmin")
                        .requestMatchers( "/users/**").hasAuthority("SystemAdmin")

                        .requestMatchers(HttpMethod.GET,"/environments/active/{instanceId}").hasAuthority("InstanceAdmin")
                        .requestMatchers(HttpMethod.GET,"/environments/instances/{instanceId}").hasAuthority("InstanceAdmin")
                        .requestMatchers("/environments/**").hasAuthority("SystemAdmin")

                        .requestMatchers(HttpMethod.GET,"/projects").authenticated()
                        .requestMatchers(HttpMethod.POST,"/projects").hasAuthority("SystemAdmin")
                        .requestMatchers(HttpMethod.DELETE,"/projects/{projectId}").hasAuthority("SystemAdmin")
                        .requestMatchers(HttpMethod.POST,"projects/{projectId}/access/remove").hasAuthority("SystemAdmin")
                        .requestMatchers(HttpMethod.POST,"projects/{projectId}/access").hasAuthority("SystemAdmin")
                        .requestMatchers(HttpMethod.GET,"/projects/{projectId}/project-admins").hasAnyAuthority("ProjectAdmin", "SystemAdmin")
                        .requestMatchers(HttpMethod.GET,"/projects/{projectId}/tags").hasAnyAuthority("ProjectAdmin", "InstanceAdmin")
                        .requestMatchers(HttpMethod.GET,"/projects/{projectId}/context-fields").hasAnyAuthority("ProjectAdmin", "InstanceAdmin")

                        .requestMatchers(HttpMethod.GET,"/toggles/{toggleId}/getType").hasAuthority("ProjectAdmin")
                        .requestMatchers(HttpMethod.GET,"/toggles/{toggleId}/statistics").hasAuthority("ProjectAdmin")
                        .requestMatchers("/toggles/**").hasAuthority("InstanceAdmin")

                        .requestMatchers(HttpMethod.POST,"/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentName}/on").hasAuthority("InstanceAdmin")
                        .requestMatchers(HttpMethod.POST,"/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentName}/off").hasAuthority("InstanceAdmin")
                        .requestMatchers(HttpMethod.POST,"/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}/payload").hasAuthority("InstanceAdmin")
                        .requestMatchers(HttpMethod.PUT,"/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}/payload").hasAuthority("InstanceAdmin")
                        .requestMatchers(HttpMethod.DELETE,"/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environments/{environmentId}/payload").hasAuthority("InstanceAdmin")

                        .requestMatchers(HttpMethod.POST,"/projects/{projectId}/instances").hasAuthority("SystemAdmin")
                        .requestMatchers(HttpMethod.DELETE,"/projects/{projectId}/instances/{instanceId}").hasAuthority("SystemAdmin")
                        .requestMatchers(HttpMethod.POST,"/projects/{projectId}/instances/{instanceId}/api-tokens").hasAuthority("InstanceAdmin")
                        .requestMatchers(HttpMethod.GET,"/projects/{projectId}/instances").hasAnyAuthority("SystemAdmin", "ProjectAdmin")
                        .requestMatchers(HttpMethod.POST,"/instances/{instanceId}/access").hasAuthority("SystemAdmin")
                        .requestMatchers(HttpMethod.POST,"/instances/{instanceId}/access/remove").hasAuthority("SystemAdmin")
                        .requestMatchers(HttpMethod.GET,"/instances/{instanceId}/instance-admins").hasAnyAuthority("InstanceAdmin", "SystemAdmin")
                        .requestMatchers(HttpMethod.GET,"instances/{instanceId}").hasAnyAuthority("InstanceAdmin","SystemAdmin")
                        .requestMatchers(HttpMethod.GET,"/projects/{projectId}/toggles/{toggleId}").hasAnyAuthority("InstanceAdmin", "ProjectAdmin")
                        .requestMatchers(HttpMethod.GET,"/projects/{projectId}/toggles/{toggleId}/constraints").hasAnyAuthority("InstanceAdmin","ProjectAdmin")
                        .requestMatchers("/projects/{projectId}/toggles/{toggleId}/environment/{environmentId}/instances/{instanceId}/constraints/**").hasAnyAuthority("InstanceAdmin","ProjectAdmin")
                        .requestMatchers("/projects/{projectId}/toggles/{toggleId}/instances/{instanceId}/environment/{environmentId}/constraints/**").hasAnyAuthority("InstanceAdmin","ProjectAdmin")
                        .requestMatchers("/toggles/{toggleId}/instances/{instanceId}/environment/{environmentId}/constraints/**").hasAnyAuthority("InstanceAdmin")
                        .requestMatchers("/constraints/{constraintId}/values").hasAuthority("InstanceAdmin")
                        .requestMatchers("/minio/**").hasAnyAuthority("InstanceAdmin","ProjectAdmin","SystemAdmin")

                        .requestMatchers(HttpMethod.GET,"/instances/{instanceId}/toggles/{toggleId}/environments").hasAnyAuthority("InstanceAdmin","ProjectAdmin")

                        .requestMatchers("/projects/{projectId}/toggles/{toggleId}/constraints/{constraintId}").hasAuthority("ProjectAdmin")
                        .requestMatchers("/projects/{projectId}/toggles/{toggleId}/constraints").hasAuthority("ProjectAdmin")
                        .requestMatchers("/instances/**").hasAuthority("InstanceAdmin")
                        .requestMatchers("/projects/**").hasAuthority("ProjectAdmin")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
