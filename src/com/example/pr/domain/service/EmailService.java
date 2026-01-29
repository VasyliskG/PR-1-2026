package com.example.pr.domain.service;

import java.util.Properties;
import java.util.Random;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Сервіс для надсилання email повідомлень через Gmail SMTP.
 */
public class EmailService {

  // Налаштування Gmail SMTP
  private static final String SMTP_HOST = "smtp.gmail.com";
  private static final String SMTP_PORT = "587";

  // ⚠️ УВАГА: В реальному проекті зберігайте в змінних середовища або конфіг-файлі!
  private final String senderEmail;
  private final String appPassword;

  public EmailService(String senderEmail, String appPassword) {
    this.senderEmail = senderEmail;
    this.appPassword = appPassword;
  }

  /**
   * Створює сесію для підключення до Gmail SMTP.
   */
  private Session createSession() {
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", SMTP_HOST);
    props.put("mail.smtp.port", SMTP_PORT);
    props.put("mail.smtp.ssl.trust", SMTP_HOST);
    props.put("mail.smtp.ssl.protocols", "TLSv1.2");

    return Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(senderEmail, appPassword);
      }
    });
  }

  /**
   * Надсилає email з кодом підтвердження.
   */
  public void sendVerificationCode(String recipientEmail, String code) throws MessagingException {
    Session session = createSession();

    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(senderEmail, false));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
    message.setSubject("🗳️ Код підтвердження - Система електронного голосування");

    String htmlContent = buildVerificationEmailHtml(code);
    message.setContent(htmlContent, "text/html; charset=UTF-8");

    Transport.send(message);
  }

  /**
   * Генерує випадковий 6-значний код підтвердження.
   */
  public static String generateVerificationCode() {
    Random random = new Random();
    int code = 100000 + random.nextInt(900000); // 100000-999999
    return String.valueOf(code);
  }

  /**
   * Створює HTML-шаблон листа.
   */
  private String buildVerificationEmailHtml(String code) {
    return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
        </head>
        <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
          <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
            <h1 style="color: #2c3e50; text-align: center;">🗳️ Система електронного голосування</h1>
            <hr style="border: 1px solid #eee;">
            <p style="font-size: 16px; color: #333;">Вітаємо!</p>
            <p style="font-size: 16px; color: #333;">Ваш код підтвердження для реєстрації:</p>
            <div style="text-align: center; margin: 30px 0;">
              <span style="font-size: 36px; font-weight: bold; color: #3498db; letter-spacing: 8px; background: #ecf0f1; padding: 15px 30px; border-radius: 8px;">
                %s
              </span>
            </div>
            <p style="font-size: 14px; color: #666;">Код дійсний протягом <strong>10 хвилин</strong>.</p>
            <p style="font-size: 14px; color: #666;">Якщо ви не реєструвались у системі, проігноруйте цей лист.</p>
            <hr style="border: 1px solid #eee;">
            <p style="font-size: 12px; color: #999; text-align: center;">© 2026 Система електронного голосування</p>
          </div>
        </body>
        </html>
        """.formatted(code);
  }
}
