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

                .antMatchers("/api/body-style/{id}", "/api/fuel-type/{id}", "/api/gearbox-type", "/api/gearbox-type/{id}",
                        "/api/make/{id}", "/api/make/{makeId}/model/{modelId}")
                .hasAuthority("MANAGE_CODE_BOOKS")

                .antMatchers(HttpMethod.POST, "/api/body-style", "/api/fuel-type", "/api/gearbox-type",
                        "/api/make/", "/api/make/{makeId}/model")
                .hasAuthority("MANAGE_CODE_BOOKS")

                .antMatchers("/api/car", "/api/car/{id}", "/api/car/{id}/partial", "/api/car/{id}/edit")
                .hasAuthority("MANAGE_CARS")

                .antMatchers(HttpMethod.POST, "/api/client").hasAnyAuthority("MANAGE_CLIENTS", "MANAGE_ADVERTISEMENTS", "CLIENT_EDIT_PROFILE")
                .antMatchers(HttpMethod.GET, "/api/client/{id}").hasAnyAuthority("MANAGE_CLIENTS", "MANAGE_ADVERTISEMENTS", "CLIENT_EDIT_PROFILE")

                .antMatchers(HttpMethod.PUT, "/api/client")
                .hasAuthority("CLIENT_EDIT_PROFILE")
                .antMatchers(HttpMethod.GET, "/api/client/profile")
                .hasAuthority("CLIENT_EDIT_PROFILE")

                .antMatchers(HttpMethod.PUT, "/api/agent")
                .hasAuthority("AGENT_EDIT_PROFILE")
                .antMatchers(HttpMethod.GET, "/api/agent/profile")
                .hasAuthority("AGENT_EDIT_PROFILE")

                .antMatchers("/api/price-list", "/api/price-list/{id}")
                .hasAuthority("MANAGE_PRICE_LISTS")

                .antMatchers(HttpMethod.GET, "/api/rent-report").hasAuthority("MANAGE_RENT_REPORTS")
                .antMatchers(HttpMethod.POST, "/api/rent-report").hasAuthority("MANAGE_RENT_REPORTS")

                .antMatchers("/api/role").hasAuthority("MANAGE_ROLES")
                .antMatchers(HttpMethod.GET, "/api/permission").hasAuthority("MANAGE_ROLES")
                .antMatchers(HttpMethod.GET, "/api/rent-report").hasAuthority("MANAGE_RENT_REPORTS")
                .antMatchers(HttpMethod.POST, "/api/rent-report/{rentInfoId}").hasAuthority("MANAGE_RENT_REPORTS")
                .antMatchers(HttpMethod.POST, "/api/rent-request").hasAnyAuthority("MANAGE_ADVERTISEMENTS", "MY_RENT_REQUESTS")


                .antMatchers(HttpMethod.GET, "/api/rent-request/{status}/mine")
                .hasAuthority("MY_RENT_REQUESTS")
                .antMatchers(HttpMethod.PUT, "/api/rent-request/{rentRequestId}/rent-info/{rentInfoId}/pay")
                .hasAuthority("MY_RENT_REQUESTS")

                .antMatchers(HttpMethod.GET,
                        "/api/advertisement/{advertisementId}/rent-requests/{status}")
                .hasAuthority("MANAGE_ADVERTISEMENTS")

                .antMatchers(HttpMethod.PUT, "/api/rent-request/{id}")
                .hasAnyAuthority("MANAGE_ADVERTISEMENTS", "MY_RENT_REQUESTS")

                .antMatchers("/api/car/statistics/{filter}")
                .hasAuthority("GET_STATISTICS")

                .antMatchers(HttpMethod.POST, "/api/rent-request/{id}/rent-info/{rentInfoId}/comment")
                .hasAnyAuthority("MANAGE_ADVERTISEMENTS", "MY_RENT_REQUESTS")

                .antMatchers(HttpMethod.POST, "/api/rent-request/{id}/rent-info/{rentInfoId}/feedback")
                .hasAnyAuthority("MANAGE_ADVERTISEMENTS", "MY_RENT_REQUESTS")

                .antMatchers(HttpMethod.GET, "/api/rent-request/{id}/rent-info/{rentInfoId}/feedback")
                .hasAnyAuthority("MANAGE_ADVERTISEMENTS", "MY_RENT_REQUESTS")

                .antMatchers(HttpMethod.POST, "/api/rent-request/{id}/message")
                .hasAnyAuthority("MANAGE_ADVERTISEMENTS", "MY_RENT_REQUESTS")

                .antMatchers(HttpMethod.GET, "/api/rent-request/{id}/message")
                .hasAnyAuthority("MANAGE_ADVERTISEMENTS", "MY_RENT_REQUESTS")

                .antMatchers(HttpMethod.GET, "/api/comment/{id}")
                .hasAnyAuthority("MANAGE_ADVERTISEMENTS", "MY_RENT_REQUESTS")

                .antMatchers(HttpMethod.GET, "/api/comment/{status}/status")
                .hasAnyAuthority("MANAGE_ADVERTISEMENTS", "MY_RENT_REQUESTS")

                .anyRequest().authenticated().and()

                .cors().and()
                .addFilterBefore(new TokenAuthenticationFilter(jwtUserDetailsService.tokenUtils,
                        jwtUserDetailsService), BasicAuthenticationFilter.class);
        //        .headers().contentSecurityPolicy(
        //                        "default-src 'self' https://localhost:8090/;img-src 'self' blob: data:; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; script-src 'self' 'unsafe-eval'; font-src 'self' https://fonts.googleapis.com https://fonts.gstatic.com;");

        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers(HttpMethod.POST, "/api/auth/**");
        web.ignoring().antMatchers(HttpMethod.PUT, "/api/auth");
        web.ignoring().antMatchers(HttpMethod.PUT, "/api/client/activate**");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/advertisement/{id}");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/car/{id}/picture");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/car/{id}/picture");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/body-style");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/fuel-type");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/gearbox-type");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/make");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/make/{makeId}/models");
        web.ignoring().antMatchers(HttpMethod.POST, "/api/advertisement/search");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/advertisement/{id}");

        web.ignoring().antMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "/favicon.ico", "/favicon.png", "/**/*.html",
                "/**/*.css", "/**/*.js", "/assets/**", "/*.jpg");
    }

}