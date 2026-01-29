package com.example.pr.infrastructure.config;

import java.io.*;
import java.util.Properties;

/**
 * Конфігурація email сервісу.
 * Завантажує налаштування з файлу або змінних середовища.
 */
public class EmailConfig {

  private static final String CONFIG_FILE = "config/email.properties";

  private String senderEmail;
  private String appPassword;
  private boolean enabled;

  public EmailConfig() {
    loadConfig();
  }

  private void loadConfig() {
    // Спочатку пробуємо змінні середовища
    senderEmail = System.getenv("VOTING_EMAIL");
    appPassword = System.getenv("VOTING_EMAIL_PASSWORD");

    if (senderEmail != null && appPassword != null) {
      enabled = true;
      return;
    }

    // Потім пробуємо файл конфігурації
    File configFile = new File(CONFIG_FILE);
    if (configFile.exists()) {
      try (InputStream input = new FileInputStream(configFile)) {
        Properties props = new Properties();
        props.load(input);

        senderEmail = props.getProperty("email.sender");
        appPassword = props.getProperty("email.password");
        enabled = Boolean.parseBoolean(props.getProperty("email.enabled", "true"));
      } catch (IOException e) {
        System.err.println("Помилка завантаження конфігурації email: " + e.getMessage());
        enabled = false;
      }
    } else {
      enabled = false;
    }
  }

  public String getSenderEmail() { return senderEmail; }
  public String getAppPassword() { return appPassword; }
  public boolean isEnabled() { return enabled; }
}
