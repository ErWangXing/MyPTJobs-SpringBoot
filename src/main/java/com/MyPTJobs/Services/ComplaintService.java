package com.MyPTJobs.Services;

import com.MyPTJobs.Class.Complaint;
import com.MyPTJobs.Class.Employer;
import com.MyPTJobs.Class.JobSeeker;
import com.MyPTJobs.Repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Component
public class ComplaintService {
    private Path rootLocation;
    @Autowired
    private ComplaintRepository respository;
    @Autowired
    @Lazy
    private JobSeekerService jobSeekerService;
    @Autowired
    @Lazy
    private EmployerService employerService;

    @Autowired
    @Lazy
    private JobService jobService;

    public boolean save(Complaint newComplaint) {
        respository.save(newComplaint);
        return true;
    }

    public boolean recordComplaint(int id, String type, String reason, String otherReason, int reporterID) {
        Complaint complaint = new Complaint();
        if (reason.equals("Others")) {
            complaint.setOthers(otherReason);
        }
        complaint.setType(type);
        complaint.setReason(reason);
        switch (type) {
            case "job":
                complaint.setJobID(id);
                complaint.setJobSeekerID(reporterID);
                break;
            case "employer":
                complaint.setEmployerID(id);
                complaint.setJobSeekerID(reporterID);
                break;
            case "jobSeeker":
                complaint.setJobSeekerID(id);
                complaint.setEmployerID(reporterID);
                break;
            default:
                return false;
        }
        if (save(complaint)) {
            return true;
        }
        return false;
    }


    public int getNumberComplaint() {
        return respository.getUnprocessComplaints().size();
    }

    public List<Complaint> getUnprocessComplaints() {
        List<Complaint> complaintList = new ArrayList<>();
        complaintList = respository.getUnprocessComplaints();

        return complaintList;
    }

    public Complaint getComplaintDetail(int id) {
        Optional<Complaint> complaint = respository.findById(id);

        if (complaint.isPresent()) {
            return complaint.get();
        }

        return new Complaint();
    }

    public boolean getManyCompaints(List<Integer> ids) {


        List<Complaint> complaint = respository.getManyCompaints(ids);

        if (complaint.size() > 0) {
            return true;
        }

        return false;
    }
    public List<Complaint> getComplaintsByIDandType(int id, String type) {
        List<Complaint> complaintList = respository.getComplaintsByIDandType(id, type);



        return complaintList;
    }


//    public boolean processComplaint(int id) throws MessagingException, IOException {
//        Optional<Complaint> complaint = respository.findById(id);
//
//        if (complaint.isPresent()) {
//            String title = "Warning for Complaint about ";
//
//            String content = "<h2>" + complaint.get().getReason() + "</h2>" +
//                    "<br>" +
//                    (complaint.get().getReason().equals("Others") ? "<h3>" + complaint.get().getOthers() + "</h3>" : "");
//            List<Complaint> complaintList = new ArrayList<>();
//            if (complaint.get().getEmployerID() > 0) {
//                title += "Employer Information";
//                content += "<br>Please make change on your information";
//                complaintList = respository.getAllComplaintofEmployer(complaint.get().getEmployerID());
//                Employer employer = employerService.getEmployerById(complaint.get().getEmployerID());
//                switch (complaintList.size()) {
//                    case 0:
//                        // send warning
//                        employerService.sendWarning(employer, title, content);
//                        break;
//
//                    case 1:
//                    default:
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.add(Calendar.DAY_OF_YEAR, 1);
//                        Date date = calendar.getTime();
//
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        String formattedDateTime = sdf.format(date);
//
//                        System.out.println(formattedDateTime);
//
//                        // block a day
//                        title = "Inform that your account has been blocked";
//                        content += "<br>Your account will be unlocked at "+formattedDateTime;
//                        employerService.sendWarning(employer, title, content);
//                        employerService.block(employer);
//                        break;
//
//
//                }
//            }
//            if (complaint.get().getJobID() > 0) {
//                title += "Job Information";
//                content += "<br>Please make change on your information";
//                complaintList = respository.getAllComplaintofJob(complaint.get().getJobID());
//                Employer employer = employerService.getEmployerByJob(complaint.get().getJobID());
//
//                switch (complaintList.size()) {
//                    case 0:
//                        // send warning
//                        employerService.sendWarning(employer, title, content);
//                        break;
//
//                    case 1:
//                    default:
//                        Calendar calendar = Calendar.getInstance();
////                        calendar.add(Calendar.DAY_OF_YEAR, 1);
//                        Date date = calendar.getTime();
//
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        String formattedDateTime = sdf.format(date);
//
//                        System.out.println(formattedDateTime);
//                        // block a day
////                        employerService.block(employer);
//                        title = "Inform that your posted job has been deleted";
//                        content += "<br>Your posted job has been deleted at "+formattedDateTime;
//                        employerService.sendWarning(employer, title, content);
//                        jobService.deleteJob(complaint.get().getJobID());
//                        break;
//
//
//                }
//            }
//
//            if (complaint.get().getJobSeekerID() > 0) {
//                title += "Job Seeker Information";
//                content += "<br>Please make change on your information";
//                complaintList = respository.getAllComplaintofJobSeeker(complaint.get().getJobSeekerID());
//                JobSeeker jobSeeker = jobSeekerService.getJobSeekerByID(complaint.get().getJobSeekerID());
//                switch (complaintList.size()) {
//                    case 0:
//                        // send warning
//                        jobSeekerService.sendWarning(jobSeeker, title, content);
//                        break;
//
//                    case 1:
//                    default:
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.add(Calendar.DAY_OF_YEAR, 1);
//                        Date date = calendar.getTime();
//
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        String formattedDateTime = sdf.format(date);
//
//                        System.out.println(formattedDateTime);
//                        // block a day
//                        title = "Inform that your account has been blocked";
//                        content += "<br>Your account will be unlocked at "+formattedDateTime;
//                        jobSeekerService.sendWarning(jobSeeker, title, content);
//                        jobSeekerService.block(jobSeeker);
//                        break;
//
//
//                }
//
//
//            }
//            updateComplaintStatus(complaint.get(), "Success");
//            return true;
//        }
//
//        return false;
//    }
    public Map<String, Integer> countComplaints(List<Complaint> complaints) {
        Map<String, Integer> countMap = new HashMap<>();
        for (Complaint complaint : complaints) {
            if (countMap.containsKey(complaint.getReason())) {
                countMap.put(complaint.getReason(), countMap.get(complaint.getReason()) + 1);
            } else {
                countMap.put(complaint.getReason(), 1);
            }

        }
        return countMap;
    }

