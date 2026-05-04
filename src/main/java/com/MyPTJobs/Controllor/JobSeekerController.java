package com.MyPTJobs.Controllor;

import com.MyPTJobs.Class.*;
import com.MyPTJobs.Repository.JobSeekerRepository;
import com.MyPTJobs.Services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController // This means that this class is a Controller
@RequestMapping(path = "/jobseeker") // This means URL's start with /demo (after Application path)
public class JobSeekerController {
    private Path rootLocation;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;
    @Autowired
    private JobService jobService;

    @Autowired
    private EmployerController EmployerController;

    @Autowired
    private com.MyPTJobs.Services.FavouriteJobService FavouriteJobService;

    @Autowired
    private com.MyPTJobs.Services.JobApplicationService JobApplicationService;

    @Autowired
    private WorkExperienceService workExperienceService;
    @Autowired
    private SkillService skillService;

    @Autowired
    private LanguageService langService;

    @Autowired
    private JobSeekerService jobSeekerService;

    @Autowired
    private EmployerService employerService;

    @Autowired
    private JobOfferService jobOfferService;

    @Autowired
    private JobInterviewService jobInterviewService;

    @Autowired
    private JobApplicationService jobApplicationService;


    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    FcmService fcmService;

    private final String uploadDir = "Images/JobSeeker/";

    @PostMapping(path = "/all")
    public @ResponseBody Iterable<JobSeeker> getAllUsers() {
        // This returns a JSON or XML with the users
        return jobSeekerRepository.findAll();
    }

