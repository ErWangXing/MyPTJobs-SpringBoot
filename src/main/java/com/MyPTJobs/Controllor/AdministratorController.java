package com.MyPTJobs.Controllor;

import com.MyPTJobs.Class.*;
import com.MyPTJobs.Repository.AdministratorRepository;
import com.MyPTJobs.Services.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController // This means that this class is a Controller
@RequestMapping(path = "/admin") // This means URL's start with /demo (after Application path)
public class AdministratorController {
    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private EmployerService employerService;

    @Autowired
    private JobSeekerService jobSeekerService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private JobService jobService;

    @Autowired
    private AdministratorRepository repository;

    @Autowired
    private RatingService ratingService;

    @Autowired
    FcmService fcmService;

    @PostMapping(path = "/login")
    public ResponseEntity<Administrator> login(@ModelAttribute Administrator administrator) {
        try {
            Optional<Administrator> adminData = repository.checkExisting(administrator.getEmail());

            if (adminData.isPresent()) {
                Optional<Administrator> admin = repository.login(adminData.get().getAdminID(), administrator.getPassword());
                if (admin.isPresent()) {
                    if (admin.get().getAdminID() > 0) {
//                        _employer.setToken(new AESUtil().encryptAES(Integer.toString(_employer.getId())));
                        admin.get().generateToken();
                        repository.saveToken(admin.get().getToken(), admin.get().getAdminID());
                        return new ResponseEntity<>(admin.get(), HttpStatus.OK);
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

    @PostMapping("/checkLogin")
    public ResponseEntity<String> checkLogin(@RequestParam("token") String token) {
        if ( token.isEmpty() ){
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        Optional<Administrator> _administrator = repository.checkExistingByToken(token);
        if (_administrator.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestParam("token") String token, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        Optional<Administrator> admin = repository.checkOldPassword(token);
        if (admin.isPresent()) {
            if (admin.get().getPassword().equals(oldPassword)) {
                System.out.println(newPassword);
                repository.updateNewPassword(newPassword, admin.get().getAdminID());
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Success", HttpStatus.CONFLICT);
            }

        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<String> forgetPassword(@RequestParam("email") String email) throws MessagingException, UnsupportedEncodingException {
        Optional<Administrator> admin = repository.findByEmail(email);
        if (admin.isPresent()) {
            admin.get().setVerificationCode(new function().getRandomNumberString());
            repository.save(admin.get());
            administratorService.sendVerificationEmail(admin.get());

            return new ResponseEntity<>("Success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Success", HttpStatus.NOT_FOUND);


        }
//        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/checkVerificationCode")
    public ResponseEntity<String> checkVerificationCode(@RequestParam("email") String email, @RequestParam("code") String code) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<Administrator> admin = repository.findByEmail(email);
        String token = "";
        if (admin.isPresent()) {
            if (admin.get().getVerificationCode().equals(code)) {
                admin.get().generateToken();
                repository.save(admin.get());
                token = admin.get().getToken();
                return new ResponseEntity<>(token, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(token, HttpStatus.CONFLICT);
            }

        } else {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        Optional<Administrator> admin = repository.checkExistingByToken(token);
        if (admin.isPresent()) {
            repository.resetPassword(newPassword, admin.get().getAdminID());
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Success", HttpStatus.CONFLICT);
        }


//        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/requestKYBList")
    public ResponseEntity<List<Employer>> requestKYBList(@RequestParam("search") String search) {
        List<Employer> employerList = employerService.requestKYBList(search);

        if (employerList.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(employerList, HttpStatus.OK);
    }

    @PostMapping("/requestKYBDetail")
    public ResponseEntity<Employer> requestKYBList(@RequestParam("id") int id) {
        Employer employer = employerService.getKYBVerficationInformation(id);

//        if ( employer.isEmpty() ){
        return new ResponseEntity<>(employer, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(employerList, HttpStatus.OK);
    }

    @PostMapping("/manageKYBVerification")
    public ResponseEntity<String> manageKYBVerification(@RequestParam("id") int id, @RequestParam("status") String status) throws IOException {
        try {
            System.out.println(employerService.updateKYBVerification(id, status));

            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @PostMapping("/requestKYCList")
    public ResponseEntity<List<JobSeeker>> requestKYCList(@RequestParam("search") String search) {
        List<JobSeeker> jobSeekerList = jobSeekerService.requestKYCList(search);

        if (jobSeekerList.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(jobSeekerList, HttpStatus.OK);
    }

    @PostMapping("/requestKYCDetail")
    public ResponseEntity<JobSeeker> requestKYCDetail(@RequestParam("id") int id) {
        JobSeeker jobSeeker = jobSeekerService.getKYCVerficationInformation(id);

//        if ( employer.isEmpty() ){
        return new ResponseEntity<>(jobSeeker, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(employerList, HttpStatus.OK);
    }

    @PostMapping("/manageKYCVerification")
    public ResponseEntity<String> manageKYCVerification(@RequestParam("id") int id, @RequestParam("status") String status) throws IOException {
        try {
            System.out.println(jobSeekerService.updateKYCVerification(id, status));

            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/requestEmployerList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Employer>> requestEmployerList(@RequestParam("filter") String filter) throws IOException {
        try {
            List<Employer> employerList = new ArrayList<Employer>();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;
            return new ResponseEntity<>(employerService.getEmployerList(filter), HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }

    }

    @PostMapping("/requestEmployerInformation")
    public ResponseEntity<Employer> requestEmployerInformation(@RequestParam("employerID") int employerID) {
        Employer employer = employerService.getEmployerById(employerID);
            return new ResponseEntity<>(employer, HttpStatus.OK);
    }
    @RequestMapping(value = "/requestCompanyInformation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Employer> requestCompanyInformation(@RequestParam("employerID") int employerID) {
        Employer employer = employerService.getCompanyInformationByID(employerID);
        return new ResponseEntity<>(employer, HttpStatus.OK);
    }
    @RequestMapping(value = "/requestCompanyDescription", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Employer> requestCompanyDescription(@RequestParam("employerID") int employerID) {
        Employer employer = employerService.getEmployerCompanyDescriptionByID(employerID);
            return new ResponseEntity<>(employer, HttpStatus.OK);
    }

    @RequestMapping(value = "/updateInformation", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Employer> updateInformation(@ModelAttribute Employer emp, @RequestPart(required = false) @RequestParam("file") Optional<MultipartFile> file, @RequestParam("employerID") int employerID) throws IOException {
        if (employerService.updateCompanyBackground(emp, file, employerID) ){
            return requestCompanyInformation(employerID);
        }else{
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/updateCompanyDescription")
    public ResponseEntity<String> updateCompanyDescription(@ModelAttribute Employer emp, @RequestParam("employerID") int employerID) {
//        System.out.println(emp.getCompanyDescription() + emp.getToken());
        Employer employer = employerService.getEmployerById(employerID);
        if (Objects.nonNull(employer)) {
            employerService.updateCompanyInformation(emp.getCompanyDescription(), employerID);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/requestOperatingHours")
    public ResponseEntity<Employer> requestOperatingHours(@RequestParam("employerID") int employerID) {
        Employer employer = employerService.getEmployerOperatingHoursByID(employerID);
        return new ResponseEntity<>(employer, HttpStatus.OK);
    }

    @RequestMapping(value = "/updateOperatingHours", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateOperatingHours(@RequestParam("operatingHours") String operatingHours, @RequestParam("employerID") int employerID) {
        Employer employer = employerService.getEmployerById(employerID);
        if (Objects.nonNull(employer)) {
            employerService.updateOperatingHours(operatingHours, employerID);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/homeDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> homeDetail(@RequestParam("token") String token) {
        try {
            HashMap<String, String> map = new HashMap<>();
            int totalUser = 0;
            int totalEmployer = 0;
            int pendingKYC = 0;
            int pendingKYB = 0;
            int successCase = 0;
            int complaint = 0;

            int page = 1;
            int limit = 1;
            int pageStart = page * limit;

            totalUser = jobSeekerService.getNumberJobSeeker();
            totalEmployer = employerService.getNumberEmployer();
            pendingKYC = jobSeekerService.getNumberPendingVerification();
            pendingKYB = employerService.getNumberPendingVerification();
//            successCase = jobApplicationService.getNumberCompletedJobApplication(employer.get().getId(), page, limit, Arrays.asList("User Accept", "Offer Accepted"));
            complaint = complaintService.getNumberComplaint();


            map.put("totalUser", Integer.toString(totalUser));
            map.put("totalEmployer", Integer.toString(totalEmployer));
            map.put("pendingKYC",Integer.toString(pendingKYC) );
            map.put("pendingKYB", Integer.toString(pendingKYB));
            map.put("successCase", Integer.toString(successCase));
            map.put("complaint", Integer.toString(complaint));


            return new ResponseEntity<>(map, HttpStatus.OK);

//
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/requestComplaintList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Complaint>> requestComplaintList() throws IOException {
        try {
            List<Complaint> complaintList = new ArrayList<>();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;
            return new ResponseEntity<>(complaintService.getUnprocessComplaints(), HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }

    }

//    @PostMapping("/requestComplaint")
//    public ResponseEntity<Complaint> requestComplaint(@RequestParam("id") int id) {
//        Complaint complaint = complaintService.getComplaintDetail(id);
//        if ( Objects.nonNull(complaint) ){
//            return new ResponseEntity<>(complaint, HttpStatus.OK);
//
//        }
//        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
//    }

    @PostMapping("/requestComplaint")
    public ResponseEntity<List<Complaint>> requestComplaint(@RequestParam("id") int id, @RequestParam("type") String type) {
        List<Complaint> complaintList = complaintService.getComplaintsByIDandType(id, type);
        return new ResponseEntity<>(complaintList, HttpStatus.OK);    }

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

    @RequestMapping(value = "/getPossibleComplaint", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> getPossibleComplaint(@RequestParam("id") int complaintID, @RequestParam("type") String type) {
        try {
            Complaint complaint = complaintService.getComplaintDetail(complaintID);
            HashMap<String, String> map = new HashMap<>();
            List<String> countries = new ArrayList<>();
            try {

                List<Complaint> complaintList = complaintService.getPreviousAction(complaint, type);
                List<String> strings = new ArrayList<>(complaintList.size());
                for (Complaint complaint1 : complaintList) {
                    JSONObject obj=new JSONObject();
//                    obj.put("reason",complaint1.getReason());
                    obj.put("date", complaint1.getCreatedAt());
//                    obj.put("other",complaint1.getOthers());
                    obj.put("action",complaint1.getAction());
                    strings.add(obj.toString());
                }
//                complaintList.sort(Comparator.comparing(String::toString));
                map.put("history", strings.toString());
                map.put("action", complaint.showAction(complaintList.size()));

            } catch (Exception e) {System.out.println(countries);
                e.printStackTrace();
            }

//            complaintService.getPossibleComplaint(complaintID);


//            map.put("totalUser", Integer.toString(totalUser));
//            map.put("totalEmployer", Integer.toString(totalEmployer));
//            map.put("pendingKYC",Integer.toString(pendingKYC) );
//            map.put("pendingKYB", Integer.toString(pendingKYB));
//            map.put("successCase", Integer.toString(successCase));
//            map.put("complaint", Integer.toString(complaint));


            return new ResponseEntity<>(map, HttpStatus.OK);

//
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }



    }

//    @PostMapping("/takeActionIntoComplaint")
//    public ResponseEntity<String> takeActionIntoComplaint(@RequestParam("id") int id,@RequestParam("status") String status ) throws MessagingException, IOException {
//        Complaint complaint = complaintService.getComplaintDetail(id);
//        if ( Objects.nonNull(complaint) ){
//            if ( status.equals("process") ){
//                if ( complaintService.processComplaint(id) ){
//                    return new ResponseEntity<>("Success", HttpStatus.OK);
//                }
//            }else{
//                complaintService.updateComplaintStatus(complaint,"Failed");
//                return new ResponseEntity<>("Failed", HttpStatus.OK);
//            }
//
//
//        }
//        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
//    }
    @PostMapping("/takeActionIntoComplaint")
    public ResponseEntity<String> takeActionIntoComplaint(@RequestParam("ids") List<String> ids,@RequestParam("status") String status ,@RequestParam("action") String action,@RequestParam("type") String type,@RequestParam("num") int num,@RequestParam("typeOfPeriod") String typeOfPeriod) throws MessagingException, IOException {
//        String[] strArray = idList.replaceAll("[\\[\\]]", "").split(",\\s*");
//        List<Integer> list = new ArrayList<>();
//
//        String ids = String.join(",", strArray);
        List<Integer> idList = new ArrayList<>();
        for (String str : ids) {
            System.out.println(str);
            idList.add(Integer.parseInt(str));
        }

        boolean validIdsOrNot = complaintService.getManyCompaints(idList);
        if ( validIdsOrNot ){
            if ( status.equals("process") ){
                if ( complaintService.processComplaint(idList, status, action, type, num, typeOfPeriod) ){
                    return new ResponseEntity<>("Success", HttpStatus.OK);
                }
            }else{
                complaintService.updateComplaintStatus(idList,"Failed", "", 0);
                return new ResponseEntity<>("Failed", HttpStatus.OK);
            }


        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
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

    @RequestMapping(value = "/requestEmployerRatingList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Rating>> requestEmployerRatingList(@RequestParam("id") int id) throws IOException {
        try {
            List<Rating> ratingList = new ArrayList<Rating>();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;

            Employer employer = employerService.getEmployerById(id);
            if (Objects.nonNull(employer)) {
                ratingList = ratingService.getRatingList(employer.getId(), "Employer");
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
}
