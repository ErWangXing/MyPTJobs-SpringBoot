package com.MyPTJobs.Services;

import com.MyPTJobs.Class.Administrator;
import com.MyPTJobs.Repository.AdministratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.Optional;

@Service
@Component
public class AdministratorService {
    private Path rootLocation;
    @Autowired
    private AdministratorRepository respository;
    @Autowired
    private JavaMailSender mailSender;
    public void sendVerificationEmail(Administrator admin)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = admin.getEmail();
        String fromAddress = "myptjobsmy@gmail.com";
        String senderName = "MyPTJobs";
        String subject = "Verification Code for Reset Password";
        String content = "Dear [[name]],<br>"
                + "Your verification code is: <br>"
                + "<h3>[[code]]</h3>"
                + "Thank you,<br>"
                + "MyPTJobs";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", admin.getName());

        content = content.replace("[[code]]", admin.getVerificationCode());

        helper.setText(content, true);

        mailSender.send(message);

    }

    public Boolean checkByToken(String token){
        Optional<Administrator> administrator = respository.checkExistingByToken(token);
        if ( administrator.isPresent() ){
            return true;
        }
        return false;
    }
}