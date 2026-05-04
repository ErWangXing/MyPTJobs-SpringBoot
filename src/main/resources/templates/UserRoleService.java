package templates;

import com.MyPTJobs.TokenAuthentication1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class UserRoleService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JobSeekerService jobSeekerService;

    public boolean isValidToken(String token) {
        Authentication authentication = authenticationManager.authenticate(
                new TokenAuthentication1(token));
        authentication = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jobSeekerService.checkByToken(token);
    }

    }



