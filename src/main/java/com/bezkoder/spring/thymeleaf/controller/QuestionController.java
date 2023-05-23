package com.bezkoder.spring.thymeleaf.controller;

import com.bezkoder.spring.thymeleaf.Question;
import com.bezkoder.spring.thymeleaf.QuestionList;
import com.bezkoder.spring.thymeleaf.entity.User;
import com.bezkoder.spring.thymeleaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;


@Controller
public class QuestionController {


    @Autowired
    private UserRepository userRepository;

    // localhost:8080/qr?encryptedkey=XF98EG
    // localhost:8080/qr?encryptedkey=HJ34IK
    // localhost:8080/qr?encryptedkey=FQ90BM

    @GetMapping("/qr")
    public String loadQuestion(Model model, @RequestParam @NonNull String encryptedkey) {

        Question question = QuestionList.getQuestionByEncryptedKey(encryptedkey);
        model.addAttribute("number", QuestionList.getQuestionNumberByEncryptedKey(encryptedkey));
        model.addAttribute("question", question.getQuestion());
        model.addAttribute("hint", "Tipp...");
        return "questions";
    }


    @GetMapping("/hint")
    @ResponseBody
    public String getHint(Model model, @RequestParam @NonNull int number) {
        System.out.println("Hint zu Aufgabe " + number + " abgefragt.");
        return QuestionList.getHintByQuestionNumber(number);
    }

    @GetMapping("/check")
    @ResponseBody
    @Transactional
    public String checkAnswer(Model model, @RequestParam @NonNull int number, @RequestParam @NonNull String answer, @RequestParam @NonNull String code) {


        boolean codeIsValid = userRepository.existsByCode(code);

        if (!codeIsValid) {
            return "Dein Code ist nicht gültig...";
        }

        User user = userRepository.findByCode(code).get(0);
        boolean numberIsValid = number == user.getPostennummer();

        if (!numberIsValid) {
            return "Du bist nicht beim korrekten Posten!";
        }

        Question question = QuestionList.getQuestionByNumber(number);

        for (String s : question.getCorrectAnswers()) {
            if (s.equalsIgnoreCase(answer)) {
                System.out.println("Aufgabe " + number + " wurde richtig beantwortet.");
                user.setPostennummer(user.getPostennummer() + 1);
                System.out.println(user);
                return "Die Antwort ist richtig! Den nächsten Posten findest du hier: " + question.getNextStep();
            }
        }

        return "Die Antwort ist nicht korrekt...";
    }


}
