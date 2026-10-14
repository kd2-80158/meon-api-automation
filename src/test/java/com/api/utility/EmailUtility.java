package com.api.utility;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class EmailUtility {

	private static Properties loadEmailProps() {
		Properties p = new Properties();
		try (FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/config/email.properties")) {
			p.load(fis);
		} catch (Exception e) {
			// if properties file is missing, we still allow env vars override below
		}
		return p;
	}

	private static String getPropOrEnv(Properties p, String key, String envKey, String defaultValue) {
		String env = System.getenv(envKey);
		if (env != null && !env.isBlank())
			return env;
		String val = p.getProperty(key);
		return val == null ? defaultValue : val;
	}
	
	public static String loadRecipientsFromPropsOrDefault() {

	    // 1) Prefer environment variable (CI safe)
	    String envRecipients = System.getenv("MAIL_RECIPIENTS");
	    if (envRecipients != null && !envRecipients.trim().isEmpty()) {
	        return envRecipients;
	    }

	    // 2) Load from email.properties
	    Properties p = new Properties();
	    try (FileInputStream fis =
	            new FileInputStream(System.getProperty("user.dir") + "/config/email.properties")) {
	        p.load(fis);
	        String propertyRecipients = p.getProperty("mail.recipients");
	        if (propertyRecipients != null && !propertyRecipients.trim().isEmpty()) {
	            return propertyRecipients;
	        }
	    } catch (Exception ignored) {
	        // ignore and fall through
	    }

	    // 3) Fallback default (safe dummy email)
	    return "qa-team@example.com";
	}


	/**
	 * Send email with HTML body and optional attachment. recipientsCsv can be
	 * comma-separated list.
	 */
	public static void sendReportEmail(String subject, String htmlBody, String attachmentPath, String recipientsCsv) {
		try {
			Properties p = loadEmailProps();
			final String host = getPropOrEnv(p, "smtp.host", "SMTP_HOST", "localhost");
			final String port = getPropOrEnv(p, "smtp.port", "SMTP_PORT", "25");
			final String username = getPropOrEnv(p, "smtp.username", "SMTP_USERNAME", null);
			final String password = getPropOrEnv(p, "smtp.password", "SMTP_PASSWORD", null);
			final String from = getPropOrEnv(p, "smtp.from", "SMTP_FROM", username);
			final boolean tls = Boolean.parseBoolean(getPropOrEnv(p, "smtp.tls", "SMTP_TLS", "true"));

			Properties props = new Properties();
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.auth", username != null ? "true" : "false");
			props.put("mail.smtp.starttls.enable", String.valueOf(tls));

			Session session;
			if (username != null && password != null) {
				session = Session.getInstance(props, new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
			} else {
				session = Session.getInstance(props);
			}

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			List<String> recipients = Arrays.stream(recipientsCsv.split(",")).map(String::trim)
					.filter(s -> !s.isEmpty()).collect(Collectors.toList());
			for (String r : recipients) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(r));
			}
			message.setSubject(subject);
			message.setSentDate(new Date());

			// Create the message part for HTML body
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(htmlBody == null ? "" : htmlBody, "text/html; charset=utf-8");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(htmlPart);

			// Add attachment if provided
			if (attachmentPath != null && !attachmentPath.isBlank()) {
				File file = new File(attachmentPath);
				if (file.exists() && file.isFile()) {
					MimeBodyPart attachPart = new MimeBodyPart();
					DataSource source = new FileDataSource(file);
					attachPart.setDataHandler(new DataHandler(source));
					attachPart.setFileName(file.getName());
					multipart.addBodyPart(attachPart);
				} else {
					// Attachment missing — still send email (you can change to throw)
					System.err.println("Attachment not found: " + attachmentPath);
				}
			}

			message.setContent(multipart);

			Transport.send(message);
			System.out.println("Email sent to: " + recipientsCsv);
		} catch (MessagingException me) {
			throw new RuntimeException("Failed to send email", me);
		}
	}
}
