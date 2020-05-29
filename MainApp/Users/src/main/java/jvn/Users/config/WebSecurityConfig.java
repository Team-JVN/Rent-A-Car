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

                .antMatchers("/api/admin", "/api/admin/{status}",
                        "/api/admin/{id}")
                .hasAuthority("MANAGE_ADMINS")

                .antMatchers(HttpMethod.PUT, "/api/admin")
                .hasAuthority("ADMIN_EDIT_PROFILE")

                .antMatchers("/api/client", "/api/client/{status}",
                        "/api/client/{id}", "/api/advertisement/{id}/block","/api/advertisement/{id}/approve",
                        "/api/advertisement/{id}/reject")
                .hasAuthority("MANAGE_CLIENTS")

                .antMatchers(HttpMethod.PUT, "/api/client")
                .hasAuthority("CLIENT_EDIT_PROFILE")

                .antMatchers("/api/agent", "/api/agent/{status}", "/api/agent/{id}")
                .hasAuthority("MANAGE_AGENTS")

                .antMatchers(HttpMethod.PUT, "/api/agent")
                .hasAuthority("AGENT_EDIT_PROFILE")

                .anyRequest().authenticated().and()

                .cors().and()

                .addFilterBefore(new TokenAuthenticationFilter(jwtUserDetailsService.tokenUtils,
                        jwtUserDetailsService), BasicAuthenticationFilter.class);
//                                .headers().contentSecurityPolicy(
//                                                "default-src 'self' https://localhost:8090/;img-src 'self' blob: data:; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; script-src 'self' 'unsafe-eval'; font-src 'self' https://fonts.googleapis.com https://fonts.gstatic.com;");
        //  http.addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(), CsrfFilter.class);
        // http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        http.csrf().disable();

//                        .antMatchers("/api/role").hasAuthority("MANAGE_ROLES")
//                .antMatchers(HttpMethod.GET, "/api/permission").hasAuthority("MANAGE_ROLES")
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers(HttpMethod.POST, "/api/auth/**");
        web.ignoring().antMatchers(HttpMethod.PUT, "/api/auth");
        web.ignoring().antMatchers(HttpMethod.PUT, "/api/client/activate**");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/advertisement/{id}");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/car/{id}/picture");
    }

}