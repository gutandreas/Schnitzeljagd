package com.bezkoder.spring.thymeleaf.controller;

import com.bezkoder.spring.thymeleaf.QuestionList;
import com.bezkoder.spring.thymeleaf.entity.User;
import com.bezkoder.spring.thymeleaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class FinishController {

    @Autowired
    private UserRepository userRepository;

    // localhost:8080/finish?encryptedkey=KJ73DP&code=...
    @GetMapping("/finish")
    @ResponseBody
    @Transactional
    public String checkFinish(Model model,  @RequestParam @NonNull String code){

        System.out.println("Schnitzeljagd wird abgeschlossen");

        boolean codeIsValid = userRepository.existsByCode(code);

        if (!codeIsValid){
            return "Dein Code ist nicht gültig...";
        }

        User user = userRepository.findByCode(code).get(0);

        boolean numberIsValid = user.getPostennummer() == QuestionList.getTotalNumberOfQuestions() + 1;

        if (!numberIsValid){
            return "Du hast noch nicht alle Posten gelöst!";
        }


        user.setStop(LocalDateTime.now());
        user.setFertig(true);
        user.calculateDuration();

       String answer = "Herzliche Gratulation, " + user.getVorname() + ", du hast die Schnitzeljagd in "
               + user.getDurationAsFormattedString() + " gemeistert!";

        return answer;
    }

    @GetMapping("/ranking")
    public String getRanking(Model model){
        List<User> ranking = userRepository.getRanking();
        model.addAttribute("ranking", ranking);

        return "ranking";
    }

    @GetMapping("/checkout")
    public String getCheckoutPage(){


        return "checkout";
    }
}
