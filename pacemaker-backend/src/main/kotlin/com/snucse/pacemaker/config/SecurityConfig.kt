package com.snucse.pacemaker.config

import com.snucse.pacemaker.config.jwt.JwtConfig
import com.snucse.pacemaker.config.jwt.JwtTokenProvider
import com.snucse.pacemaker.config.jwt.RestAuthenticationEntryPoint
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class SecurityConfig(
        @Autowired private val jwtTokenProvider: JwtTokenProvider,
        @Autowired private val restAuthenticationEntryPoint: RestAuthenticationEntryPoint
): WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {

        http
                .cors().disable()
                .csrf().disable()
                .antMatcher("/api/**").authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(JwtConfig(jwtTokenProvider, restAuthenticationEntryPoint))
    }
}