    public String generateComplaintList(Map<String, Integer> countMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");

        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            sb.append("<li>");
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
            sb.append("</li>");
        }

        sb.append("</ul>");
        return sb.toString();
    }

    public boolean processComplaint(List<Integer> ids, String status, String action, String type, int num, String typeOfPeriod) throws MessagingException, IOException {
        List<Complaint> complaintList = respository.getManyCompaints(ids);

        if (complaintList.size() > 0) {
            String title = "Warning: Complaint Received";

            String content = "";
//            "<h2>" + complaint.get().getReason() + "</h2>" +
//                    "<br>" +
//                    (complaint.get().getReason().equals("Others") ? "<h3>" + complaint.get().getOthers() + "</h3>" : "");
//            List<Complaint> complaintList = new ArrayList<>();
            int complaintPerson;
            Employer employer = new Employer();
            JobSeeker jobSeeker = new JobSeeker();
            String adminAction = "";
            int grouping = 0;

            switch (action){
                case "Send Warning":
                    switch (type){
                        case "JobSeeker":
                            complaintPerson = complaintList.stream().findFirst().get().getJobSeekerID();
                            jobSeeker = jobSeekerService.getJobSeekerByID(complaintPerson);
                            title += " - Job Seeker";
                            content = generateComplaintList(countComplaints(complaintList));
//                            content += "<br>Please make change on your information";
                            jobSeekerService.sendWarning(jobSeeker, title, content, "Job Seeker");
                            adminAction  = "Send Warning";
                            grouping = respository.getAllComplaintofJobSeeker(complaintPerson).size();

                            break;
                        case "Job":
                            complaintPerson = jobService.getSelectedJob(complaintList.stream().findFirst().get().getJobID()).getEmployerID();
                            employer = employerService.getEmployerById(complaintPerson);
                            title += " - Job";
//                            content += "<br>Please make change on your information";
                            content = generateComplaintList(countComplaints(complaintList));
                            employerService.sendWarning(employer, title, content, type);
                            adminAction  = "Send Warning";
                            grouping = respository.getAllComplaintofJob(complaintList.stream().findFirst().get().getJobID()).size();

                            break;
                        case "Employer":
                            complaintPerson = complaintList.stream().findFirst().get().getEmployerID();
                            employer = employerService.getEmployerById(complaintPerson);
                            title += " - Employer";
//                            content += "<br>Please make change on your information";
                            content = generateComplaintList(countComplaints(complaintList));
                            employerService.sendWarning(employer, title, content, type);
                            adminAction  = "Send Warning";
                            grouping = respository.getAllComplaintofEmployer(complaintPerson).size();
                            break;
                    }

                    break;

                case "Deactivate":
                case "Delete Job":
                    Calendar calendar = Calendar.getInstance();
                    if (typeOfPeriod.equalsIgnoreCase("Day")) {
                        calendar.add(Calendar.DAY_OF_YEAR, num);
                    } else if (typeOfPeriod.equalsIgnoreCase("Week")) {
                        calendar.add(Calendar.WEEK_OF_YEAR, num);
                    } else if (typeOfPeriod.equalsIgnoreCase("Month")) {
                        calendar.add(Calendar.MONTH, num);
                    }
                    Date date = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = sdf.format(date);

                    System.out.println(formattedDateTime);

                    switch (type){
                        case "JobSeeker":
                            complaintPerson = complaintList.stream().findFirst().get().getJobSeekerID();
                            jobSeeker = jobSeekerService.getJobSeekerByID(complaintPerson);
                            // block a day
                            title = "Inform that your account has been blocked";
                            content += "<br>Your account will be unlocked at "+formattedDateTime;
                            jobSeekerService.sendBlockedEmail(jobSeeker, title, formattedDateTime, "Job Seeker");
                            jobSeekerService.deactive(jobSeeker, formattedDateTime, typeOfPeriod);
                            adminAction  = "Deactive "+num+" "+typeOfPeriod;
                            grouping = respository.getAllComplaintofJobSeeker(complaintPerson).size();

                            break;
                        case "Job":
                            complaintPerson = jobService.getSelectedJob(complaintList.stream().findFirst().get().getJobID()).getEmployerID();
                            employer = employerService.getEmployerById(complaintPerson);
                            title = "Inform that your job has been deleted";
                            content += "<br>Your posted job has been deleted at "+sdf.format(Calendar.getInstance().getTime());
                            employerService.sendDeletedJob(employer, title);
                            jobService.deleteJob(complaintList.stream().findFirst().get().getJobID());
                            adminAction  = "Deactive "+num+" "+typeOfPeriod;
                            grouping = respository.getAllComplaintofJob(complaintList.stream().findFirst().get().getJobID()).size();

                            break;
                        case "Employer":
                            complaintPerson = complaintList.stream().findFirst().get().getEmployerID();
                            employer = employerService.getEmployerById(complaintPerson);
                            title = "Inform that your account has been blocked";
                            content += "<br>Your account will be unlocked at "+formattedDateTime;
                            employerService.sendBlockedEmail(employer, title, formattedDateTime, type);
                            employerService.deactive(employer, formattedDateTime, typeOfPeriod);
                            adminAction  = "Deactive "+num+" "+typeOfPeriod;
                            grouping = respository.getAllComplaintofEmployer(complaintPerson).size();

                            break;
                    }
                    break;
            }


            updateComplaintStatus(ids, "Success", adminAction, ++grouping);
            return true;
        }

        return false;
    }

    public void updateComplaintStatus(List<Integer> ids, String status, String action, int grouping) {
//        complaint.setStatus(status);
//        respository.save(complaint);
        respository.cancelComplaint(ids, status, action, grouping);
    }

    public List<Complaint> getPreviousAction(Complaint complaint, String type) {
        List<Complaint> complaintList = new ArrayList<>();
        System.out.println(type);
        type = type.toLowerCase();
        switch (type) {
            case "job":
                complaintList = respository.getAllComplaintofJob(complaint.getJobID());
                break;
            case "employer":
                complaintList = respository.getAllComplaintofEmployer(complaint.getEmployerID());
                break;
            case "jobseeker":
                complaintList = respository.getAllComplaintofJobSeeker(complaint.getJobSeekerID());
                break;
            default:

        }

        return complaintList;
    }

//    public String showAction(int id, String type, int number) {
//        String action = "";
//
//        showAction(number);
//
//        return action;
//    }
}