package com.ehrbridge.gateway.service;

import com.ehrbridge.gateway.dto.auth.VerifyOtpRequest;
import com.ehrbridge.gateway.dto.auth.VerifyOtpResponse;
import com.ehrbridge.gateway.entity.User;
import com.ehrbridge.gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

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

    public ResponseEntity<VerifyOtpResponse> verifyOtp(VerifyOtpRequest request)
    {
        if(SecurityContextHolder.getContext().getAuthentication() == null)
        {
            return new ResponseEntity<VerifyOtpResponse>(VerifyOtpResponse.builder().message("The user does not exist").build(), HttpStatusCode.valueOf(403));
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("The email does not exist"));

        if(user.getOtpValidity().before(new Date()))
        {
            return new ResponseEntity<VerifyOtpResponse>(VerifyOtpResponse.builder().message("The OTP has expired please retry").build(), HttpStatusCode.valueOf(402));
        }


        if(passwordEncoder.matches(request.getOtp(), user.getOtp()))
        {
            user.setOtpValidity(new Date());
            userRepository.save(user);

            String token = jwtService.generateToken(user);

            return new ResponseEntity<VerifyOtpResponse>(VerifyOtpResponse.builder().message("OTP verification Successful").ehrbid(user.getEhrbID()).token(token).build(), HttpStatusCode.valueOf(200));
        }

        return new ResponseEntity<VerifyOtpResponse>(VerifyOtpResponse.builder().message("Incorrect OTP").build(), HttpStatusCode.valueOf(401));







    }

}
