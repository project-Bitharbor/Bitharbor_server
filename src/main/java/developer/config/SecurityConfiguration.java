package developer.config;


import developer.login.handler.UserAccessDeniedHandler;
import developer.login.handler.UserAuthenticationEntryPoint;
import developer.login.handler.UserAuthenticationFailureHandler;
import developer.login.handler.UserAuthenticationSuccessHandler;
import developer.login.jwt.filter.JwtAuthenticationProcessingFilter;
import developer.login.jwt.filter.JwtVerificationFilter;
import developer.login.jwt.service.JwtService;
import developer.login.jwt.util.CustomAuthorityUtils;
import developer.login.oauth.OAuth2UserSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration @RequiredArgsConstructor
@EnableWebSecurity(debug=false)
public class SecurityConfiguration {
    private final JwtService jwtService;
    private final CustomAuthorityUtils authorityUtils;
    private final OAuth2UserSuccessHandler oAuth2UserSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return
                http.headers().frameOptions().sameOrigin()
                        .and()
                        .csrf().disable()
                        .cors(withDefaults())
                        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and()
                        .exceptionHandling()
                        .authenticationEntryPoint(new UserAuthenticationEntryPoint())
                        .accessDeniedHandler(new UserAccessDeniedHandler())
                        .and()
                        .apply(new CustomFilterConfigurer())
                        .and()
                        .formLogin().disable()
                        .httpBasic().disable()
                        .authorizeHttpRequests()
                        .antMatchers("/members").permitAll()
                        .antMatchers(HttpMethod.GET, "/members/**").permitAll()
                        .antMatchers("/members/login").permitAll()
                        .antMatchers("/members/**").hasRole("MEMBER")

                        .antMatchers(HttpMethod.GET, "/community/**").permitAll()
                        .antMatchers("/community/**").hasRole("MEMBER")

                        .antMatchers(HttpMethod.GET, "/knowledge/**").permitAll()
                        .antMatchers("/knowledge/**").hasRole("MEMBER")

                        .antMatchers(HttpMethod.GET, "/qna/**").permitAll()
                        .antMatchers("/qna/**").hasRole("MEMBER")

                        .anyRequest().permitAll()
                        .and()
                        .oauth2Login()
                        .successHandler(oAuth2UserSuccessHandler)
                        .and()
                        .build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("http://localhost:3000");
        configuration.addAllowedOriginPattern("http://localhost:8080");
        configuration.addAllowedOriginPattern("http://ec2-13-125-193-97.ap-northeast-2.compute.amazonaws.com:8080");
        configuration.addAllowedOriginPattern("https://server.bit-harbor.net");
        configuration.addAllowedOriginPattern("https://bit-harbor.vercel.app");
        configuration.addAllowedOriginPattern("*");

        configuration.addAllowedHeader("*");
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Refresh");
        configuration.addExposedHeader(HttpHeaders.LOCATION);
         configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationProcessingFilter jwtAuthenticationFilter = new
                    JwtAuthenticationProcessingFilter(authenticationManager, jwtService);
            jwtAuthenticationFilter.setFilterProcessesUrl("/members/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new UserAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new UserAuthenticationFailureHandler());

            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtService, authorityUtils);

            builder.addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationProcessingFilter.class)
                    .addFilterAfter(jwtVerificationFilter, OAuth2LoginAuthenticationFilter.class);
        }

    }
}
