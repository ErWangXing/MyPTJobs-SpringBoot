package com.MyPTJobs.Repository;

import com.MyPTJobs.Class.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Integer> {
    @Query("SELECT t FROM Employer t  WHERE t.email = ?1")
    Optional<Employer> findByEmail(String email);
    @Query("SELECT t FROM Employer t")
    List<Employer> findAll();

    @Query("SELECT Count(t) FROM Employer t")
    int getNumberOfEmployer();

    @Query("SELECT Count(t) FROM Employer t where t.verification = 'pending'")
    int getNumberPendingVerification();


    @Query("SELECT new Employer(t.id) FROM Employer t Where t.token = ?1")
    Optional<Employer> findIdByToken(String token);

    @Query("SELECT COALESCE(t.id,0) FROM Employer t Where t.token = ?1")
    int getIdByToken(String token);
    @Query("SELECT t FROM Employer t  WHERE t.email = ?1")
    Optional<Employer> checkExisting(String email);

    @Query("SELECT t FROM Employer t  WHERE t.token = ?1")
    Optional<Employer> checkExistingByToken(String token);

    @Query("SELECT t FROM Employer t  WHERE t.id = ?1")
    Optional<Employer> findEmployerByID(int id);

    @Query("SELECT new Employer(t.id, t.employerName, t.companyName, t.IC, t.ssm, t.SSMImage, t.frontICImage, t.selfieImage, t.verificationDate, t.imageFile) FROM Employer t  WHERE t.id = ?1")
    Optional<Employer> getVerificationInformation(int id);

    @Query("SELECT new Employer(t.id, t.companyName, t.email, t.verification, COALESCE(t.imageFile,''), t.rating) FROM Employer t  WHERE t.email = ?1 and  t.password = ?2")
    Employer login(String email, String password);
//    @Query("SELECT new Employer(t.id, t.name, t.email, t.verification, t.jobSetting, COALESCE(t.imageFile,''), t.token, t.rating) FROM Employer t  WHERE t.email = ?1")
//    Employer getBasicInfo(String email);
//    @Modifying(clearAutomatically = true)
//    @Transactional
//    @Query("update Employer t set t.imageFile = ?1, t.name = ?2, t.IC = ?3, t.phoneNumber =?4, t.gender= ?5, t.employmentStatus = ?6, highestEducation =?7 where t.id = ?8")
//    void updateProfile(String imageFile, String name, String ic, String phoneNumber, String gender, String employmentStatus, String highestEducation, int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Employer t set t.token = ?1, t.notificationToken = ?2 where t.id = ?3")
    void saveToken(String token, String notificationToken,  int id);


    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Employer t set t.verification = ?1 where t.id = ?2")
    void updateVerification(String status, int id);


    @Query("SELECT new Employer(t.companyName, t.companyEmail, t.phoneNumber, t.location, t.ssm, t.imageFile, t.id, t.verification, t.state, t.city) FROM Employer t  WHERE t.token = ?1")
    Optional<Employer> getCompanyInformation(String token);

    @Query("SELECT new Employer(t.companyName, t.companyEmail, t.phoneNumber, t.location, t.ssm, t.imageFile, t.id, t.verification, t.state, t.city) FROM Employer t  WHERE t.id = ?1")
    Optional<Employer> getCompanyInformationByID(int token);

    @Query("SELECT new Employer(t.companyDescription, t.id) FROM Employer t  WHERE t.token = ?1")
    Optional<Employer> getCompanyDescription (String token);

    @Query("SELECT new Employer(t.companyDescription, t.id) FROM Employer t  WHERE t.id = ?1")
    Optional<Employer> getCompanyDescriptionByID (int id);

    @Query("SELECT new Employer(t.operatingHours, t.id) FROM Employer t  WHERE t.token = ?1")
    Optional<Employer> getOperatingHours (String token);

    @Query("SELECT new Employer(t.operatingHours, t.id) FROM Employer t  WHERE t.id = ?1")
    Optional<Employer> getOperatingHoursByID (int employerID);
    @Query("SELECT t FROM Employer t  WHERE t.token = ?1")
    Optional<Employer> getInformation (String token);

    @Query("SELECT new Employer(t.id, t.password) FROM Employer t  WHERE t.token = ?1")
    Optional<Employer> checkOldPassword (String token);

    @Query("SELECT  COALESCE(t.notificationToken,'') FROM Employer t  WHERE t.id = ?1")
    String checkNotificationToken (int id);

    @Query("SELECT new Employer(t.companyName,t.verification, COALESCE(t.imageFile,''), t.rating ) FROM Employer t  WHERE t.token = ?1")
    Optional<Employer> getbasicinfo (String token);

    @Query("SELECT t FROM Employer t  LEFT JOIN Job j ON ( t.id = j.employerID)  WHERE j.id = ?1")
    Optional<Employer> getEmployerByJob (int jobID);

    @Query("SELECT CASE " +
            "   WHEN e.companyName IS NULL OR e.companyEmail IS NULL OR e.imageFile IS NULL OR e.ssm IS NULL OR e.phoneNumber IS NULL OR e.location IS NULL OR e.companyDescription IS NULL OR e.operatingHours IS NULL " +
            "   THEN 'incomplete' " +
            "   ELSE 'complete' " +
            "   END " +
            "FROM Employer e " +
            "WHERE e.id = ?1")
    String checkEmployerCompleteness(int id);

    @Query("SELECT e.verification FROM Employer e " +
            "WHERE e.id = ?1")
    String checkEmployerVerification(int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Employer t set t.companyName = ?1, t.companyEmail = ?2, t.phoneNumber = ?3, t.location =?4, t.ssm= ?5, t.imageFile = ?6, t.state = ?8, t.city = ?9 where t.id = ?7")
    void updateInformation(String companyName, String companyEmail, String phoneNumber, String location, String ssm, String imageFile, int id, String state, String city);
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Employer t set t.companyDescription = ?1 where t.id = ?2")
    void updateDescrption(String description, int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Employer t set t.operatingHours = ?1 where t.id = ?2")
    void updateOperatingHours(String operatingHours, int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Employer t set t.password = ?1 where t.id = ?2")
    void updateNewPassword(String newPassword, int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Employer t set t.password = ?1, t.verificationCode = '', t.token = '' where t.id = ?2")
    void resetPassword(String newPassword, int id);


    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update employer set blockDate = ?2, blockPermanent = ?3 where employerID = ?1", nativeQuery = true)
    void deactive(int id, String blockDate, int permanent);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Employer t set t.token = '', t.notificationToken = '' where t.id = ?1")
    void removeToken(int id);


}