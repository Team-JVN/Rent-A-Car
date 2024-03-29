package jvn.Advertisements.config;

import jvn.Advertisements.security.RestAuthenticationEntryPoint;
import jvn.Advertisements.security.TokenAuthenticationFilter;
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
                .antMatchers("/ws**", "/ws/**").permitAll()
                .antMatchers("/api/price-list", "/api/price-list/{id}")
                .hasAuthority("MANAGE_PRICE_LISTS")

                .antMatchers("/api/advertisement")
                .hasAuthority("MANAGE_ADVERTISEMENTS")

                .anyRequest().authenticated().and()

                .cors().and()

                .addFilterBefore(new TokenAuthenticationFilter(), BasicAuthenticationFilter.class);
                // .headers().contentSecurityPolicy("default-src 'self' https://localhost:8080/;img-src 'self' blob: data:; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; script-src 'self' 'unsafe-eval'; font-src 'self' https://fonts.googleapis.com https://fonts.gstatic.com;");

        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/ws/**");
        web.ignoring().antMatchers("/api/advertisement/car/{carId}/edit-type");
        web.ignoring().antMatchers("/api/advertisement/by-ids/{advIds}");
        web.ignoring().antMatchers("/api/advertisement/by-id/{advId}");
        web.ignoring().antMatchers("/api/advertisement/car/{carId}/check-for-delete");
        web.ignoring().antMatchers("/api/advertisement/car/{carId}/check-for-partial-edit");
    }

}
