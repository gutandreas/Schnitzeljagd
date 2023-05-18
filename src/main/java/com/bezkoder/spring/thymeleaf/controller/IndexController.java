package com.bezkoder.spring.thymeleaf.controller;

import com.bezkoder.spring.thymeleaf.entity.Tutorial;
import com.bezkoder.spring.thymeleaf.entity.User;
import com.bezkoder.spring.thymeleaf.repository.UserRepository;
import org.hibernate.query.Query;
import org.hibernate.query.internal.QueryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class IndexController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestParam String vorname, @RequestParam String nachname, RedirectAttributes redirectAttributes){
        try {
            System.out.println(vorname);
            User user = new User();
            user.setVorname(vorname);
            user.setNachname(nachname);
            user.setUuid(UUID.randomUUID());
            user.setStart(LocalDateTime.now());
            userRepository.save(user);

            for (User u : userRepository.findAll()){
                System.out.println(u);
            }

            redirectAttributes.addFlashAttribute("message", "Der User wurde erfolgreich gespeichert.");
        } catch (Exception e) {
            redirectAttributes.addAttribute("message", e.getMessage());
        }

        return "redirect:/qr?encryptedkey=XF98EG";
    }


}
