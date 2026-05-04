package com.MyPTJobs;

import com.MyPTJobs.Services.AdministratorService;
import com.MyPTJobs.Services.EmployerService;
import com.MyPTJobs.Services.JobSeekerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JobSeekerService jobSeekerService;
    @Autowired
    private EmployerService employerService;
    @Autowired
    private AdministratorService administratorService;

    public JwtAuthenticationFilter(JobSeekerService jobSeekerService, EmployerService employerService, AdministratorService administratorService) {
        this.jobSeekerService = jobSeekerService;
        this.employerService = employerService;
        this.administratorService = administratorService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
//        String token = getTokenFromRequest(request);
//        if (token != null && jobSeekerService.checkByToken(token)) {
//            Authentication auth = new UsernamePasswordAuthenticationToken(new AuthenticatedUser(token), null, null);
//            SecurityContextHolder.getContext().setAuthentication(auth);
//        }
        String token = getTokenFromRequest(request);
        if (token != null) {
            // Check if the token is valid
            if (jobSeekerService.checkByToken(token)) {
                // If the token belongs to a job seeker, set the authentication accordingly
                Authentication auth = new UsernamePasswordAuthenticationToken(new AuthenticatedUser(token, "JOB_SEEKER"), null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else if (employerService.checkByToken(token)) {
                // If the token belongs to an employer, set the authentication accordingly
                Authentication auth = new UsernamePasswordAuthenticationToken(new AuthenticatedUser(token, "EMPLOYER"), null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
//            else if (administratorService.checkByToken(token)) {
//                // If the token belongs to an administrator, set the authentication accordingly
//                Authentication auth = new UsernamePasswordAuthenticationToken(new AuthenticatedUser(token, "ADMINISTRATOR"), null, null);
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        String token = request.getParameter("token");
        if (token != null) {
            return token;
        }
        return null;
    }

}
