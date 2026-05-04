package com.MyPTJobs.Services;

import com.MyPTJobs.Class.Job;
import com.MyPTJobs.Class.JobSeeker;
import com.MyPTJobs.Repository.JobSeekerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Component
public class JobSeekerService {
    private Path rootLocation;
    @Autowired
    private JobSeekerRepository respository;
    @Autowired
    private WorkExperienceService workExperienceService;
    @Autowired
    private SkillService skillService;

    @Autowired
    private LanguageService langService;
    @Autowired
    private ComplaintService complaintService;


    @Autowired
    private JavaMailSender mailSender;

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<JobSeeker> getPersonalInformation(int jobSeekerID) throws IOException {
        Optional<JobSeeker> jobSeeker = respository.findByID(jobSeekerID);
        if (jobSeeker.isPresent()) {
            jobSeeker.get().setToken("");
            jobSeeker.get().setLanguageList(langService.getListofLanguage(jobSeeker.get()));
            jobSeeker.get().setSkillList(skillService.getListofSkill(jobSeeker.get()));
            jobSeeker.get().setWorkExperienceList(workExperienceService.getListofWorkingExperience(jobSeeker.get()));

        }
        return jobSeeker;

    }


    public List<JobSeeker> getRecommendationList(Job job) throws IOException {
//        List<JobSeeker> jobSeekerList = respository.getRecommendationList(job.getId());
        List<JobSeeker> result = entityManager.createNamedQuery("getRecommendationList", JobSeeker.class)
                .setParameter(1, job.getId())
                .getResultList();
        return result;

    }

//    public void sendVerificationEmail(JobSeeker jobSeeker) throws MessagingException, UnsupportedEncodingException {
//        String toAddress = jobSeeker.getEmail();
//        String fromAddress = "myptjobsmy@gmail.com";
//        String senderName = "MyPTJobs";
//        String subject = "Verification Code for Reset Password";
//        String content = "Dear [[name]],<br>" + "Your verification code is: <br>" + "<h3>[[code]]</h3>" + "Thank you,<br>" + "MyPTJobs";
//
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message);
//
//        helper.setFrom(fromAddress, senderName);
//        helper.setTo(toAddress);
//        helper.setSubject(subject);
//
//        content = content.replace("[[name]]", jobSeeker.getName());
////        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
//
//        content = content.replace("[[code]]", jobSeeker.getVerificationCode());
//
//        helper.setText(content, true);
//
//        mailSender.send(message);
//
//    }
    public void sendVerificationEmail(JobSeeker jobSeeker) throws MessagingException, UnsupportedEncodingException {
        String toAddress = jobSeeker.getEmail();
        String fromAddress = "myptjobsmy@gmail.com";
        String senderName = "MyPTJobs";
        String subject = "Verification Code for Reset Password, " + jobSeeker.getName();
        String content = "Dear " + jobSeeker.getName() + ",<br><br>"
                + "We hope this email finds you well! As requested, we're sending you the verification code to reset your password.<br><br>"
                + "<h3>" + jobSeeker.getVerificationCode() + "</h3><br>"
                + "To reset your password, please follow these steps:<br>"
                + "1) Go to the reset password page<br>"
                + "2) Enter the verification code<br>"
                + "3) Set a new password<br><br>"
                + "If you have any questions or need further assistance, please don't hesitate to contact us at support@myptjobs.com.<br><br>"
                + "Thank you for choosing MyPTJobs!<br><br>"
                + "Best regards,<br>"
                + "MyPTJobs team";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }


    public List<JobSeeker> requestKYCList(String filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<JobSeeker> query = cb.createQuery(JobSeeker.class);
        Root<JobSeeker> root = query.from(JobSeeker.class);
        List<Predicate> predicates = new ArrayList<>();
        // add conditions to the predicates list

        if (!filter.isEmpty()) {
            predicates.add(cb.like(root.get("name"), "%" + filter + "%"));
        }

        predicates.add(cb.equal(root.get("verification"), "Pending"));

        // combine the predicates using a logical operator (AND or OR)
        Predicate combinedPredicate = cb.and(predicates.toArray(new Predicate[0]));

        // use constructor expression to select specific attributes
        query.select(cb.construct(JobSeeker.class, root.get("name"), root.get("imageFile"), root.get("verificationDate"), root.get("id")));

        query.where(combinedPredicate);

        List<JobSeeker> results = entityManager.createQuery(query).getResultList();
        return results;
    }

    public JobSeeker getKYCVerficationInformation(@RequestParam("id") int id) {
        Optional<JobSeeker> jobSeeker = respository.getVerificationInformation(id);
        if (jobSeeker.isPresent()) {
            return jobSeeker.get();
        }
        return new JobSeeker();
    }

    public boolean updateKYCVerification(int id, String status) {
        Optional<JobSeeker> jobSeeker = respository.findByID(id);
        System.out.println(status);
        if (jobSeeker.isPresent()) {
            if (status.equals("Accepted")) {
                respository.updateVerification("Verified", jobSeeker.get().getId());
                return true;

            } else if (status.equals("Rejected")) {
                respository.updateVerification("Failed", jobSeeker.get().getId());
                return true;

            }
            return false;
        } else {
            return false;
        }
    }

    public JobSeeker getJobSeekerByID(int jobSeekerID) {
        Optional<JobSeeker> jobSeeker = respository.findByID(jobSeekerID);
        if (jobSeeker.isPresent()) {
            return jobSeeker.get();
        } else {
            return new JobSeeker();
        }
    }

    public int getNumberJobSeeker() {
        return respository.getNumberOfJobSeeker();
    }

    public int getNumberPendingVerification() {
        return respository.getNumberPendingVerification();
    }

    public void deactive(JobSeeker jobSeeker, String blockDate, String type) {
        int permanent = 0;
        if ( type.equalsIgnoreCase("Permanent") ){
            permanent = 1;
        }
        respository.deactive(jobSeeker.getId(), blockDate, permanent);
    }

    public void sendWarning(JobSeeker jobSeeker, String title, String contentDetail, String type) throws MessagingException, UnsupportedEncodingException {
        if ( jobSeeker.getEmail() == null || jobSeeker.getEmail().isBlank() || jobSeeker.getEmail().isEmpty() ){
            return;
        }String toAddress = jobSeeker.getEmail();
        String fromAddress = "myptjobsmy@gmail.com";
        String senderName = "MyPTJobs";
        String subject = title;
//        String content = "Dear [[name]],<br>" + contentDetail + "<br><br>Thank you,<br>" + "MyPTJobs";
        String content = "Dear " + jobSeeker.getName() + ",<br><br>"
                + "We have received your complaint regarding the " + type + ". After reviewing the information provided, we have determined that there may be cause for concern.<br><br>We take all complaints seriously and will investigate the issue as soon as possible. Please be advised that false or misleading complaints may result in disciplinary action against the complainant.<br><br>"
                + "Please note that the following reason(s) were provided for your complaint:<br>" + contentDetail
                + "<br><br>Thank you for bringing this matter to our attention. We will contact you if further information is required.<br><br>"
                + "Sincerely,<br>"
                + "MyPTJobs team";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

//        content = content.replace("[[name]]", jobSeeker.getName());
//        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

        helper.setText(content, true);

        mailSender.send(message);

    }

    public void sendBlockedEmail(JobSeeker jobSeeker, String title, String formattedDateTime, String type) throws MessagingException, UnsupportedEncodingException {
        if ( jobSeeker.getEmail() == null || jobSeeker.getEmail().isBlank() || jobSeeker.getEmail().isEmpty() ){
            return;
        }String toAddress = jobSeeker.getEmail();
        String fromAddress = "myptjobsmy@gmail.com";
        String senderName = "MyPTJobs";
        String subject = title;
//        String content = "Dear [[name]],<br>" + contentDetail + "<br><br>Thank you,<br>" + "MyPTJobs";
        String content = "Dear " + jobSeeker.getName() + ",<br><br>";
        content += "<h2>Your account has been blocked</h2>";
        content += "<p>We are sorry to inform you that your account has been blocked due to a violation of our terms and conditions.</p>";
        content += "<p>You will be able to access your account again at " + formattedDateTime + ".</p>";
        content += "<p>If you have any questions or concerns, please do not hesitate to contact us.</p>"
                + "Sincerely,<br>"
                + "MyPTJobs team";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

//        content = content.replace("[[name]]", jobSeeker.getName());
//        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

        helper.setText(content, true);

        mailSender.send(message);

    }

    public Boolean checkByToken(String token) {
        if ( token.isEmpty() ){
            return false;
        }
        if (!token.isEmpty()) {
            Optional<JobSeeker> _jobSeeker = respository.checkExistingByToken(token);
            if (_jobSeeker.isPresent()) {
                return checkBlockOrNot(_jobSeeker.get());
            }

        }
        return false;
    }

    public boolean checkBlockOrNot(JobSeeker jobSeeker){
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp currentTime = new Timestamp(currentTimeMillis);
        if ( (jobSeeker.getBlockDate() == null || currentTime.after(jobSeeker.getBlockDate())) && jobSeeker.getBlockPermanent() == 0 ){
            return true;
        }
        return false;
    }

    public boolean checkCompleteInformationOrNot(JobSeeker jobSeeker){
        String completeness = respository.checkJobseekerCompleteness(jobSeeker.getId());
        if ( completeness.equals("complete") ){
            return true;
        }
        return false;
    }

    public boolean checkVerification(JobSeeker jobSeeker){
        String verification = respository.checkJobseekerVerification(jobSeeker.getId());
        if ( verification.equals("Verified") ){
            return true;
        }
        return false;
    }


}