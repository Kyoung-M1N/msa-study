package com.msa.configserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Value("${admin.username}")
	private String username;

	@Value("${admin.password}")
	private String password;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//  config-client는 http://ip:port/저장소이름/저장소환경 의 경로로 설정파일에 접근하게 된다.
	// 이름-환경.properties
	// 이름-환경.yml
	// 위의 파일명들이 아래의 경로로 바뀌게 된다.
	// /이름/환경
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf((csrf) -> csrf.disable())
			.authorizeHttpRequests((auth) -> auth.anyRequest().authenticated())
			.httpBasic(Customizer.withDefaults())
			.build();
	}

	// 내부에서 config를 주고 받기 위해 사용되는 계정은 많은 수가 필요하지 않기 때문에 in-memory로 저장
	// 아래는 직접 userdetails를 작성하여 메모리에 계정 정보를 저장하는 코드
	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails user = User.builder()
			.username(username)
			.password(bCryptPasswordEncoder().encode(password))
			.roles("ADMIN")
			.build();
		return new InMemoryUserDetailsManager(user);
	}
}
