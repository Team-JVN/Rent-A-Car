package jvn.Cars.config;

import jvn.Cars.security.RestAuthenticationEntryPoint;
import jvn.Cars.security.TokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()
                .authorizeRequests()

                .antMatchers("/api/car", "/api/car/{id}", "/api/car/{id}/partial")
                .hasAuthority("MANAGE_CARS")

                .antMatchers("/api/car/statistics/{filter}")
                .hasAuthority("GET_STATISTICS")

                .antMatchers("/api/car/verify/{userId}/{carId}")
                .hasAuthority("MANAGE_ADVERTISEMENTS")

                .antMatchers("/api/body-style/{id}", "/api/fuel-type/{id}", "/api/gearbox-type", "/api/gearbox-type/{id}",
                        "/api/make/{id}", "/api/make/{makeId}/model/{modelId}")
                .hasAuthority("MANAGE_CODE_BOOKS")

                .antMatchers(HttpMethod.POST, "/api/body-style", "/api/fuel-type", "/api/gearbox-type",
                        "/api/make/", "/api/make/{makeId}/model")
                .hasAuthority("MANAGE_CODE_BOOKS")

                .anyRequest().authenticated().and()

                .cors().and()

                .addFilterBefore(new TokenAuthenticationFilter(), BasicAuthenticationFilter.class);

        ///verify/{userId}/{carId}
        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.GET, "/api/car/{id}/picture");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/body-style");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/fuel-type");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/gearbox-type");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/make");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/make/{makeId}/models");
    }

}
