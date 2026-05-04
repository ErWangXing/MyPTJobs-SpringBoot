package com.MyPTJobs.Repository;

import com.MyPTJobs.Class.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface JobSeekerRepository extends JpaRepository<JobSeeker, Integer> {
    @Query("SELECT t FROM JobSeeker t  WHERE t.email = ?1")
    Optional<JobSeeker> findByEmail(String email);

    @Query("SELECT t FROM JobSeeker t")
    List<JobSeeker> findAll();
    @Query("SELECT Count(t) FROM JobSeeker t")
    int getNumberOfJobSeeker();

    @Query("SELECT Count(t) FROM JobSeeker t where t.verification = 'pending'")
    int getNumberPendingVerification();

    @Query("SELECT t FROM JobSeeker t  WHERE t.email = ?1")
    Optional<JobSeeker> checkExisting(String email);

    @Query("SELECT t FROM JobSeeker t  WHERE t.id = ?1")
    Optional<JobSeeker> findByID(int jobSeekerID);

    @Query("SELECT t FROM JobSeeker t  WHERE t.token = ?1")
    Optional<JobSeeker> checkExistingByToken(String token);

    @Query("SELECT new JobSeeker(t.preferJobType, t.preferSalary, t.preferLocation, t.preferDistance, t.preferDay, t.preferStartTime, t.preferEndTime) FROM JobSeeker t  WHERE t.token = ?1")
    Optional<JobSeeker> getJobSetting(String token);

    @Query("SELECT new JobSeeker(t.id, t.name, t.email, t.verification, t.jobSetting, COALESCE(t.imageFile,''), t.rating) FROM JobSeeker t  WHERE t.email = ?1 and  t.password = ?2")
    JobSeeker login(String email, String password);

    @Query("SELECT new JobSeeker(t.id, t.name, t.email, t.verification, t.jobSetting, COALESCE(t.imageFile,''), t.token, t.rating) FROM JobSeeker t  WHERE t.email = ?1")
    JobSeeker getBasicInfo(String email);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update JobSeeker t set t.imageFile = ?1, t.name = ?2, t.IC = ?3, t.phoneNumber =?4, t.gender= ?5, t.employmentStatus = ?6, highestEducation =?7 where t.id = ?8")
    void updateProfile(String imageFile, String name, String ic, String phoneNumber, String gender, String employmentStatus, String highestEducation, int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update JobSeeker t set t.token = ?1, t.notificationToken = ?2 where t.id = ?3")
    void saveToken(String token, String notificationToken, int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update JobSeeker t set t.jobSetting = ?1 where t.token = ?2")
    void updateJobSetting(String jobSetting, String token);

    @Query("SELECT new JobSeeker(t.id, t.name,  t.email, t.verification, t.jobSetting, COALESCE(t.imageFile,''), t.token, t.rating) FROM JobSeeker t  WHERE t.token = ?1")
    JobSeeker getLatestBasicInfo(String token);

    @Query("SELECT new JobSeeker(t.id, t.name, COALESCE(t.IC,''), COALESCE(t.phoneNumber, ''), COALESCE(t.gender, ''), COALESCE(t.employmentStatus, ''), COALESCE(t.highestEducation, ''), COALESCE(t.imageFile,''), t.verification) FROM JobSeeker t  WHERE t.token = ?1")
    JobSeeker getBiodata(String token);

    @Query("SELECT new JobSeeker(t.id, COALESCE(t.currentState,''), COALESCE(t.currentCity, '') ) FROM JobSeeker t  WHERE t.token = ?1")
    JobSeeker getCurrentArea(String token);
    @Query("SELECT  COALESCE(t.notificationToken,'') FROM JobSeeker t  WHERE t.id = ?1")
    String checkNotificationToken (int id);


    @Query("SELECT CASE " +
            "       WHEN js.currentCity IS NULL OR js.currentState IS NULL OR js.email IS NULL OR js.employmentStatus IS NULL OR js.gender IS NULL OR js.highestEducation IS NULL OR js.imageFile IS NULL OR js.name IS NULL OR js.phoneNumber IS NULL OR js.currentCity = '' OR js.currentState = '' OR js.email = '' OR js.phoneNumber = ''" +
            "       THEN 'incomplete' " +
            "       ELSE 'complete' " +
            "       END " +
            "FROM JobSeeker js " +
            "WHERE js.id = ?1")
    String checkJobseekerCompleteness(int id);

    @Query("SELECT js.verification FROM JobSeeker js " +
            "WHERE js.id = ?1")
    String checkJobseekerVerification(int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update JobSeeker t set t.currentState = ?1, t.currentCity = ?2 where t.token = ?3")
    void updateArea(String currentState, String currentCity, String token);

//    @Query(value = "SELECT new WorkExperience(a.id, a.workExperienceTitle , a.workExperienceCompany, a.dateStartWork, a.dateEndWork) FROM WorkExperience a LEFT Join JobSeeker b on (a.jobSeeker = b.id) WHERE b.token = ?1")
//    List<WorkExperience> getListOfWorkExperience(String token);

    @Query("SELECT new JobSeeker(t.id, t.password) FROM JobSeeker t  WHERE t.token = ?1")
    Optional<JobSeeker> checkOldPassword(String token);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update JobSeeker t set t.password = ?1 where t.id = ?2")
    void updateNewPassword(String newPassword, int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update JobSeeker t set t.password = ?1, t.verificationCode = '', t.token = '' where t.id = ?2")
    void resetPassword(String newPassword, int id);


    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update JobSeeker t set t.preferDay = ?1, t.preferEndTime = ?2,t.preferJobType = ?3,t.preferLocation = ?4, t.preferStartTime = ?5,t.preferDistance = ?6,t.preferSalary = ?7 where t.id = ?8")
    void updateJobSetting(String preferDay, LocalTime preferEndTime, String preferJobType, String preferLocation, LocalTime preferStartTime, double preferDistance, double preferSalary, int id);

    @Query("SELECT js FROM JobSeeker js WHERE (js.preferJobType = 'All' OR  js.preferJobType = ?1 ) AND  ?2 >= js.preferSalary AND JSON_EXTRACT(js.preferDay, CONCAT('$.\"', DATE_FORMAT(?3, '%a'), '\"')) = true AND js.preferStartTime >= ?4 AND js.preferEndTime <= ?5 AND ?6 = js.preferLocation")
    List<JobSeeker> getRecommendationListWithFilter(String jobType, double salaryPerHours, String jobDate, LocalTime startTime, LocalTime endTime, String area);

    //    @Query(value = "SELECT js.*, CASE" +
//            "         WHEN jo.offerID IS NULL THEN 0" +
//            "         ELSE 1" +
//            "       END AS isOffered FROM job j join jobseeker js LEFT JOIN jobOffer jo on (j.jobID = jo.jobID AND js.id = jo.jobSeekerID) WHERE (js.preferJobType = 'All' OR j.jobType = js.preferJobType) AND  j.salaryPerHours >= js.preferSalary AND JSON_EXTRACT(js.preferDay, CONCAT('$.\"', DATE_FORMAT(j.jobDate, '%a'), '\"')) = true AND js.preferStartTime >= j.startTime AND js.preferEndTime <= j.endTime AND j.area = js.preferLocation AND j.jobID = ?1", nativeQuery = true)
//    List<JobSeeker> getRecommendationList(int jobID);

    @Query("SELECT new JobSeeker (js.name, js.IC, js.frontICImage, js.selfieImage, js.verificationDate, js.id) FROM JobSeeker js  WHERE js.id = ?1")
    Optional<JobSeeker> getVerificationInformation(int id);
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update JobSeeker t set t.verification = ?1 where t.id = ?2")
    void updateVerification(String status, int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update jobseeker set blockDate = ?2, blockPermanent = ?3  where id = ?1", nativeQuery = true)
    void deactive(int id, String blockDate, int permanent);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update JobSeeker t set t.token = '', t.notificationToken = '' where t.id = ?1")
    void removeToken(int id);


}