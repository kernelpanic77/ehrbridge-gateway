package com.ehrbridge.auth.service;

import com.ehrbridge.auth.dto.VerifyOtpRequest;
import com.ehrbridge.auth.dto.VerifyOtpResponse;
import com.ehrbridge.auth.entity.User;
import com.ehrbridge.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {
    @Autowired
    private final UserRepository userRepository;

    public String generateOtp() {
        Random rnd = new Random();
        int n = 100000 + rnd.nextInt(900000);
        return Integer.toString(n);
    }

    public void sendEmail(String otp, User user) throws MessagingException, UnsupportedEncodingException {
         final String fromEmail = "ehrbridge@gmail.com";
         final String password = "euvyijnwpepekicb";
         final String smtpHostServer = "smtp.gmail.com";

        Properties props = System.getProperties();
        props.put("mail.smtp.host", smtpHostServer);
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getInstance(props, auth);

        MimeMessage message = new MimeMessage(session);

        message.addHeader("Content-type", "text/HTML; charset=UTF-8");
        message.addHeader("format", "flowed");
        message.addHeader("Content-Transfer-Encoding", "8bit");

        message.setFrom(new InternetAddress(fromEmail, "NoReply-JD"));
        message.setReplyTo(InternetAddress.parse("fromEmail", false));
        message.setSubject("Verify EhrBridge Account", "UTF-8");
        message.setText("Your OTP for EHR Bridge is " + otp);
        message.setSentDate(new Date());
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail(), false));

        Transport.send(message);

//        System.out.println(otp);
    }

    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request)
    {
        if(SecurityContextHolder.getContext().getAuthentication() == null)
        {
            return VerifyOtpResponse.builder().message("The user does not exist").build();
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("The email does not exist"));

        if(user.getOtpValidity().after(new Date()))
        {
            return VerifyOtpResponse.builder().message("The OTP has expired please retry").build();
        }
        if(user.getOtp().equals(request.getOtp()))
        {
            return VerifyOtpResponse.builder().message("OTP verification Successful").build();
        }

        return VerifyOtpResponse.builder().message("Incorrect OTP").build();







    }

}
