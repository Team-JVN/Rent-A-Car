package jvn.Renting.config;

import jvn.Renting.security.RestAuthenticationEntryPoint;
import jvn.Renting.security.TokenAuthenticationFilter;
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
                .antMatchers(HttpMethod.POST, "/api/rent-request").hasAuthority("MY_RENT_REQUESTS")
                .antMatchers(HttpMethod.GET, "/api/rent-request/{status}/mine",
                        "/api/rent-request/{id}")
                .hasAuthority("MY_RENT_REQUESTS")

                .antMatchers(HttpMethod.GET,
                        "/api/rent-request/{status}/advertisement/{advertisementId}")
                .hasAuthority("MANAGE_ADVERTISEMENTS")
                .antMatchers(HttpMethod.GET,
                        "/api/rent-request/{id}")
                .hasAuthority("MANAGE_ADVERTISEMENTS")
                .antMatchers(HttpMethod.POST, "/api/rent-request").hasAuthority("MANAGE_ADVERTISEMENTS")
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
        web.ignoring().antMatchers(HttpMethod.GET, "/api/car/verify/{userId}/{carId}");
    }

}