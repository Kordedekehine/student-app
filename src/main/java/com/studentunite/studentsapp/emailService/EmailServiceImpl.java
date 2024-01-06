package com.studentunite.studentsapp.emailService;


//import com.studentunite.studentsapp.AppUser.AppUser;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//
//
//
//@Service
//public class EmailServiceImpl  {
//
//    private final JavaMailSender javaMailSender;
//
//    private final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//
//   public EmailServiceImpl(JavaMailSender javaMailSender) {
//        this.javaMailSender = javaMailSender;
//    }
//
//
//    public void sendRegistrationSuccessfulEmail(AppUser appUser, String token) {
//        String link = "http://localhost:9090" + token;
//
//        simpleMailMessage.setTo(appUser.getEmail());
//        simpleMailMessage.setSubject("Account Registration");
//        simpleMailMessage.setFrom("STUDENT APP");
//        String template = "Dear [[name]],\n"
//                + "Thanks for registering on Student App.\n"
//                + "Kindly use the code below to validate your account and activate your account:\n"
//                + "[[code]]\n"
//                + "Thank you.\n"
//                + "Student App team";
//        template = template.replace("[[name]]", appUser.getUsername());
//        template = template.replace("[[code]]", token);
//        simpleMailMessage.setText(template);
//
//        try {
//            javaMailSender.send(simpleMailMessage);
//        } catch (Exception exception) {
//            exception.printStackTrace();
//            throw new RuntimeException(String.format("Mail not sent"));
//        }
//    }
//
//
//    public void sendVerificationMessage(AppUser user) {
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        simpleMailMessage.setFrom("STUDENT APP");
//        simpleMailMessage.setTo(user.getEmail());
//        simpleMailMessage.setSubject("Welcome to student insight app");
//        String template = "Dear [[name]],\n"
//                + "Your account has been verified, kindly login to explore\n"
//                + "[[URL]]\n"
//                + "Thank you,\n\n"
//                + "The student app Team";
//        template = template.replace("[[name]]", user.getUsername());
//        template = template.replace("[[URL]]", "STUDENT APP");
//        // message.setText(template);
//        simpleMailMessage.setText(template);
//        try {
//            javaMailSender.send(simpleMailMessage);
//        } catch (Exception exception) {
//            throw new RuntimeException("Message not sent");
//        }
//    }
//
//}
//
//
