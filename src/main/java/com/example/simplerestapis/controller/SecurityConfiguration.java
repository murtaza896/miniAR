//package com.example.simplerestapis.controller;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@EnableWebSecurity
//public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
//
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		// TODO Auto-generated method stub
//		auth.inMemoryAuthentication()
//			.withUser("bleh")
//			.password("bleh")
//			.roles("USER")
//			.and()
//			.withUser("foo")
//			.password("foo")
//			.roles("ADMIN");
////		super.configure(auth);
//	}
//	
//	@Bean
//	public PasswordEncoder getPasswordencoder() {
//		return NoOpPasswordEncoder.getInstance();
//	}
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		// TODO Auto-generated method stub
//		http.authorizeRequests()
//			.antMatchers("/**").hasRole("ADMIN")
//			.and().formLogin();
////		super.configure(http);
//	}
//	
//	
//}
