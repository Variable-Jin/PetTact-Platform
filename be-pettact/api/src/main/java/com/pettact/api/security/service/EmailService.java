package com.pettact.api.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendVerificationLink(String toEmail, String token) {
        String link = "http://localhost:8080/v1/user/email/verify?token=" + token;

        String html = """
            <div style="font-family:Arial,sans-serif; font-size:14px;">
                <h3>[PetTact 이메일 인증]</h3>
                <p>아래 버튼을 클릭하여 이메일 인증을 완료해주세요.</p>
                <a href="%s" style="display:inline-block; padding:10px 20px; background:#4CAF50; color:white; text-decoration:none; border-radius:4px;">이메일 인증하기</a>
            </div>
            """.formatted(link);

        sendEmail(toEmail, "PetTact 이메일 인증", html);
    }
	
	// 이메일 전송
    private void sendEmail(String to, String subject, String html) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("메일 전송 실패", e);
        }
    }
}
