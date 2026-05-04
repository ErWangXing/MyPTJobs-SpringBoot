package com.MyPTJobs.Repository;

import com.MyPTJobs.Class.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Integer> {
    @Query("SELECT t FROM Administrator t  WHERE t.email = ?1")
    Optional<Administrator> findByEmail(String email);
    @Query("SELECT new Administrator(t.adminID, t.username, t.name, t.email, COALESCE(t.imageFile,'')) FROM Administrator t  WHERE t.adminID = ?1 and  t.password = ?2")
    Optional<Administrator> login(int adminID, String password);
    @Query("SELECT t FROM Administrator t  WHERE t.email = ?1 or t.username = ?1")
    Optional<Administrator> checkExisting(String userInput);

    @Query("SELECT t FROM Administrator t  WHERE t.token = ?1")
    Optional<Administrator> checkExistingByToken(String token);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Administrator t set t.token = ?1 where t.adminID = ?2")
    void saveToken(String token, int id);

    @Query("SELECT new Administrator(t.adminID, t.password) FROM Administrator t  WHERE t.token = ?1")
    Optional<Administrator> checkOldPassword (String token);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Administrator t set t.password = ?1 where t.adminID = ?2")
    void updateNewPassword(String newPassword, int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Administrator t set t.password = ?1, t.verificationCode = '', t.token = '' where t.adminID = ?2")
    void resetPassword(String newPassword, int id);
}