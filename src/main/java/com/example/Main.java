/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

@Controller
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  String index() {
    return "index";
  }

  @RequestMapping("/hello")
  String hello(HttpServletRequest request, Model model) {
      // RelativisticModel.select();
      String visitorName = request.getParameter("name");
      String visitorMail = request.getParameter("e-mail");
      String visitorMessage = request.getParameter("message");
      
      final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
 // Get a Properties object
    // Properties props = System.getProperties();
    Properties props = new Properties();
    props.setProperty("mail.smtp.host", "smtp.gmail.com");
    props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
    // props.setProperty("mail.smtp.socketFactory.fallback", "false");
    props.setProperty("mail.smtp.port", "587");
    props.setProperty("mail.smtp.socketFactory.port", "465");
    props.put("mail.smtp.auth", "true");
    // props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.ssl.enable", "true");
    // props.put("mail.debug", "true");
    // props.put("mail.store.protocol", "pop3");
    // props.put("mail.transport.protocol", "smtp");
    final String username = "jxqu2web@gmail.com";//
    final String password = "qjx179608472";
    String sol;
    try{
      Session session = Session.getDefaultInstance(props, 
                          new Authenticator(){
                             protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                             }});

   // -- Create a new message --
      Message msg = new MimeMessage(session);

   // -- Set the FROM and TO fields --
      msg.setFrom(new InternetAddress(username));
      msg.setRecipients(Message.RecipientType.TO, 
                        InternetAddress.parse("isjunxianqu@gmail.com",false));
      msg.setSubject("from jxqu_web visitor");
      msg.setText(visitorMail + " " + visitorMessage);
      msg.setSentDate(new Date());
      Transport.send(msg);
      sol ="Message sent.";
      System.out.println(sol);
    }catch (MessagingException e){ 
      sol = "error, cause: "+ e;
      System.out.println( sol );
    }

      String result = visitorName.toUpperCase();
      model.addAttribute("final", sol);
      model.addAttribute("name", result);
      return "hello";
  }

  
  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getTimestamp("tick"));
      }

      model.put("records", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }

}
