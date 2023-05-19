package com.bezkoder.spring.thymeleaf.controller;

import com.bezkoder.spring.thymeleaf.Codes;
import com.bezkoder.spring.thymeleaf.entity.User;
import com.bezkoder.spring.thymeleaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class IndexController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestParam String vorname, @RequestParam String nachname, RedirectAttributes redirectAttributes){
        try {
            System.out.println(vorname);
            String code = Codes.getNewCode();

            User user = new User();
            user.setVorname(vorname);
            user.setNachname(nachname);
            user.setPostennummer(1);
            user.setCode(code);
            user.setStart(LocalDateTime.now());
            userRepository.save(user);

            for (User u : userRepository.findAll()){
                System.out.println(u);
            }

            Codes.printCodes();

            redirectAttributes.addFlashAttribute("message", "Der User wurde erfolgreich gespeichert.");
            return "redirect:/welcome?code=" + code + "&vorname=" + vorname;


        } catch (Exception e) {
            redirectAttributes.addAttribute("message", e.getMessage());
            System.out.println(e);
        }

        return "error";
    }

    @GetMapping("/welcome")
    public String welcome(Model model, @RequestParam String code, @RequestParam String vorname){
        model.addAttribute("code", code);
        model.addAttribute("vorname", vorname);

        return "welcome";
    }



}
