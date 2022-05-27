package com.mikep.applicantTimer;

import com.mikep.applicantTimer.Filters.JwtRequestFilter;
import com.mikep.applicantTimer.Services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@Profile("!https")
public class SecSecurityConfig
        extends WebSecurityConfigurerAdapter {

    //...
    @Autowired
    private MyUserDetailsService userDetailsService;

//    public void setMyUserDetailsService(MyUserDetailsService service) {
//        userDetailsService = service;
//    }
//    public MyUserDetailsService getMyUserDetailsService() {
//        return userDetailsService;
//    }

    @Autowired
    JwtRequestFilter jwtRequestFilter;

//    public void setJwtRequestFilter(JwtRequestFilter jwt) {
//        jwtRequestFilter = jwt;
//    }
//    public JwtRequestFilter getJwtRequestFilter() {
//        return jwtRequestFilter;
//    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.HEAD.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name()));
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setUserDetailsService(this.userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(final HttpSecurity http)
            throws Exception {
                    http
                        .cors()
                        .and()
                    .csrf()
                        .disable()
                            .authorizeRequests()
                //...
                            .antMatchers(
                            HttpMethod.GET,
                            "/index*", "/static/**", "/*.js", "/*.json", "/*.ico")
                            .permitAll()
                            .antMatchers(
                                HttpMethod.POST,
                                "/index*", "/static/**", "/*.js", "/*.json", "/*.ico")
                        .permitAll()
                        .antMatchers("/auth/**").permitAll()

                        .anyRequest().authenticated()
                        .and()

                .logout()
                    .permitAll()
                    .and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);


                http.exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        }
                );
        //...
    }
}