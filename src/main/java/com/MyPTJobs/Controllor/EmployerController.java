package com.MyPTJobs.Controllor;

import com.MyPTJobs.Class.*;
import com.MyPTJobs.Repository.EmployerRepository;
import com.MyPTJobs.Services.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;


@RestController // This means that this class is a Controller
@RequestMapping(path = "/employer") // This means URL's start with /demo (after Application path)
public class EmployerController {
    private final String uploadDir = "Images/Employer/";
    private Path rootLocation;
    @Autowired
    private EmployerRepository employerRepository;
    @Autowired
    private JobApplicationService jobApplicationService;
    @Autowired
    private JobInterviewService jobInterviewService;

    @Autowired
    private JobOfferService jobOfferService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private JobSeekerService jobSeekerService;
    @Autowired
    private JobService jobService;

    @Autowired
    private EmployerService employerService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    FcmService fcmService;

    @PostMapping(path = "/all")
    public @ResponseBody Iterable<Employer> getAllUsers() {
        // This returns a JSON or XML with the users
        return employerRepository.findAll();
    }

    @PostMapping(path = "/test")
    public @ResponseBody String test() {
        // This returns a JSON or XML with the users
        return "q23";
    }
    @PostMapping("/checkLogin")
    public ResponseEntity<String> checkLogin(@RequestParam("token") String token) {
        if ( token.isEmpty() ){
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        Optional<Employer> _employer = employerRepository.checkExistingByToken(token);
        if (_employer.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }
    /*
        success       = 201
        found         = 302
        Error         = 500
     */
    @PostMapping(path = "/signup")
    public ResponseEntity<String> signUp(@ModelAttribute Employer employer) {
        System.out.println(employer.getEmail());
        try {
            Optional<Employer> employerData = employerRepository.checkExisting(employer.getEmail());
            System.out.println(1);
            if (employerData.isEmpty()) {
                employer.setEmployerName("");
                employer.setCompanyName("");
                employer.setCreated_at(new Timestamp(System.currentTimeMillis()));
                employer.setVerification("Unverified");
                employer.setVerificationCode("");
                employer.setRating(0);
                Employer _employer = employerRepository
                        .save(employer);
                return new ResponseEntity<>("success", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Email Exists", HttpStatus.FOUND);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
        login success = 200
        error         = 409
        not found     = 204
        Error         = 500
     */

    @PostMapping(path = "/login")
    public ResponseEntity<Employer> login(@ModelAttribute Employer employer) {
        try {
            Optional<Employer> employerData = employerRepository.checkExisting(employer.getEmail());

            if (employerData.isPresent()) {
                if(! employerService.checkBlockOrNot(employerData.get()) ){
                    return new ResponseEntity<>(new Employer(employerData.get().getBlockDate(), employerData.get().getBlockPermanent()), HttpStatus.FORBIDDEN);
                }
                Employer _employer = employerRepository.login(employer.getEmail(), employer.getPassword());
                if (_employer != null) {
                    if (_employer.getId() > 0) {
//                        _employer.setToken(new AESUtil().encryptAES(Integer.toString(_employer.getId())));
                        _employer.generateToken();
                        employerRepository.saveToken(_employer.getToken(),employer.getNotificationToken(), _employer.getId());
                        return new ResponseEntity<>(_employer, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(null, HttpStatus.CONFLICT);
                    }
                }
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }

        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
       success       = 200
       not found       = 204
       Error       = 500
    */
    @RequestMapping(value = "/addJob", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addNewJob(@ModelAttribute Job job, @RequestPart(required = false)
        @RequestParam("file") Optional<MultipartFile> file, @RequestParam("token") String token) throws IOException {
        int employerID = employerRepository.getIdByToken(token);
        if (employerID == 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            job.setEmployerID(employerID);
        }
        return jobService.addJob(job, file);
    }

    @RequestMapping(value = "/updatePostedJob", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updatePostedJob(@ModelAttribute Job job, @RequestPart(required = false) @RequestParam("file") Optional<MultipartFile> file, @RequestParam("token") String token) throws IOException {

        try {
            if (jobService.updateJob(job, file)) {
                return new ResponseEntity<>("success", HttpStatus.OK);
            }
            return new ResponseEntity<>("failed", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }

    }

    @RequestMapping(value = "/hidePostedJob", method = RequestMethod.POST)
    public ResponseEntity<String> hidePostedJob(@RequestParam("JobID") int JobID) throws IOException {

        try {
            if (jobService.updateJobStatus(JobID, "Inactive")) {
                return new ResponseEntity<>("success", HttpStatus.OK);
            }
            return new ResponseEntity<>("failed", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }

    }

    @RequestMapping(value = "/showPostedJob", method = RequestMethod.POST)
    public ResponseEntity<String> showPostedJob(@RequestParam("JobID") int JobID) throws IOException {

        try {
            if (jobService.updateJobStatus(JobID, "Active")) {
                return new ResponseEntity<>("success", HttpStatus.OK);
            }
            return new ResponseEntity<>("failed", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }

    }

    @RequestMapping(value = "/removePostedJob", method = RequestMethod.POST)
    public ResponseEntity<String> removePostedJob(@RequestParam("JobID") int JobID) throws IOException {

        try {
            if (jobService.deleteJob(JobID)) {
                return new ResponseEntity<>("success", HttpStatus.OK);
            }
            return new ResponseEntity<>("failed", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }

    }


    @RequestMapping(value = "/postedJobLIst", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Job>> requestPostedJob(@RequestParam("token") String token) throws IOException {
        try {
//            List<Job> jobList = new jobController().getPostedJobList(token);
            List<Job> jobList = new ArrayList<Job>();
            System.out.println(token);
            jobList = jobService.getJobList(token);
            for (Job element : jobList) {
                System.out.println(element.getId());
            }
//            jobList
            return new ResponseEntity<>(jobList, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/requestSelectedPostedJob", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> requestSelectedPostedJob(@RequestParam("id") int id) throws IOException {
        try {
            Job selectedJob = new Job();
            selectedJob = jobService.getSelectedJob(id);


            return new ResponseEntity<>(selectedJob, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @PostMapping("/requestCompanyInformation")
    public ResponseEntity<Employer> requestCompanyInformation(@RequestParam("token") String token) {
        Optional<Employer> employer = employerRepository.getCompanyInformation(token);
        if (employer.isPresent()) {
            return new ResponseEntity<>(employer.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/requestCompanyDescription")
    public ResponseEntity<Employer> requestCompanyDescription(@RequestParam("token") String token) {
        Optional<Employer> employer = employerRepository.getCompanyDescription(token);
        if (employer.isPresent()) {
            return new ResponseEntity<>(employer.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/updateInformation", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Employer> updateInformation(@ModelAttribute Employer emp, @RequestPart(required = false)
    @RequestParam("file") Optional<MultipartFile> file, @RequestParam("token") String token) throws IOException {
        MultipartFile _file = file.orElse(null);
        Optional<Employer> employer = employerRepository.getCompanyInformation(emp.getToken());
        if (!employer.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        String fileName = Integer.toString(employer.get().getId())+"-"+(System.currentTimeMillis() / 1000L);
        try {
            if (_file != null) {
                rootLocation = Paths.get(uploadDir);
                if (!Files.exists(rootLocation)) {
                    Files.createDirectories(rootLocation);
                }
                String specificUploadDir = uploadDir + employer.get().getId() + "/";
                rootLocation = Paths.get(specificUploadDir);
                if (!Files.exists(rootLocation)) {
                    Files.createDirectories(rootLocation);
                }
                FileStorageProperties saveDir = new FileStorageProperties();
                saveDir.setUploadDir(specificUploadDir);
                FileStorageService fileService = new FileStorageService(saveDir);
                fileName = fileService.storeFile(_file, fileName);
                employer.get().setImageFile(employer.get().getId() + "/" + fileName);
            }
            employerRepository.updateInformation(emp.getCompanyName(), emp.getCompanyEmail(), emp.getPhoneNumber(),
                    emp.getLocation(), emp.getSsm(), employer.get().getImageFile(), employer.get().getId(),
                    emp.getState(), emp.getCity());
            Optional<Employer> _employer = employerRepository.getCompanyInformation(token);
            if (_employer.isPresent()) {
                return new ResponseEntity<>(_employer.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/updateCompanyDescription")
    public ResponseEntity<String> updateCompanyDescription(@ModelAttribute Employer emp) {
        System.out.println(emp.getCompanyDescription() + emp.getToken());
        Optional<Employer> employer = employerRepository.findIdByToken(emp.getToken());
        if (employer.isPresent()) {
            employerRepository.updateDescrption(emp.getCompanyDescription(), employer.get().getId());
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/requestOperatingHours")
    public ResponseEntity<Employer> requestOperatingHours(@RequestParam("token") String token) {
        Optional<Employer> employer = employerRepository.getOperatingHours(token);
        if (employer.isPresent()) {
            return new ResponseEntity<>(employer.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/updateOperatingHours", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateOperatingHours(@RequestParam("operatingHours") String operatingHours, @RequestParam("token") String token) {
        Optional<Employer> employer = employerRepository.findIdByToken(token);
        if (employer.isPresent()) {
            employerRepository.updateOperatingHours(operatingHours, employer.get().getId());
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/requestInformation")
    public ResponseEntity<Employer> requestInformation(@RequestParam("token") String token) {
        Optional<Employer> employer = employerRepository.getInformation(token);
        if (employer.isPresent()) {
            return new ResponseEntity<>(employer.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/getbasicinfo")
    public ResponseEntity<Employer> getbasicinfo(@RequestParam("token") String token) {
        Optional<Employer> employer = employerRepository.getbasicinfo(token);
        if (employer.isPresent()) {
            return new ResponseEntity<>(employer.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


    @GetMapping("/files/{folder:.+}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String folder, @PathVariable String filename) throws IOException {
        FileStorageProperties saveDir = new FileStorageProperties();
        saveDir.setUploadDir(uploadDir + folder + "/");
        System.out.println(saveDir.getUploadDir());
        FileStorageService fileService = new FileStorageService(saveDir);
        Resource file = fileService.loadFileAsResource(filename);
        String type = Files.probeContentType(Path.of(uploadDir + filename));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, type).body(file);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {
        FileStorageProperties saveDir = new FileStorageProperties();
        saveDir.setUploadDir(uploadDir + "/");
        System.out.println(saveDir.getUploadDir());
        FileStorageService fileService = new FileStorageService(saveDir);
        Resource file = fileService.loadFileAsResource(filename);
        String type = Files.probeContentType(Path.of(uploadDir + filename));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, type).body(file);
    }

//    @PostMapping("/updatejobsetting")
//    public ResponseEntity<Employer> updateJobSetting( @RequestParam("token") String token,  @RequestParam("jobSetting") String jobSetting){
//        System.out.println(token);
//        System.out.println(jobSetting);
//        employerRepository.updateJobSetting(jobSetting, token);
//        Employer _employer = employerRepository.getLatestBasicInfo(token);
//       return new ResponseEntity<>(_employer, HttpStatus.OK);
//    }

//    @PostMapping("/getbiodata")
//    public ResponseEntity<Employer> retrieveBiodata( @RequestParam("token") String token){
//        Employer _employer = employerRepository.getBiodata(token);
//        if (_employer != null && _employer.getId() > 0){
//            return new ResponseEntity<>(_employer, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//    }
//
//    @PostMapping("/getbasicinfo")
//    public ResponseEntity<Employer> getBasicInfo( @RequestParam("token") String token){
//        Employer _employer = employerRepository.getLatestBasicInfo(token);
//        if (_employer != null && _employer.getId() > 0){
//            return new ResponseEntity<>(_employer, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//    }


//    @PostMapping("/getwork")
//    public ResponseEntity<List<WorkExperience>> getwork( @RequestParam("token") String token){
//        Optional<Employer> _employer = employerRepository.checkExistingByToken(token);
//        if (_employer.isPresent()){
//            List<WorkExperience> workExperience = employerRepository.getListOfWorkExperience(token);
//            workExperience.forEach(System.out::println);
//            return new ResponseEntity<>(workExperience, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestParam("token") String token, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        Optional<Employer> employer = employerRepository.checkOldPassword(token);
        if (employer.isPresent()) {
            if (employer.get().getPassword().equals(oldPassword)) {
                System.out.println(newPassword);
                employerRepository.updateNewPassword(newPassword, employer.get().getId());
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Success", HttpStatus.CONFLICT);
            }

        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<String> changePassword(@RequestParam("email") String email) throws MessagingException, UnsupportedEncodingException {
        Optional<Employer> employer = employerRepository.findByEmail(email);
        if (employer.isPresent()) {
            employer.get().setVerificationCode(new function().getRandomNumberString());
            employerRepository.save(employer.get());
            employerService.sendVerificationEmail(employer.get());

            return new ResponseEntity<>("Success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Success", HttpStatus.NOT_FOUND);


        }
//        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/checkVerificationCode")
    public ResponseEntity<String> checkVerificationCode(@RequestParam("email") String email, @RequestParam("code") String code) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<Employer> employer = employerRepository.findByEmail(email);
        String token = "";
        if (employer.isPresent()) {
            if (employer.get().getVerificationCode().equals(code)) {
                employer.get().generateToken();
                employerRepository.save(employer.get());
                token = employer.get().getToken();
                return new ResponseEntity<>(token, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(token, HttpStatus.CONFLICT);
            }

        } else {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> changePassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        Optional<Employer> employer = employerRepository.checkExistingByToken(token);
        if (employer.isPresent()) {
            employerRepository.resetPassword(newPassword, employer.get().getId());
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Success", HttpStatus.CONFLICT);
        }


//        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/requestAppliedJobList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JobApplication>> requestAppliedJobList(@RequestParam("token") String token) throws IOException {
        try {
            List<JobApplication> jobList = new ArrayList<JobApplication>();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;

            Optional<Employer> employer = employerRepository.checkExistingByToken(token);
            if (employer.isPresent()) {
                jobList = jobApplicationService.getEmployerAppliedJob(employer.get().getId(), page, limit);
                return new ResponseEntity<>(jobList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(jobList, HttpStatus.NOT_FOUND);
            }
//
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/updateApplicationStatus", method = RequestMethod.POST)
    public ResponseEntity<String> updateApplicationStatus(@RequestParam("applicationID") int applicationID, @RequestParam("status") String status) throws IOException, FirebaseMessagingException {

        if (jobApplicationService.updateApplicationStatus(applicationID, status)) {
            Optional<JobApplication> jobApplication = jobApplicationService.getExistingApplication(applicationID);
            if ( jobApplication.isPresent() ){
                Job job = jobService.getSelectedJob(jobApplication.get().getJobID());
                MessageDTO note = new MessageDTO();
                if ( status.equals("Accepted") ){
                    note.setContent("Congratulations! Your Job Application has been Accepted");
                }else{
                    note.setContent("Thank you for applying. We regret to inform you that your job application has not been successful at this time.");
                }
                note.setSubject(job.getTitle());
                fcmService.sendNotificationToSpecificDevice("jobSeeker", jobApplication.get().getJobSeekerID() , note);
            }
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


    // interview
    @RequestMapping(value = "/requestInterviewList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JobInterview>> requestInterviewList(@RequestParam("token") String token) throws IOException {
        try {
            List<JobInterview> jobList = new ArrayList<JobInterview>();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;

            Optional<Employer> employer = employerRepository.checkExistingByToken(token);
            if (employer.isPresent()) {
                jobList = jobInterviewService.getEmployerInterview(employer.get().getId(), page, limit);
                return new ResponseEntity<>(jobList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(jobList, HttpStatus.NOT_FOUND);
            }
//
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }

    }

    @RequestMapping(value = "/sendInterview", method = RequestMethod.POST)
    public ResponseEntity<String> sendInterview(@ModelAttribute JobInterview jobInterview) throws FirebaseMessagingException, IOException {

        if (jobInterviewService.addInterview(jobInterview)) {
            Optional<JobApplication> jobApplication = jobApplicationService.getExistingApplication(jobInterview.getApplicationID());

           if ( jobApplication.isPresent() ){
               Job job = jobService.getSelectedJob(jobApplication.get().getJobID());
               MessageDTO note = new MessageDTO();
               note.setContent("Congratulations! You have been invited for an interview.");
               note.setSubject(job.getTitle());
               fcmService.sendNotificationToSpecificDevice("jobSeeker", jobApplication.get().getJobSeekerID() , note);
           }
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/updateInterviewStatus", method = RequestMethod.POST)
    public ResponseEntity<String> updateInterviewStatus(@RequestParam("interviewID") int interviewID, @RequestParam("status") String status) throws IOException, FirebaseMessagingException {

        if (jobInterviewService.updateEmployerInterviewStatus(interviewID, status)) {
            Optional<JobApplication> jobApplication = jobInterviewService.checkExistingJobApplication(interviewID);
            if ( jobApplication.isPresent() ){
                Job job = jobService.getSelectedJob(jobApplication.get().getJobID());
                MessageDTO note = new MessageDTO();
                if ( status.equals("Accepted") ){
                    note.setContent("We are thrilled to inform you that you have been selected for the position!");
                }else{
                    note.setContent("Thank you for your time and effort during the interview process. We regret to inform you that another candidate has been selected for the position.");
                }
                note.setSubject(job.getTitle());
                fcmService.sendNotificationToSpecificDevice("jobSeeker", jobApplication.get().getJobSeekerID() , note);
            }
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


    @RequestMapping(value = "/viewJobSeeker", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobSeeker> viewJobSeeker(@RequestPart(required = false) @RequestParam("jobSeekerID") int jobSeekerID) throws IOException {
        try {
            Optional<JobSeeker> jobSeeker = jobSeekerService.getPersonalInformation(jobSeekerID);
            if (jobSeeker.isPresent()) {
                return new ResponseEntity<>(jobSeeker.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/viewJobDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> viewJobDetail(@RequestParam("value") int value) throws IOException {
        try {
            Job jobList = new Job();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;
            jobList = jobService.getSelectedJob(value);
            jobList.setIsFavourite("0");


            return new ResponseEntity<>(jobList, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/completeJobAndRate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> completeJobAndRate(@RequestParam("applicationID") int applicationID, @RequestParam("rate") String rate) throws IOException {
        try {
            Optional<JobApplication> jobApplication = jobApplicationService.getExistingApplication(applicationID);
            if (jobApplication.isPresent()) {
                double averageRate = 0, sum = 0;
                JSONObject jsonObject = new JSONObject(rate);
                int count = 0;

                // Loop through the keys in the JSONObject
                for (String key : jsonObject.keySet()) {
                    // Get the value for the current key
                    double value = jsonObject.getDouble(key);

                    // Increment the sum and count
                    sum += value;
                    count++;
                }

                // Calculate the average
                averageRate = sum / count;
                System.out.println(averageRate);
                Rating newRate = new Rating(applicationID, rate, averageRate, jobApplication.get().getJobSeekerID());
                ratingService.save(newRate);

                System.out.println(ratingService.calcJobSeekerAverageRating(jobApplication.get().getJobSeekerID()));
                jobApplication.get().setStatus("Completed");
                jobApplicationService.updateApplicationStatus(applicationID, "Completed");

                if ( jobApplication.isPresent() ){
                    Job job = jobService.getSelectedJob(jobApplication.get().getJobID());
                    MessageDTO note = new MessageDTO();

                    note.setContent("Congratulations on completing the job! Your employer has rated your performance. We kindly request you to reciprocate by rating your employer as well. Your feedback helps us build a fair and transparent community.");

                    note.setSubject(job.getTitle());
                    fcmService.sendNotificationToSpecificDevice("jobSeeker", jobApplication.get().getJobSeekerID() , note);
                }

                return new ResponseEntity<>("Success", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
            }


        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/getRecommendation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JobSeeker>> getRecommendation(@RequestParam("jobID") int jobID) throws IOException {
        try {
            Job job = jobService.getSelectedJob(jobID);
            if (Objects.nonNull(job)) {
                List<JobSeeker> jobSeekerList = new ArrayList<>();
                jobSeekerList = jobSeekerService.getRecommendationList(job);
                return new ResponseEntity<>(jobSeekerList, HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/sendOffer", method = RequestMethod.POST)
    public ResponseEntity<String> sendOffer(@ModelAttribute JobOffer jobOffer) throws IOException, FirebaseMessagingException {

        if (jobOfferService.addOffer(jobOffer)) {
            Job job = jobService.getSelectedJob(jobOffer.getJobID());
            Employer employer = employerService.getEmployerById(job.getEmployerID());
            MessageDTO note = new MessageDTO();

            note.setContent("Congratulations! We are pleased to offer you the position of "+job.getTitle()+" at "+employer.getCompanyName()+"");

            note.setSubject(job.getTitle() + " - " + employer.getCompanyName());

            fcmService.sendNotificationToSpecificDevice("jobSeeker", jobOffer.getJobSeekerID() , note);

            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/deleteOffer")
    public ResponseEntity<String> deleteOffer(@RequestParam("jobSeekerID") int jobSeekerID, @RequestParam("jobID") int jobID) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (jobOfferService.deleteOffer(jobSeekerID, jobID)) {
            return new ResponseEntity<>("200", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("300", HttpStatus.OK);
        }


    }

    @RequestMapping(value = "/requestOfferList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JobOffer>> requestOfferList(@RequestParam("token") String token) throws IOException {
        try {
            List<JobOffer> jobList = new ArrayList<JobOffer>();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;

            Optional<Employer> employer = employerRepository.checkExistingByToken(token);
            if (employer.isPresent()) {
                jobList = jobOfferService.getEmployerOffer(employer.get().getId(), page, limit);
                return new ResponseEntity<>(jobList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(jobList, HttpStatus.NOT_FOUND);
            }
//
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }

    }

    @RequestMapping(value = "/requestRatingList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Rating>> requestRatingList(@RequestParam("token") String token) throws IOException {
        try {
            List<Rating> ratingList = new ArrayList<Rating>();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;

            Optional<Employer> employer = employerRepository.checkExistingByToken(token);
            if (employer.isPresent()) {
                ratingList = ratingService.getRatingList(employer.get().getId(), "Employer");
                return new ResponseEntity<>(ratingList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ratingList, HttpStatus.NOT_FOUND);
            }
//
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }
    @PostMapping("/viewEmployer")
    public ResponseEntity<Employer> viewEmployer(@RequestParam("id") int id) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Employer emp = employerService.requestEmployerInformation(id);
        return new ResponseEntity<>(emp, HttpStatus.OK);
    }

    @RequestMapping(value = "/requestJobSeekerRatingList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Rating>> requestJobSeekerRatingList(@RequestParam("id") int id) throws IOException {
        try {
            List<Rating> ratingList = new ArrayList<Rating>();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;

            JobSeeker jobSeeker = jobSeekerService.getJobSeekerByID(id);
            if (Objects.nonNull(jobSeeker)) {
                ratingList = ratingService.getRatingList(jobSeeker.getId(), "Job Seeker");
                return new ResponseEntity<>(ratingList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ratingList, HttpStatus.NOT_FOUND);
            }
//
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/submitVerificationDocument", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> submitVerificationDocument(@ModelAttribute Employer emp, @RequestPart(required = false)
        @RequestParam("frontIC") Optional<MultipartFile> frontIC, @RequestPart(required = false) @RequestParam("ssmImage") Optional<MultipartFile> SSMImage,
        @RequestPart(required = false) @RequestParam("selfie") Optional<MultipartFile> selfie, @RequestParam("token") String token) throws IOException {
        MultipartFile _frontIC = frontIC.orElse(null);
        MultipartFile _SSMImage = SSMImage.orElse(null);
        MultipartFile _selfie = selfie.orElse(null);
        Optional<Employer> employer = employerRepository.checkExistingByToken(emp.getToken());
        if (!employer.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        String fileNameFrontIC = Integer.toString(employer.get().getId()) + "-FrontIC"+"-"+(System.currentTimeMillis() / 1000L);
        String fileNameSSMImage = Integer.toString(employer.get().getId()) + "-SSMImage"+"-"+(System.currentTimeMillis() / 1000L);
        String fileNameSelfie = Integer.toString(employer.get().getId()) + "-Selfie"+"-"+(System.currentTimeMillis() / 1000L);
        try {
            if (_frontIC != null) {
                rootLocation = Paths.get(uploadDir);
                if (!Files.exists(rootLocation)) {
                    Files.createDirectories(rootLocation);
                }
                String specificUploadDir = uploadDir + employer.get().getId();
                rootLocation = Paths.get(specificUploadDir);
                if (!Files.exists(rootLocation)) {
                    Files.createDirectories(rootLocation);
                }
                FileStorageProperties saveDir = new FileStorageProperties();
                saveDir.setUploadDir(specificUploadDir);
                FileStorageService fileService = new FileStorageService(saveDir);
                fileNameFrontIC = fileService.storeFile(_frontIC, fileNameFrontIC);
                fileNameSSMImage = fileService.storeFile(_SSMImage, fileNameSSMImage);
                fileNameSelfie = fileService.storeFile(_selfie, fileNameSelfie);
                employer.get().setSSMImage(employer.get().getId() + "/" + fileNameSSMImage);
                employer.get().setSelfieImage(employer.get().getId() + "/" + fileNameSelfie);
                employer.get().setFrontICImage(employer.get().getId() + "/" + fileNameFrontIC);

                employer.get().setEmployerName(emp.getEmployerName());
                employer.get().setCompanyName(emp.getCompanyName());
                employer.get().setSsm(emp.getSsm());
                employer.get().setIC(emp.getIC());
                employer.get().setVerification("Pending");
                employer.get().setVerificationDate(new Timestamp(System.currentTimeMillis()));
                employerRepository.save(employer.get());
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/makeJobSeekerComplaint", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> makeJobSeekerComplaint(
            @RequestParam("id") int id, @RequestParam("reason") String reason,
            @RequestParam("other") String other, @RequestParam("token") String token) throws IOException {
        try {
            Optional<Employer> employer = employerRepository.checkExistingByToken(token);
            if ( employer.isPresent() ) {
                JobSeeker jobSeeker = jobSeekerService.getJobSeekerByID(id);
                if (Objects.nonNull(jobSeeker)) {
                    if (complaintService.recordComplaint(id, "jobSeeker", reason, other, employer.get().getId())) {
                        return new ResponseEntity<>("Success", HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Failed", HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>("Failed", HttpStatus.NOT_FOUND);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
        return new ResponseEntity<>("Failed", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/homeDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> homeDetail(@RequestParam("token") String token) {
        try {
            HashMap<String, String> map = new HashMap<>();
            int postedJob = 0;
            int totalApplicant = 0;
            int jobOffer = 0;
            int jobInterview = 0;
            int pendingRating = 0;
            int completed = 0;
            Optional<Employer> employer = employerRepository.checkExistingByToken(token);

            int page = 1;
            int limit = 1;
            int pageStart = page * limit;

            if (employer.isPresent()) {
                postedJob = jobService.getNumberJob(token);
                totalApplicant = jobApplicationService.getNumberEmployerAppliedJob(employer.get().getId(), page, limit);
                jobOffer = jobOfferService.getNumberEmployerOffer(employer.get().getId(), page, limit);
                jobInterview = jobInterviewService.getNumberEmployerInterview(employer.get().getId(), page, limit);
                pendingRating = jobApplicationService.getNumberCompletedJobApplication(employer.get().getId(), page, limit, Arrays.asList("User Accept", "Offer Accepted"));
                completed = jobApplicationService.getNumberCompletedJobApplication(employer.get().getId(), page, limit, Arrays.asList("Done"));

            }

            map.put("postedJob", Integer.toString(postedJob));
            map.put("totalApplicant", Integer.toString(totalApplicant));
            map.put("jobOffer",Integer.toString(jobOffer) );
            map.put("jobInterview", Integer.toString(jobInterview));
            map.put("pendingRating", Integer.toString(pendingRating));
            map.put("completed", Integer.toString(completed));

            return new ResponseEntity<>(map, HttpStatus.OK);

//
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }


    }

    @PostMapping("/checkCompleteness")
    public ResponseEntity<String> checkCompleteness(@RequestParam("token") String token) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<Employer> employer = employerRepository.checkExistingByToken(token);
        if (employer.isPresent()) {
            if ( !employerService.checkCompleteInformationOrNot(employer.get())){
                return new ResponseEntity<>("incomplete Data", HttpStatus.OK);
            }
            if ( !employerService.checkVerification(employer.get())){
                return new ResponseEntity<>("Unverified", HttpStatus.OK);
            }
            return new ResponseEntity<>("200", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam("token") String token) {
        Optional<Employer> _employer = employerRepository.checkExistingByToken(token);
        if (_employer.isPresent()) {
            employerRepository.removeToken(_employer.get().getId());
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

//    @PostMapping("/single-notification")
//    String  sendToSpecificDevice(
//            @RequestBody MessageDTO note,
//            @RequestParam String token) throws FirebaseMessagingException {
//
//        return fcmService.sendNotificationToSpecificDevice(note,token);
//    }


}