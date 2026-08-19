package hei.school.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import hei.school.app.security.authorizer.CourseAccessAuthorizationManager;
import hei.school.app.security.authorizer.ExamAccessAuthorizationManager;
import hei.school.app.security.authorizer.GradeAccessAuthorizationManager;
import hei.school.app.security.authorizer.StudentAccessAuthorizationManager;
import hei.school.app.security.jwt.JwtAuthFilter;
import hei.school.app.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
  private final JwtService jwtService;
  private final CourseAccessAuthorizationManager courseAccessAuthorizationManager;
  private final ExamAccessAuthorizationManager examAccessAuthorizationManager;
  private final GradeAccessAuthorizationManager gradeAccessAuthorizationManager;
  private final StudentAccessAuthorizationManager studentAccessAuthorizationManager;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/students/{studentId}/**").access(studentAccessAuthorizationManager)
            .requestMatchers(HttpMethod.GET, "/courses").authenticated()
            .requestMatchers(HttpMethod.GET, "/courses/{courseId}").access(courseAccessAuthorizationManager)
            .requestMatchers(HttpMethod.POST, "/courses/{courseId}/exams").access(courseAccessAuthorizationManager)
            .requestMatchers(HttpMethod.GET, "/exams/{examId}").access(examAccessAuthorizationManager)
            .requestMatchers(HttpMethod.PUT, "/exams/{examId}").access(examAccessAuthorizationManager)
            .requestMatchers(HttpMethod.DELETE, "/exams/{examId}").access(examAccessAuthorizationManager)
            .requestMatchers(HttpMethod.GET, "/grades/{gradeId}").access(gradeAccessAuthorizationManager)
            .requestMatchers(HttpMethod.PUT, "/students/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/courses", "/cursus/**", "/groups/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/courses/**", "/cursus/**", "/groups/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/courses/**", "/cursus/**", "/groups/**").hasRole("ADMIN")

            .anyRequest().authenticated())
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(new JwtAuthFilter(jwtService, userDetailsService),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    var provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}
