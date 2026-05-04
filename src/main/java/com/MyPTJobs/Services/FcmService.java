package com.MyPTJobs.Services;

import com.MyPTJobs.Class.Job;
import com.MyPTJobs.Class.MessageDTO;
import com.MyPTJobs.Repository.EmployerRepository;
import com.MyPTJobs.Repository.JobRepository;
import com.MyPTJobs.Repository.JobSeekerRepository;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FcmService {
    private String host = "http://localhost:8080/resources/";
    @Autowired
    FirebaseMessaging firebaseMessaging;

    @Autowired
    EmployerRepository employerRepository;

    @Autowired
    JobSeekerRepository jobSeekerRepository;

    @Autowired
    JobRepository jobRepository;

    public String sendNotificationToSpecificDevice(String type, int id, MessageDTO note) throws FirebaseMessagingException {
        Notification notification= Notification
                .builder()
                .setTitle(note.getSubject())
                .setBody(note.getContent())
                .setImage(note.getImage())
                .build();
        Message message = null;
        String token =  "";
        switch (type){
            case "jobSeeker":
                note.setImage(host+"logo.png");

                token = jobSeekerRepository.checkNotificationToken(id);
                if ( !token.isEmpty() ){
                    message = Message
                            .builder()
                            .setToken(token)
                            .setNotification(notification)
                           // .putAllData(note.getData())
                            .build();
//                    firebaseMessaging.send(message);
                }
                break;
            case "employer":
                token = employerRepository.checkNotificationToken(id);
                note.setImage(host+"logo-employer.png");
                if ( !token.isEmpty() ){
                    message = Message
                            .builder()
                            .setToken(token)
                            .setNotification(notification)
                           // .putAllData(note.getData())
                            .build();
//                    firebaseMessaging.send(message);
                }
                break;
            case "job":
                token = jobRepository.checkNotificationToken(id);

                if ( !token.isEmpty() ){
                    Optional<Job> job = jobRepository.getSelectedJob((id));
                    if (job.isPresent() ){
                        note.setSubject(job.get().getTitle());
                        note.setImage(host+"logo-employer.png");
                        notification= Notification
                                .builder()
                                .setTitle(note.getSubject())
                                .setBody(note.getContent())
                                .setImage(note.getImage())
                                .build();

                        message = Message
                                .builder()
                                .setToken(token)
                                .setNotification(notification)
//                                .putAllData(note.getData())
                                .build();
                    }else{
                        token = "";
                    }
//                    firebaseMessaging.send(message);
                }
                break;
        }
        if ( (token).isEmpty() ){
            return "";
        }
        return firebaseMessaging.send(message);
    }
    public BatchResponse sendNotificationToMultipleDevices(MessageDTO note, List<String> tokens) throws FirebaseMessagingException {

        Notification notification= Notification
                .builder()
                .setTitle(note.getSubject())
                .setBody(note.getContent())
                .setImage(note.getImage())
                .build();

        MulticastMessage message = MulticastMessage
                .builder()
                .addAllTokens(tokens)
                .setNotification(notification)
                .putAllData(note.getData())
                .build();
        return firebaseMessaging.sendMulticast(message);
    }
    public void subscribeToTopic(List<String> tokens, String topic) throws FirebaseMessagingException {
        firebaseMessaging.subscribeToTopic(tokens,topic);
    }
    public void unSubscribeToTopic(List<String> tokens, String topic) throws FirebaseMessagingException {
        firebaseMessaging.subscribeToTopic(tokens,topic);
    }
    public String sendNotificationToTopic(MessageDTO note, String topic) throws  FirebaseMessagingException{
        Notification notification= Notification
                .builder()
                .setTitle(note.getSubject())
                .setBody(note.getContent())
                .setImage(note.getImage())
                .build();
        Message message = Message
                .builder()
                .setTopic(topic)
                .setNotification(notification)
                .putAllData(note.getData())
                .build();
        return firebaseMessaging.send(message);
    }
}
