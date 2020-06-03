package jvn.Users.config;

import jvn.Users.security.RestAuthenticationEntryPoint;
import jvn.Users.security.TokenAuthenticationFilter;
import jvn.Users.serviceImpl.UserServiceImpl;
import org.springframework.security.web.csrf.CsrfFilter;
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
                .antMatchers("/api/admin/{id}", "/api/admin/all/{status}", "/api/agent/{id}", "/api/agent/all/{status}",
                        "/api/client", "/api/client/{id}", "/api/client/all/{status}", "/api/client/{id}/approve",
                        "/api/client/{id}/reject", "/api/client/{id}/block", "/api/client/{id}/unblock",
                        "/api/client/{id}/create-rent-requests/{status}", "/api/client/{id}/create-comments/{status}")
                .hasAuthority("MANAGE_USERS")

                .antMatchers(HttpMethod.POST, "/api/admin", "/api/agent", "/api/client")
                .hasAuthority("MANAGE_USERS")

                .antMatchers(HttpMethod.PUT, "/api/admin")
                .hasAuthority("ADMIN_EDIT_PROFILE")
                .antMatchers(HttpMethod.GET, "/api/admin/logged-in-user")
                .hasAuthority("ADMIN_EDIT_PROFILE")

                .antMatchers(HttpMethod.PUT, "/api/agent")
                .hasAuthority("AGENT_EDIT_PROFILE")
                .antMatchers(HttpMethod.GET, "/api/agent/logged-in-user")
                .hasAuthority("AGENT_EDIT_PROFILE")

                .antMatchers(HttpMethod.PUT, "/api/client")
                .hasAuthority("CLIENT_EDIT_PROFILE")
                .antMatchers(HttpMethod.GET, "/api/client/logged-in-user")
                .hasAuthority("CLIENT_EDIT_PROFILE")

                .antMatchers("/api/role").hasAuthority("MANAGE_ROLES")
                .antMatchers(HttpMethod.GET, "/api/permission").hasAuthority("MANAGE_ROLES")

                //api/client/for-rent-request
                ///api/client/verify/{clientId}
                //api/client/clients-by-id/{clientId}

                .anyRequest().authenticated().and()

                .cors().and()

                .addFilterBefore(new TokenAuthenticationFilter(jwtUserDetailsService.tokenUtils,
                        jwtUserDetailsService), BasicAuthenticationFilter.class);
//                                .headers().contentSecurityPolicy(
//                                                "default-src 'self' https://localhost:8080/;img-src 'self' blob: data:; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; script-src 'self' 'unsafe-eval'; font-src 'self' https://fonts.googleapis.com https://fonts.gstatic.com;");

        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers(HttpMethod.POST, "/api/auth/**");
        web.ignoring().antMatchers(HttpMethod.PUT, "/api/auth");
        web.ignoring().antMatchers(HttpMethod.PUT, "/api/client/activate**");


        web.ignoring().antMatchers(HttpMethod.GET, "/api/advertisement/{id}");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/car/{id}/picture");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/client/verify/{clientId}");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/client/clients-by-id/{clientId}");
    }

}
