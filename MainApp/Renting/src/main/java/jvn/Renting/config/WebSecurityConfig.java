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
                .antMatchers(HttpMethod.PUT, "/api/rent-request/{rentRequestId}/rent-info/{rentInfoId}/pay")
                .hasAuthority("MY_RENT_REQUESTS")

                .antMatchers(HttpMethod.GET,
                        "/api/rent-request/{status}/advertisement/{advertisementId}",
                        "/api/rent-request/advertisement/{advId}/check-for-delete",
                        "/api/rent-request/advertisement/{advId}/edit-type")
                .hasAuthority("MANAGE_ADVERTISEMENTS")
                .antMatchers(HttpMethod.GET,
                        "/api/rent-request/{id}")
                .hasAuthority("MANAGE_ADVERTISEMENTS")
                .antMatchers(HttpMethod.POST, "/api/rent-request")
                .hasAuthority("MANAGE_ADVERTISEMENTS")

                .antMatchers(HttpMethod.GET, "/api/rent-request/advertisement/{advIds}/check-rent-infos")
                .hasAuthority("MANAGE_CARS")

                .antMatchers(HttpMethod.PUT, "/api/rent-request/{id}")
                .hasAnyAuthority("MANAGE_ADVERTISEMENTS", "MY_RENT_REQUESTS")

                .anyRequest().authenticated().and()

                .cors().and()

                .addFilterBefore(new TokenAuthenticationFilter(), BasicAuthenticationFilter.class);

        ///verify/{userId}/{carId}
        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers(HttpMethod.GET, "/api/car/verify/{userId}/{carId}");
    }

}
