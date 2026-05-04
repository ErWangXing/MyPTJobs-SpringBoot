package com.MyPTJobs;

import com.MyPTJobs.Services.AdministratorService;
import com.MyPTJobs.Services.EmployerService;
import com.MyPTJobs.Services.JobSeekerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JobSeekerService jobSeekerService;
    @Autowired
    private EmployerService employerService;
    @Autowired
    private AdministratorService administratorService;

//    antMatchers("/api/jobseeker/register", "/api/jobseeker/login", "/api/jobseeker/token/refresh").permitAll()
//                .antMatchers(HttpMethod.GET, "/api/job/**", "/api/employer/**", "/api/jobseeker/**").permitAll()
//                .antMatchers(HttpMethod.POST, "/api/jobseeker/forgot-password").permitAll()
//                .antMatchers("/api/job/**").hasAnyRole("JOB_SEEKER", "EMPLOYER", "ADMIN")
//                .antMatchers("/api/employer/**").hasAnyRole("EMPLOYER", "ADMIN")
//                .antMatchers("/api/admin/**").hasRole("ADMIN")
//                .anyRequest().authenticated()
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
//                .antMatchers("/public/**").permitAll()
                .antMatchers("/*/files/**").permitAll()
                .antMatchers("/resources/**").permitAll()
//                .antMatchers("/**").permitAll()
                .antMatchers("/admin/**").permitAll()
//                .antMatchers("/jobseeker/**").permitAll()
                .antMatchers("/jobseeker/login", "/jobseeker/resetPassword", "/jobseeker/forgetPassword", "/jobseeker/checkVerificationCode", "/jobseeker/signup", "/jobseeker/googlesignup", "/jobseeker/requestJobList", "/jobseeker/viewEmployer", "/jobseeker/requestJob", "/jobseeker/requestEmployerRatingList").permitAll()
 .antMatchers("/employer/login", "/employer/resetPassword", "/employer/forgetPassword", "/employer/checkVerificationCode", "/employer/signup", "/employer/requestJobSeekerRatingList", "/employer/viewEmployer").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jobSeekerService, employerService, administratorService), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
//        auth.userDetailsService(jobSeekerService).passwordEncoder(passwordEncoder());
//        auth.userDetailsService(employerService).passwordEncoder(passwordEncoder());
//        auth.userDetailsService(adminService).passwordEncoder(passwordEncoder());

    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
