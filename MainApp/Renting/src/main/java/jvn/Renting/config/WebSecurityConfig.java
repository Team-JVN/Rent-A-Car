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
                .antMatchers("/ws**", "/ws/**", "/rentrequest/ws**", "/rentrequest/ws/**", "/rentrequest/ws",
                        "/rentreport/ws**", "/rentreport/ws/**", "/rentreport/ws",
                        "/comment/ws**", "/comment/ws/**", "/comment/ws").permitAll()

                .antMatchers(HttpMethod.POST, "/api/rent-request").hasAuthority("MY_RENT_REQUESTS")
                .antMatchers(HttpMethod.GET, "/api/rent-request/{status}/mine",
                        "/api/rent-request/{id}")
                .hasAuthority("MY_RENT_REQUESTS")
                .antMatchers(HttpMethod.PUT, "/api/rent-request/{rentRequestId}/rent-info/{rentInfoId}/pay")
                .hasAuthority("MY_RENT_REQUESTS")

                .antMatchers(HttpMethod.GET,
                        "/api/rent-request/{status}/advertisement/{advertisementId}")
                .hasAuthority("MANAGE_ADVERTISEMENTS")
                .antMatchers(HttpMethod.GET,
                        "/api/rent-request/{id}")
                .hasAuthority("MANAGE_ADVERTISEMENTS")
                .antMatchers(HttpMethod.POST, "/api/rent-request")
                .hasAuthority("MANAGE_ADVERTISEMENTS")

                .antMatchers(HttpMethod.PUT, "/api/rent-request/{id}")
                .hasAnyAuthority("MANAGE_ADVERTISEMENTS", "MY_RENT_REQUESTS")

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

                .antMatchers(HttpMethod.POST, "/api/rent-report/{id}")
                .hasAnyAuthority("MANAGE_ADVERTISEMENTS")

                .antMatchers(HttpMethod.GET, "/api/comment/{id}")
                .hasAnyAuthority("MANAGE_COMMENTS")

                .antMatchers(HttpMethod.GET, "/api/comment/{status}/status")
                .hasAnyAuthority("MANAGE_COMMENTS")

                .anyRequest().authenticated().and()

                .cors().and()

                .addFilterBefore(new TokenAuthenticationFilter(), BasicAuthenticationFilter.class);
//                               .headers().contentSecurityPolicy(
//                                               "default-src 'self' https://localhost:8080/;img-src 'self' blob: data:; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; script-src 'self' 'unsafe-eval'; font-src 'self' https://fonts.googleapis.com https://fonts.gstatic.com;");

        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers("/ws/**");
        web.ignoring().antMatchers("/rentrequest/ws/**");
        web.ignoring().antMatchers("/rentrequest/ws**");
        web.ignoring().antMatchers("/rentrequest/ws");
        web.ignoring().antMatchers("/rentreport/ws/**");
        web.ignoring().antMatchers("/rentreport/ws**");
        web.ignoring().antMatchers("/rentreport/ws");
        web.ignoring().antMatchers("/comment/ws/**");
        web.ignoring().antMatchers("/comment/ws**");
        web.ignoring().antMatchers("/comment/ws");

        web.ignoring().antMatchers(HttpMethod.GET, "/api/car/verify/{userId}/{carId}");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/rent-request/advertisement/{advId}/check-for-delete");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/rent-request/advertisement/{advId}/edit-type");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/rent-request/advertisement/{advIds}/check-rent-infos");

    }

}
