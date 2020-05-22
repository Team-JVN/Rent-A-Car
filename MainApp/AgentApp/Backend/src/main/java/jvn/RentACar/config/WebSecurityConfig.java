package jvn.RentACar.config;

import jvn.RentACar.security.RestAuthenticationEntryPoint;
import jvn.RentACar.security.TokenAuthenticationFilter;
import jvn.RentACar.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserServiceImpl jwtUserDetailsService;

        @Autowired
        private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
                return super.authenticationManagerBean();
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
                auth.userDetailsService(jwtUserDetailsService).passwordEncoder(jwtUserDetailsService.passwordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
                http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                                .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()

                                .authorizeRequests().antMatchers("/api/auth/**").permitAll()

                                .antMatchers("/api/advertisement", "/api/advertisement/{id}",
                                                "/api/advertisement/{id}/partial", "/api/advertisement/{id}/edit",
                                                "/api/advertisement/all/{status}")
                                .hasAuthority("MANAGE_ADVERTISEMENTS")

                                .antMatchers("/api/body-style", "/api/body-style/{id}", "/api/fuel-type",
                                                "/api/fuel-type/{id}", "/api/gearbox-type", "/api/gearbox-type/{id}",
                                                "/api/make/**")
                                .hasAuthority("MANAGE_CODE_BOOKS")

                                .antMatchers("/api/car", "/api/car/{id}", "/api/car/{id}/partial", "/api/car/{id}/edit")
                                .hasAuthority("MANAGE_CARS")

                                .antMatchers("/api/client", "/api/client/{id}").hasAuthority("MANAGE_CLIENTS")

                                .antMatchers("/api/price-list", "/api/price-list/{id}")
                                .hasAuthority("MANAGE_PRICE_LISTS")

                                .antMatchers(HttpMethod.GET, "/api/rent-report").hasAuthority("MANAGE_RENT_REPORTS")
                                .antMatchers(HttpMethod.POST, "/api/rent-report").hasAuthority("MANAGE_RENT_REPORTS")

                                .antMatchers("/api/role").hasAuthority("MANAGE_ROLES")
                                .antMatchers(HttpMethod.GET, "/api/permission").hasAuthority("MANAGE_ROLES")

                                .antMatchers(HttpMethod.POST, "/api/rent-request").hasAuthority("CREATE_RENT_REQUEST")

                                .antMatchers(HttpMethod.GET, "/api/rent-request/{status}/mine",
                                                "/api/rent-request/{id}")
                                .hasAuthority("GET_MY_RENT_REQUESTS")

                                .antMatchers(HttpMethod.GET,
                                                "/api/advertisement/{advertisementId}/rent-requests/{status}",
                                                "/api/rent-request/{id}")
                                .hasAuthority("GET_RECEIVED_RENT_REQUESTS")

                                .antMatchers(HttpMethod.DELETE, "/api/rent-request/{id}")
                                .hasAuthority("DELETE_RENT_REQUEST")

                                .antMatchers(HttpMethod.PUT, "/api/rent-request/{id}")
                                .hasAuthority("CHANGE_RENT_REQUEST_STATUS")

                                .anyRequest().authenticated().and()

                                .cors().and()

                                .addFilterBefore(new TokenAuthenticationFilter(jwtUserDetailsService.tokenUtils,
                                                jwtUserDetailsService), BasicAuthenticationFilter.class)
                                .headers().contentSecurityPolicy(
                                                "default-src 'self' https://localhost:8090/; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; script-src 'self' 'unsafe-eval'; font-src 'self' https://fonts.googleapis.com https://fonts.gstatic.com;img-src 'self' data:");

                // http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
                http.csrf().disable();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {

                web.ignoring().antMatchers(HttpMethod.POST, "/api/auth/**");
                web.ignoring().antMatchers(HttpMethod.PUT, "/api/auth");
                web.ignoring().antMatchers(HttpMethod.PUT, "/api/client/activate**");
                web.ignoring().antMatchers(HttpMethod.GET, "/api/advertisement/{id}");
                web.ignoring().antMatchers(HttpMethod.GET, "/api/car/{id}/picture");
                web.ignoring().antMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "/favicon.ico","/favicon.png", "/**/*.html",
                                "/**/*.css", "/**/*.js", "/assets/**", "/*.jpg");
        }

}