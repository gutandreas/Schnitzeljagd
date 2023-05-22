package com.bezkoder.spring.thymeleaf.controller;

import com.bezkoder.spring.thymeleaf.entity.User;
import com.bezkoder.spring.thymeleaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class FinishController {

    @Autowired
    private UserRepository userRepository;

    // localhost:8080/finish?encryptedkey=KJ73DP&code=...
    @GetMapping("/finish")
    @Transactional
    public String checkFinish(Model model, @RequestParam @NonNull String encryptedkey, @RequestParam @NonNull String code){

        boolean codeIsValid = userRepository.existsByCode(code);

        if (!codeIsValid){
            return "Dein Code ist nicht gültig...";
        }

        User user = userRepository.findByCode(code).get(0);
        user.setStop(LocalDateTime.now());
        user.setFertig(true);
        user.calculateDuration();

        model.addAttribute("vorname", user.getVorname());
        model.addAttribute("zeit", user.getDuration().toSeconds());
        model.addAttribute("rang", 9);

        return "finish";
    }

    @GetMapping("/ranking")
    public String getRanking(Model model){
        List<User> ranking = userRepository.getRanking();
        String list = "";
        for (User u : ranking){
            list += u.getVorname() + "\t" + u.getNachname() + "\t" + u.getDuration().toSeconds() + "\n";
        }
        model.addAttribute("ranking", list);

        return "ranking";
    }
}
