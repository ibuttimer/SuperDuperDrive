package com.udacity.jwdnd.course1.cloudstorage.config;

import com.udacity.jwdnd.course1.cloudstorage.misc.ResourceStore;
import com.udacity.jwdnd.course1.cloudstorage.services.AuthenticationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static com.udacity.jwdnd.course1.cloudstorage.config.UrlFactory.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationService authenticationService;

    public SecurityConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(this.authenticationService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers(SIGNUP_URL, LOGIN_URL, CSS_ANT_PATTERN, JS_ANT_PATTERN)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()

            .formLogin()
                .loginPage(LOGIN_URL)
                .defaultSuccessUrl(HOME_URL, true)
                .permitAll()
                .and()

            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL))
                .logoutSuccessUrl(urlWithQuery(LOGIN_URL, "logout"))
                .invalidateHttpSession(true);

    }

    @Override
    public void configure(WebSecurity web) {

        if (Boolean.parseBoolean(ResourceStore.getBundle().getString("spring.h2.console.enabled"))) {
            // allow free access to h2 console
            web.ignoring().antMatchers(H2_CONSOLE_PATTERN);
        }
    }
}