    @GetMapping("/time")
    public String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        ZoneOffset offset = ZoneOffset.systemDefault().getRules().getOffset(now);
        return now.atOffset(offset).toString();
    }


    @PostMapping(path = "/test")
    public @ResponseBody String test() {
        // This returns a JSON or XML with the users
        return "q23";
    }

    /*
        success       = 201
        found         = 302
        Error         = 500
     */
    @PostMapping(path = "/signup")
    public ResponseEntity<String> signUp(@ModelAttribute JobSeeker jobSeeker) {
        try {
            Optional<JobSeeker> jobSeekerData = jobSeekerRepository.checkExisting(jobSeeker.getEmail());

            if (jobSeekerData.isEmpty()) {
                jobSeeker.setName("");
                jobSeeker.setIC("");
                jobSeeker.setCreated_at(new Timestamp(System.currentTimeMillis()));
                jobSeeker.setVerification("Unverified");
                jobSeeker.setJobSetting("Off");
                jobSeeker.setVerificationCode("");
                jobSeeker.setRating(0);
                JobSeeker _jobSeeker = jobSeekerRepository
                        .save(jobSeeker);
                return new ResponseEntity<>("success", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Email Exists", HttpStatus.FOUND);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
        success       = 200
        found         = 302
        Error         = 500
     */
    @PostMapping(path = "/googlesignup")
    public ResponseEntity<JobSeeker> googleSignUp(@RequestPart(required = false) @ModelAttribute JobSeeker jobSeeker, @RequestParam("image") Optional<String> image) {
        String _image = image.orElse("");
        try {
            Optional<JobSeeker> jobSeekerData = jobSeekerRepository.checkExisting(jobSeeker.getEmail());

            if (jobSeekerData.isEmpty()) {
                jobSeeker.setCreated_at(new Timestamp(System.currentTimeMillis()));
                jobSeeker.setVerification("Unverified");
                jobSeeker.setJobSetting("Off");
                jobSeeker.setVerificationCode("");
                jobSeeker.setRating(0);
                JobSeeker _jobSeeker = jobSeekerRepository
                        .save(jobSeeker);
                _jobSeeker.setImageFile("");
//                _jobSeeker.setToken(new AESUtil().encryptAES(Integer.toString(_jobSeeker.getId())));
                _jobSeeker.generateToken();
                jobSeekerRepository.saveToken(_jobSeeker.getToken(), jobSeeker.getNotificationToken(), _jobSeeker.getId());
                if (_image != null && !_image.isEmpty()) {
                    String fileName = Integer.toString(_jobSeeker.getId());

                    rootLocation = Paths.get(uploadDir);
                    if (!Files.exists(rootLocation)) {
                        Files.createDirectories(rootLocation);
                    }
                    FileStorageProperties saveDir = new FileStorageProperties();
                    saveDir.setUploadDir(uploadDir);
                    FileStorageService fileService = new FileStorageService(saveDir);
                    try {
                        fileService.saveFileFromURL(_image, fileName);
                        _jobSeeker.setImageFile(fileName + ".png");
                        jobSeekerRepository
                                .save(jobSeeker);
                    } catch (Exception e) {
                        System.out.println(e);
                        throw new IOException("Could not save uploaded file: " + fileName);
                    }
                }

                return new ResponseEntity<>(_jobSeeker, HttpStatus.OK);
            } else {
                if( !jobSeekerService.checkBlockOrNot(jobSeekerData.get()) ){
                    return new ResponseEntity<>(new JobSeeker(jobSeekerData.get().getBlockDate(), jobSeekerData.get().getBlockPermanent()), HttpStatus.FORBIDDEN);
                }
                JobSeeker _jobSeeker = jobSeekerRepository.getBasicInfo(jobSeeker.getEmail());
//                _jobSeeker.setToken(new AESUtil().encryptAES(Integer.toString(_jobSeeker.getId())));
                _jobSeeker.generateToken();
                jobSeekerRepository.saveToken(_jobSeeker.getToken(), jobSeeker.getNotificationToken(), _jobSeeker.getId());
                return new ResponseEntity<>(_jobSeeker, HttpStatus.OK);
//                return new ResponseEntity<>(null, HttpStatus.FOUND);
            }

        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /*
        login success = 200
        error         = 409
        not found     = 204
        Error         = 500
     */

    @PostMapping(path = "/login")
    public ResponseEntity<JobSeeker> login(@ModelAttribute JobSeeker jobSeeker) {
        try {
            Optional<JobSeeker> jobSeekerData = jobSeekerRepository.checkExisting(jobSeeker.getEmail());

            if (jobSeekerData.isPresent()) {
                if( !jobSeekerService.checkBlockOrNot(jobSeekerData.get()) ){
                    return new ResponseEntity<>(new JobSeeker(jobSeekerData.get().getBlockDate(),
                            jobSeekerData.get().getBlockPermanent()), HttpStatus.FORBIDDEN);
                }
                JobSeeker _jobSeeker = jobSeekerRepository.login(jobSeeker.getEmail(), jobSeeker.getPassword());
                if (_jobSeeker != null) {
                    if (_jobSeeker.getId() > 0) {
                        _jobSeeker.generateToken();
                        jobSeekerRepository.saveToken(_jobSeeker.getToken() ,jobSeeker.getNotificationToken(), _jobSeeker.getId());
                        return new ResponseEntity<>(_jobSeeker, HttpStatus.OK);
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
    @RequestMapping(value = "/saveProfile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JobSeeker> uploadImage(@ModelAttribute JobSeeker jobSeeker, @RequestPart(required = false)
        @RequestParam("file") Optional<MultipartFile> file) throws IOException {
        MultipartFile _file = file.orElse(null);
        JobSeeker _jobSeeker = jobSeekerRepository.getBiodata(jobSeeker.getToken());
        if (_jobSeeker == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        String fileName = Integer.toString(_jobSeeker.getId())+"-"+(System.currentTimeMillis() / 1000L);
        try {
            if (_file != null) {
                rootLocation = Paths.get(uploadDir);
                if (!Files.exists(rootLocation)) {
                    Files.createDirectories(rootLocation);
                }
                String specificUploadDir =  uploadDir + _jobSeeker.getId()+"/";
                rootLocation = Paths.get(specificUploadDir);
                if (!Files.exists(rootLocation)) {
                    Files.createDirectories(rootLocation);
                }
                FileStorageProperties saveDir = new FileStorageProperties();
                saveDir.setUploadDir(specificUploadDir);
                FileStorageService fileService = new FileStorageService(saveDir);
                fileName = fileService.storeFile(_file, fileName);
                _jobSeeker.setImageFile(_jobSeeker.getId()+"/"+fileName);
            }
            jobSeekerRepository.updateProfile(_jobSeeker.getImageFile(), jobSeeker.getName(), jobSeeker.getIC(),
                    jobSeeker.getPhoneNumber(), jobSeeker.getGender(), jobSeeker.getEmploymentStatus(),
                    jobSeeker.getHighestEducation(), _jobSeeker.getId());
            return retrieveBiodata(jobSeeker.getToken());
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/files/{folder:.+}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String folder, @PathVariable String filename) throws IOException {
        FileStorageProperties saveDir = new FileStorageProperties();
        saveDir.setUploadDir(uploadDir+folder+"/");
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
        saveDir.setUploadDir(uploadDir+"/");
        System.out.println(saveDir.getUploadDir());
        FileStorageService fileService = new FileStorageService(saveDir);
        Resource file = fileService.loadFileAsResource(filename);
        String type = Files.probeContentType(Path.of(uploadDir + filename));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, type).body(file);
    }

    @PostMapping("/updatejobsetting")
    public ResponseEntity<JobSeeker> updateJobSetting(@RequestParam("token") String token, @RequestParam("jobSetting") String jobSetting) {
        System.out.println(token);
        System.out.println(jobSetting);
        jobSeekerRepository.updateJobSetting(jobSetting, token);
        JobSeeker _jobSeeker = jobSeekerRepository.getLatestBasicInfo(token);
        return new ResponseEntity<>(_jobSeeker, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_JOB_SEEKER')")
    @PostMapping("/getbiodata")
    public ResponseEntity<JobSeeker> retrieveBiodata(@RequestParam("token") String token) {
        JobSeeker _jobSeeker = jobSeekerRepository.getBiodata(token);
        if (_jobSeeker != null && _jobSeeker.getId() > 0) {
            return new ResponseEntity<>(_jobSeeker, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

//    @PreAuthorize("@jobSeekerService.checkByToken(authentication.principal.token) and #id == authentication.principal.id")
    @PostMapping("/getbasicinfo")
    public ResponseEntity<JobSeeker> getBasicInfo(@RequestParam("token") String token) {
        JobSeeker _jobSeeker = jobSeekerRepository.getLatestBasicInfo(token);
        if (_jobSeeker != null && _jobSeeker.getId() > 0) {
            return new ResponseEntity<>(_jobSeeker, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/getCurrentArea")
    public ResponseEntity<JobSeeker> getCurrentArea(@RequestParam("token") String token) {
        JobSeeker _jobSeeker = jobSeekerRepository.getCurrentArea(token);
        if (_jobSeeker != null && _jobSeeker.getId() > 0) {
            return new ResponseEntity<>(_jobSeeker, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/updateCurrentArea")
    public ResponseEntity<String> updateCurrentArea(@RequestParam("token") String token, @RequestParam("state") String currentState, @RequestParam("city") String currentCity) {
        Optional<JobSeeker> _jobSeeker = jobSeekerRepository.checkExistingByToken(token);
        if (_jobSeeker.isPresent()) {
            jobSeekerRepository.updateArea(currentState, currentCity, token);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/checkLogin")
    public ResponseEntity<String> checkLogin(@RequestParam("token") String token) {
        if ( token.isEmpty() ){
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        Optional<JobSeeker> _jobSeeker = jobSeekerRepository.checkExistingByToken(token);
        if (_jobSeeker.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam("token") String token) {
        Optional<JobSeeker> _jobSeeker = jobSeekerRepository.checkExistingByToken(token);
        if (_jobSeeker.isPresent()) {
            jobSeekerRepository.removeToken(_jobSeeker.get().getId());
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

//    @PostMapping("/getwork")
//    public ResponseEntity<List<WorkExperience>> getwork( @RequestParam("token") String token){
//        Optional<JobSeeker> _jobSeeker = jobSeekerRepository.checkExistingByToken(token);
//        if (_jobSeeker.isPresent()){
//            List<WorkExperience> workExperience = jobSeekerRepository.getListOfWorkExperience(token);
//            workExperience.forEach(System.out::println);
//            return new ResponseEntity<>(workExperience, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//    }


    @RequestMapping(value = "/requestJobList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Job>> requestJobList(@RequestPart(required = false) @RequestParam("value") Optional<String> value,
                                                    @RequestPart(required = false) @RequestParam("isFilter") Optional<Boolean> isFilter,
                                                    @RequestPart(required = false) @RequestParam("jobType") Optional<String> jobType,
                                                    @RequestPart(required = false) @RequestParam("jobLocation") Optional<String> jobLocation,
                                                    @RequestPart(required = false) @RequestParam("distance") Optional<Double> distance,
                                                    @RequestPart(required = false) @RequestParam("salaryPerHours") Optional<Double> salaryPerHours,
                                                    @RequestPart(required = false) @RequestParam("startTime") Optional<String> startTime,
                                                    @RequestPart(required = false) @RequestParam("endTime") Optional<String> endTime,
                                                    @RequestPart(required = false) @RequestParam("selectedDate") Optional<String> selectedDate,
                                                    @RequestPart(required = false) @RequestParam("currentLatitude") Optional<String> currentLatitude,
                                                    @RequestPart(required = false) @RequestParam("currentLongitude") Optional<String> currentLongitude,
                                                    @RequestPart(required = false) @RequestParam("limitResult") Optional<Integer> limitResult ,
                                                    @RequestPart(required = false) @RequestParam("orderBY") Optional<String> orderBY,
                                                    @RequestPart(required = false) @RequestParam("rand") Optional<Boolean> rand) throws IOException {

        int maxResults = limitResult.orElse(0);

        Boolean _isFilter = isFilter.orElse(false);
        System.out.println(2);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Job> query = cb.createQuery(Job.class);
        Root<Job> root = query.from(Job.class);
        query.select(root);
        Root<Employer> employerRoot = query.from(Employer.class);
        String _jobType = jobType.orElse("All");
        String _jobLocation = jobLocation.orElse("All");
        Double _distance = distance.orElse(0.0);
        Double _salaryPerHours = salaryPerHours.orElse(0.0);
        String _startTime = startTime.orElse("");
        String _endTime = endTime.orElse("");
        String _selectedDate = selectedDate.orElse("");
        String _currentLatitude = currentLatitude.orElse("");
        String _currentLongitude = currentLongitude.orElse("");
        String _value = value.orElse("");
        String _orderBy = orderBY.orElse("");
        boolean _rand = rand.orElse(false);
        List<Predicate> predicates = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();
        String currentDateFormatted = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));


        // add conditions to the predicates list

        // Define the join condition using the employerID column
        predicates.add(cb.equal(root.get("employerID"), employerRoot.get("id")));
        predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), currentDateFormatted));

        if (!_value.isEmpty()) {
            predicates.add(cb.like(root.get("title"), "%" + _value + "%"));
        }
        predicates.add(cb.isNull(root.get("deleted_at")));
        predicates.add(cb.equal(root.get("jobStatus"), "Active"));
        if (_isFilter) {
            if (!_jobType.equals("All")) {
                predicates.add(cb.equal(root.get("jobType"), _jobType));
            }

            if (!_jobLocation.equals("All")) {
                predicates.add(cb.equal(root.get("area"), _jobLocation));
            }
            System.out.println(_distance);
            if (_distance > 0) {
                if (!_currentLatitude.isEmpty() && !_currentLongitude.isEmpty()) {
                    // calculate the earth radius in kilometers
                    double R = 6371;

                    // get the latitude and longitude of the user's location
                    double userLat = Double.parseDouble(_currentLatitude); // get user's latitude
                    double userLng = Double.parseDouble(_currentLongitude); // get user's longitude

                    // calculate the latitude and longitude range using the Haversine formula
                    double maxLat = userLat + Math.toDegrees(_distance / R);
                    double minLat = userLat - Math.toDegrees(_distance / R);
                    double maxLng = userLng + Math.toDegrees(_distance / R / Math.cos(Math.toRadians(userLat)));
                    double minLng = userLng - Math.toDegrees(_distance / R / Math.cos(Math.toRadians(userLat)));

                    // add the latitude and longitude range conditions to the predicates list
                    predicates.add(cb.between(root.get("latitude"), minLat, maxLat));
                    predicates.add(cb.between(root.get("longitude"), minLng, maxLng));
                }

            }

            if (_salaryPerHours > 0) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("salaryPerHours"), _salaryPerHours));
            }

            if (!_startTime.isEmpty()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), LocalTime.parse(_startTime, DateTimeFormatter.ofPattern("HH:mm"))));
            }

            if (!_endTime.isEmpty()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), LocalTime.parse(_endTime, DateTimeFormatter.ofPattern("HH:mm"))));
            }

            if (!_selectedDate.isEmpty()) {
                predicates.add(cb.equal(root.get("date"), _selectedDate));
            }

        }

        predicates.add(cb.notEqual(employerRoot.get("blockPermanent"), 1));
        // combine the predicates using a logical operator (AND or OR)
        Predicate combinedPredicate = cb.and(predicates.toArray(new Predicate[0]));

        // use constructor expression to select specific attributes
        query.select(cb.construct(Job.class, root.get("id"), root.get("title"), root.get("description"), root.get("salaryPerHours"),
                root.get("latitude"), root.get("longitude"), root.get("date"), root.get("startTime"), root.get("endTime"),
                root.get("location"), root.get("employerID"), root.get("image"), root.get("area"), root.get("created_at"),
                root.get("endDate"), employerRoot));


        query.orderBy(cb.desc(root.get("created_at")));

        List<Job> results = new ArrayList<>();
        query.where(combinedPredicate);

        if ( _rand == true ){
            // add the order by clause to get random 3 jobs
            query.orderBy(cb.desc(root.get("salaryPerHours")));

        }

        if ( maxResults > 0 ){
            results = entityManager.createQuery(query).setMaxResults(maxResults).getResultList();
        }else{
            results = entityManager.createQuery(query).getResultList();
        }



        return new ResponseEntity<>(results, HttpStatus.OK);

    }

    @RequestMapping(value = "/requestFavouriteJobList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Job>> requestFavouriteJobList(@RequestParam("token") String token) throws IOException {
        try {
            List<Job> jobList = new ArrayList<Job>();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;

            Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if (jobSeeker.isPresent()) {
                jobList = jobService.getFavouriteJob(jobSeeker.get().getId(), page, limit);
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

    @RequestMapping(value = "/requestAppliedJobList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JobApplication>> requestAppliedJobList(@RequestParam("token") String token) throws IOException {
        try {
            List<JobApplication> jobList = new ArrayList<JobApplication>();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;

            Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if (jobSeeker.isPresent()) {
                jobList = JobApplicationService.getJobSeekerAppliedJob(jobSeeker.get().getId(), page, limit);
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

    @RequestMapping(value = "/requestJob", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> requestJob(@RequestParam("value") int value, @RequestParam("token") Optional<String> _token) throws IOException {
        String token = _token.orElse("");
        System.out.println(token);
        try {
            Job jobList = new Job();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;
            jobList = jobService.getSelectedJob(value);
            jobList.setIsFavourite("0");
            if (!token.isEmpty()) {
                if (FavouriteJobService.checkFavourite(token, value)) {
                    jobList.setIsFavourite("1");
                }
            }
            jobList.setIsApplied("0");
            if (!token.isEmpty()) {
                if (JobApplicationService.checkApplication(token, value)) {
                    jobList.setIsApplied("1");
                }
            }
            jobList.setProcessOrNot(0);
            if (!token.isEmpty()) {
                if (employerService.getEmployerById(jobList.getEmployerID()).getBlockPermanent() == 1) {
                    jobList.setProcessOrNot(1);
                }
            }
            return new ResponseEntity<>(jobList, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/updateAddWorkingExperience", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateAddWorkingExperience(@ModelAttribute WorkExperience workExperience, @RequestPart(required = false) @RequestParam("mode") String mode, @RequestPart(required = false) @RequestParam("token") String token) throws IOException {
        try {
            Optional<JobSeeker> _jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if (_jobSeeker.isPresent()) {
                workExperience.setJobSeeker(_jobSeeker.get().getId());
                workExperienceService.updateAddWorkingExperience(workExperience, mode);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
//            workExperienceController.updateAddWorkingExperience(workExperience, mode);
            return new ResponseEntity<>("Fail", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/removeWorkExperience", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeWorkExperience(@RequestPart(required = false) @RequestParam("id") int id) throws IOException {
        try {
//            if (_jobSeeker.isPresent()){
            workExperienceService.removeWorkExperience(id);
            return new ResponseEntity<>("Success", HttpStatus.OK);
//            }
//            workExperienceController.updateAddWorkingExperience(workExperience, mode);
//            return new ResponseEntity<>("Fail", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/requestListOfWorkExperience", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WorkExperience>> requestListOfWorkExperience(@RequestPart(required = false) @RequestParam("token") String token) throws IOException {
        try {
            Optional<JobSeeker> _jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if (_jobSeeker.isPresent()) {
                List<WorkExperience> workExperienceList = workExperienceService.getListofWorkingExperience(_jobSeeker.get());
                return new ResponseEntity<>(workExperienceList, HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/getWorkExperience", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkExperience> getWorkExperience(@RequestPart(required = false) @RequestParam("id") int id) throws IOException {
        try {
            WorkExperience workExperience = workExperienceService.getWorkingExperience(id);
            if (workExperience != null) {
                return new ResponseEntity<>(workExperience, HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/updateAddSkill", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateAddSkill(@ModelAttribute Skill workExperience, @RequestPart(required = false) @RequestParam("mode") String mode, @RequestPart(required = false) @RequestParam("token") String token, @RequestParam("skillName") String skillName) throws IOException {
        try {
            Optional<JobSeeker> _jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if (_jobSeeker.isPresent()) {
                workExperience.setJobSeekerID(_jobSeeker.get().getId());
                workExperience.setSkill(skillName);
                skillService.updateAddSkill(workExperience, mode);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
//            workExperienceController.updateAddWorkingExperience(workExperience, mode);
            return new ResponseEntity<>("Fail", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/removeSkill", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeSkill(@RequestPart(required = false) @RequestParam("id") int id) throws IOException {
        try {
//            if (_jobSeeker.isPresent()){
            skillService.removeSkill(id);
            return new ResponseEntity<>("Success", HttpStatus.OK);
//            }
//            workExperienceController.updateAddWorkingExperience(workExperience, mode);
//            return new ResponseEntity<>("Fail", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/requestListOfSkill", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Skill>> requestListOfSkill(@RequestPart(required = false) @RequestParam("token") String token) throws IOException {
        try {
            Optional<JobSeeker> _jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if (_jobSeeker.isPresent()) {
                List<Skill> workExperienceList = skillService.getListofSkill(_jobSeeker.get());
                return new ResponseEntity<>(workExperienceList, HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/getSkill", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Skill> getSkill(@RequestPart(required = false) @RequestParam("id") int id) throws IOException {
        try {
            Skill workExperience = skillService.getSkill(id);
            if (workExperience != null) {
                return new ResponseEntity<>(workExperience, HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/updateAddLanguage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateAddLanguage(@ModelAttribute Language workExperience, @RequestPart(required = false) @RequestParam("mode") String mode, @RequestPart(required = false) @RequestParam("token") String token, @RequestParam("languageName") String languageName) throws IOException {
        try {
            Optional<JobSeeker> _jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if (_jobSeeker.isPresent()) {
                workExperience.setJobSeekerID(_jobSeeker.get().getId());
                workExperience.setLanguage(languageName);
                langService.updateAddLanguage(workExperience, mode);
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
//            workExperienceController.updateAddWorkingExperience(workExperience, mode);
            return new ResponseEntity<>("Fail", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/removeLanguage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeLanguage(@RequestPart(required = false) @RequestParam("id") int id) throws IOException {
        try {
//            if (_jobSeeker.isPresent()){
            langService.removeLanguage(id);
            return new ResponseEntity<>("Success", HttpStatus.OK);
//            }
//            workExperienceController.updateAddWorkingExperience(workExperience, mode);
//            return new ResponseEntity<>("Fail", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/requestListOfLanguage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Language>> requestListOfLanguage(@RequestPart(required = false) @RequestParam("token") String token) throws IOException {
        try {
            Optional<JobSeeker> _jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if (_jobSeeker.isPresent()) {
                List<Language> workExperienceList = langService.getListofLanguage(_jobSeeker.get());
                return new ResponseEntity<>(workExperienceList, HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/getLanguage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Language> getLanguage(@RequestPart(required = false) @RequestParam("id") int id) throws IOException {
        try {
            Language workExperience = langService.getLanguage(id);
            if (workExperience != null) {
                return new ResponseEntity<>(workExperience, HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @RequestMapping(value = "/requestPersonalInformation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobSeeker> requestPersonalInformation(@RequestPart(required = false) @RequestParam("token") String token) throws IOException {
        try {
            Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if (jobSeeker.isPresent()) {
                jobSeeker.get().setLanguageList(langService.getListofLanguage(jobSeeker.get()));
                jobSeeker.get().setSkillList(skillService.getListofSkill(jobSeeker.get()));
                jobSeeker.get().setWorkExperienceList(workExperienceService.getListofWorkingExperience(jobSeeker.get()));
                return new ResponseEntity<>(jobSeeker.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestParam("token") String token,
                                                 @RequestParam("oldPassword") String oldPassword,
                                                 @RequestParam("newPassword") String newPassword) {
        Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkOldPassword(token);
        if (jobSeeker.isPresent()) {
            if (jobSeeker.get().getPassword().equals(oldPassword)) {
                System.out.println(newPassword);
                jobSeekerRepository.updateNewPassword(newPassword, jobSeeker.get().getId());
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Success", HttpStatus.CONFLICT);
            }

        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<String> changePassword(@RequestParam("email") String email) throws MessagingException, UnsupportedEncodingException {
        Optional<JobSeeker> jobSeeker = jobSeekerRepository.findByEmail(email);
        if (jobSeeker.isPresent()) {
            jobSeeker.get().setVerificationCode(new function().getRandomNumberString());
            jobSeekerRepository.save(jobSeeker.get());
            jobSeekerService.sendVerificationEmail(jobSeeker.get());
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Success", HttpStatus.NOT_FOUND);
        }
//        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/checkVerificationCode")
    public ResponseEntity<String> checkVerificationCode(@RequestParam("email") String email, @RequestParam("code") String code) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<JobSeeker> jobSeeker = jobSeekerRepository.findByEmail(email);
        String token = "";
        if (jobSeeker.isPresent()) {
            if (jobSeeker.get().getVerificationCode().equals(code)) {
                jobSeeker.get().generateToken();
                jobSeekerRepository.save(jobSeeker.get());
                token = jobSeeker.get().getToken();
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
        Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(token);
        if (jobSeeker.isPresent()) {
            jobSeekerRepository.resetPassword(newPassword, jobSeeker.get().getId());
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Success", HttpStatus.CONFLICT);
        }


//        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


    @PostMapping("/addFavouriteJob")
    public ResponseEntity<String> addFavouriteJob(@RequestParam("token") String token, @RequestParam("jobID") int jobID) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(token);
        if (jobSeeker.isPresent()) {
            FavouriteJobService.addFavourite(jobSeeker.get().getId(), jobID);
            return new ResponseEntity<>("200", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/deleteFavouriteJob")
    public ResponseEntity<String> deleteFavouriteJob(@RequestParam("token") String token, @RequestParam("jobID") int jobID) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(token);
        if (jobSeeker.isPresent()) {
            FavouriteJobService.deleteFavourite(jobSeeker.get().getId(), jobID);
            return new ResponseEntity<>("200", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/addApplication")
    public ResponseEntity<String> addApplication(@RequestParam("token") String token, @RequestParam("jobID") int jobID,
     @RequestParam("selectedDate") String selectedDate) throws InvalidAlgorithmParameterException, IllegalBlockSizeException,
            NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, FirebaseMessagingException {
        Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(token);
        if (jobSeeker.isPresent()) {
            if ( !jobSeekerService.checkCompleteInformationOrNot(jobSeeker.get())){
                return new ResponseEntity<>("incomplete Data", HttpStatus.OK);
            }
            if ( !jobSeekerService.checkVerification(jobSeeker.get())){
                return new ResponseEntity<>("Unverified", HttpStatus.OK);
            }
            JobApplicationService.addApplication(jobSeeker.get().getId(), jobID, selectedDate);
            MessageDTO note = new MessageDTO();
            note.setContent("A new job application has been received");
            fcmService.sendNotificationToSpecificDevice("job", jobID , note);
            return new ResponseEntity<>("200", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/deleteApplication")
    public ResponseEntity<String> deleteApplication(@RequestParam("token") String token, @RequestParam("jobID") int jobID) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(token);
        if (jobSeeker.isPresent()) {
            JobApplicationService.deleteApplication(jobSeeker.get().getId(), jobID);
            return new ResponseEntity<>("200", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/viewEmployer")
    public ResponseEntity<Employer> viewEmployer(@RequestParam("id") int id) throws InvalidAlgorithmParameterException,
            IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Employer emp = employerService.requestEmployerInformation(id);
        return new ResponseEntity<>(emp, HttpStatus.OK);
    }

    @PostMapping("/requestJobSetting")
    public ResponseEntity<JobSeeker> requestJobSetting(@RequestParam("token") String token) throws
            InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<JobSeeker> jobSeeker = jobSeekerRepository.getJobSetting(token);
        if (jobSeeker.isPresent()) {
            return new ResponseEntity<>(jobSeeker.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(new JobSeeker(), HttpStatus.NOT_FOUND);
    }


    @PostMapping("/updateJobSetting")
    public ResponseEntity<String> updateJobSetting(@RequestParam("token") String token, @ModelAttribute JobSeeker jobSeeker) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Optional<JobSeeker> _jobSeeker = jobSeekerRepository.checkExistingByToken(token);
        if (_jobSeeker.isPresent()) {
            _jobSeeker.get().setPreferDay(jobSeeker.getPreferDay());
            _jobSeeker.get().setPreferEndTime(jobSeeker.getPreferEndTime());
            _jobSeeker.get().setPreferJobType(jobSeeker.getPreferJobType());
            _jobSeeker.get().setPreferLocation(jobSeeker.getPreferLocation());
            _jobSeeker.get().setPreferStartTime(jobSeeker.getPreferStartTime());
            _jobSeeker.get().setPreferDistance(jobSeeker.getPreferDistance());
            _jobSeeker.get().setPreferSalary(jobSeeker.getPreferSalary());
            jobSeekerRepository.save(_jobSeeker.get());
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);

    }

    @RequestMapping(value = "/requestOfferJobList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JobOffer>> requestOfferJobList(@RequestParam("token") String token) throws IOException {
        try {
            List<JobOffer> jobList = new ArrayList<JobOffer>();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;

            Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if (jobSeeker.isPresent()) {
                jobList = jobOfferService.getJobSeekerOffer(jobSeeker.get().getId(), page, limit);
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
    @RequestMapping(value = "/processOffer", method = RequestMethod.POST)
    public ResponseEntity<String> processOffer(@RequestParam("offerID") int offerID, @RequestParam("status") String status) throws IOException, FirebaseMessagingException {
        Optional<JobOffer> jobOffer = jobOfferService.getExistingOffer(offerID);
        Job selectedJob = jobService.getSelectedJob(jobOffer.get().getJobID());
        if (jobOfferService.updateOfferStatus(offerID, status)) {
            if ( status.equals("Accepted") ){

                List<String> datesBetween = selectedJob.getDatesBetween();
                ObjectMapper objectMapper = new ObjectMapper();
                String selectedDate =  objectMapper.writeValueAsString(datesBetween);

                JobApplication newJobApplication = new JobApplication(jobOffer.get().getJobID(), jobOffer.get().getJobSeekerID(), "Offer Accepted", jobOffer.get().getOfferDate(), selectedDate);
                jobApplicationService.saveNew(newJobApplication);

            }
            if ( jobOffer.isPresent() ){
                MessageDTO note = new MessageDTO();
                if ( status.equals("Accepted") ){
                    note.setContent("Congratulations! The job seeker has accepted your job offer for the position of "+selectedJob.getTitle()+".");
                }else{
                    note.setContent("We regret to inform you that the job seeker has declined your job offer for the position of "+selectedJob.getTitle()+".");
                }
                note.setSubject(selectedJob.getTitle());
                fcmService.sendNotificationToSpecificDevice("employer", selectedJob.getEmployerID() , note);
            }
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/requestInterviewJobList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JobInterview>> requestInterviewJobList(@RequestParam("token") String token) throws IOException {
        try {
            List<JobInterview> jobList = new ArrayList<JobInterview>();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;

            Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if (jobSeeker.isPresent()) {
                jobList = jobInterviewService.getJobSeekerInterview(jobSeeker.get().getId(), page, limit);
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

    @RequestMapping(value = "/processInterview", method = RequestMethod.POST)
    public ResponseEntity<String> processInterview(@RequestParam("interviewID") int interviewID,
                                                   @RequestParam("status") String status) throws IOException, FirebaseMessagingException {

        if (jobInterviewService.updateInterviewStatus(interviewID, status)) {
            Optional<JobApplication> jobApplication = jobInterviewService.checkExistingJobApplication(interviewID);
            if ( jobApplication.isPresent() ){
                Job job = jobService.getSelectedJob(jobApplication.get().getJobID());
                MessageDTO note = new MessageDTO();
                if ( status.equals("Accepted") ){
                    note.setContent("Great news! The job seeker has accepted your interview invitation. They are looking forward to the scheduled interview. Please make the necessary arrangements and prepare for the meeting. Good luck!");
                }else{
                    note.setContent("We regret to inform you that the job seeker has declined your interview invitation. They will not be available for the scheduled interview. ");
                }
                note.setSubject(job.getTitle());
                fcmService.sendNotificationToSpecificDevice("employer", job.getEmployerID() , note);
            }
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
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
                Job job = jobService.getSelectedJob(jobApplication.get().getJobID());
                // Calculate the average
                averageRate = sum / count;
                System.out.println(averageRate);
                Rating newRate = new Rating(applicationID, rate, job.getEmployerID(), averageRate);
                ratingService.save(newRate);

                System.out.println(ratingService.calcJobSeekerAverageRating(jobApplication.get().getJobSeekerID()));
                jobApplication.get().setStatus("Completed");
                jobApplicationService.updateApplicationStatus(applicationID, "Done");

                if ( jobApplication.isPresent() ){
                    Job job1 = jobService.getSelectedJob(jobApplication.get().getJobID());
                    MessageDTO note = new MessageDTO();

                    note.setContent("One of your job seekers has completed the rating. Thank you for providing feedback on their performance.");

                    note.setSubject(job.getTitle());
                    fcmService.sendNotificationToSpecificDevice("employer", job.getEmployerID(), note);
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

    @RequestMapping(value = "/requestRatingList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Rating>> requestRatingList(@RequestParam("token") String token) throws IOException {
        try {
            List<Rating> ratingList = new ArrayList<Rating>();
            int page = 1;
            int limit = 1;
            int pageStart = page * limit;

            Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if (jobSeeker.isPresent()) {
                ratingList = ratingService.getRatingList(jobSeeker.get().getId(), "Job Seeker");
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

    @RequestMapping(value = "/submitVerificationDocument", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> submitVerificationDocument(@ModelAttribute JobSeeker js, @RequestPart(required = false) @RequestParam("frontIC") Optional<MultipartFile> frontIC,@RequestPart(required = false) @RequestParam("selfie") Optional<MultipartFile> selfie, @RequestParam("token") String token) throws IOException {
        MultipartFile _frontIC = frontIC.orElse(null);
        MultipartFile _selfie = selfie.orElse(null);
        Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(js.getToken());
        if (!jobSeeker.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        String fileNameFrontIC = Integer.toString(jobSeeker.get().getId()) + "-FrontIC"+"-"+(System.currentTimeMillis() / 1000L);
        String fileNameSelfie = Integer.toString(jobSeeker.get().getId()) + "-Selfie"+"-"+(System.currentTimeMillis() / 1000L);
        try {
            if (_frontIC != null && _selfie!=null) {
                rootLocation = Paths.get(uploadDir);
                if (!Files.exists(rootLocation)) {
                    Files.createDirectories(rootLocation);
                }
                String specificUploadDir =  uploadDir + jobSeeker.get().getId()+"/";
                rootLocation = Paths.get(specificUploadDir);
                if (!Files.exists(rootLocation)) {
                    Files.createDirectories(rootLocation);
                }
                FileStorageProperties saveDir = new FileStorageProperties();
                saveDir.setUploadDir(specificUploadDir);
                FileStorageService fileService = new FileStorageService(saveDir);
                fileNameFrontIC = fileService.storeFile(_frontIC, fileNameFrontIC);
                fileNameSelfie = fileService.storeFile(_selfie, fileNameSelfie);
                jobSeeker.get().setFrontICImage(jobSeeker.get().getId()+"/"+fileNameFrontIC);
                jobSeeker.get().setSelfieImage(jobSeeker.get().getId()+"/"+fileNameSelfie);
                jobSeeker.get().setIC(js.getIC());
                jobSeeker.get().setName(js.getName());
                jobSeeker.get().setVerification("Pending");
                jobSeeker.get().setVerificationDate(new Timestamp(System.currentTimeMillis()) );
                jobSeekerRepository.save(jobSeeker.get());
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }

//            employerRepository.updateInformation(emp.getCompanyName(), emp.getCompanyEmail(), emp.getPhoneNumber(), emp.getLocation(), emp.getSsm(), employer.get().getImageFile(), employer.get().getId());
//            Optional<Employer> _employer = employerRepository.getCompanyInformation(token);
//            if (_employer.isPresent()) {
//                return new ResponseEntity<>(_employer.get(), HttpStatus.OK);
//            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
    }


    @RequestMapping(value = "/makeJobComplaint", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> makeJobComplaint(@RequestParam("id") int id, @RequestParam("reason") String reason, @RequestParam("other") String other, @RequestParam("token") String token) throws IOException {
        try {
            Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if (jobSeeker.isPresent()) {
                Job job = jobService.getSelectedJob(id);
                if (Objects.nonNull(job)) {
                    if (complaintService.recordComplaint(id, "job", reason, other,jobSeeker.get().getId())) {
                        return new ResponseEntity<>("Success", HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Failed", HttpStatus.OK);
                    }
//                return new ResponseEntity<>("Success", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Failed", HttpStatus.NOT_FOUND);
                }
//
            }
        }catch(Exception e){
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
        return new ResponseEntity<>("Failed", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/makeEmployerComplaint", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> makeEmployerComplaint(@RequestParam("id") int id, @RequestParam("reason") String reason, @RequestParam("other") String other, @RequestParam("token") String token) throws IOException {
        try {
            Optional<JobSeeker> jobSeeker = jobSeekerRepository.checkExistingByToken(token);
            if ( jobSeeker.isPresent() ){
                Employer employer = employerService.getEmployerById(id);
                if (Objects.nonNull(employer)) {
                    if( complaintService.recordComplaint(id, "employer", reason, other,jobSeeker.get().getId())){
                        return new ResponseEntity<>("Success", HttpStatus.OK);
                    }else{
                        return new ResponseEntity<>("Failed", HttpStatus.OK);
                    }
//                return new ResponseEntity<>("Success", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Failed", HttpStatus.NOT_FOUND);
                }
            }

//
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//            throw new IOException("Could not save uploaded file: " + fileName);
        }
        return new ResponseEntity<>("Failed", HttpStatus.NOT_FOUND);
    }

//    @PostMapping("/single-notification")
//    public String  sendToSpecificDevice(
//            @RequestBody MessageDTO note,
//            @RequestParam String token) throws FirebaseMessagingException {
//
//        return fcmService.sendNotificationToSpecificDevice(note,token);
//    }

}