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

    // 217.160.10.113:8080/qr?encryptedkey=XF98EG 1 013 Einstieg
    // 217.160.10.113:8080/qr?encryptedkey=HJ34IK 2 Bibliothek Gatter
    // 217.160.10.113:8080/qr?encryptedkey=FQ90BM 3 010 Primzahlen
    // 217.160.10.113:8080/qr?encryptedkey=KL73CS 4 010 Distanz
    // 217.160.10.113:8080/qr?encryptedkey=QH17BX 5 013 Mimik
    // 217.160.10.113:8080/qr?encryptedkey=EI45KS 6 013 Menschen scannen
    // 217.160.10.113:8080/qr?encryptedkey=TU34PC 7 010 Informatik Hangman
    // 217.160.10.113:8080/qr?encryptedkey=AW91LD 8 Galerie 1 Legend of the wild sea
    // 217.160.10.113:8080/qr?encryptedkey=GI04ZI 9 212 Anidex
    // 217.160.10.113:8080/qr?encryptedkey=LQ61VK 10
    // 217.160.10.113:8080/qr?encryptedkey=ZU92MX 11
    // 217.160.10.113:8080/qr?encryptedkey=SE70MV 12





    @GetMapping("/qr")
    public String loadQuestion(Model model, @RequestParam @NonNull String encryptedkey) {

        Question question = QuestionList.getQuestionByEncryptedKey(encryptedkey);
        model.addAttribute("number", QuestionList.getQuestionNumberByEncryptedKey(encryptedkey));
        model.addAttribute("question", question);
        //model.addAttribute("hint", "Tipp...");
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
        boolean numberIsValid = number == user.getPostenListMixed().get(user.getPostennummer()-1);

        if (!numberIsValid) {
            return "Du bist nicht beim korrekten Posten!";
        }

        Question question = QuestionList.getQuestionByNumber(number);

        for (String s : question.getCorrectAnswers()) {
            if (s.equalsIgnoreCase(answer)) {

                if (user.getPostennummer() == QuestionList.getTotalNumberOfQuestions()){
                    user.setPostennummer(user.getPostennummer() + 1);
                    return "Die Antwort ist richtig! Damit hast du alle Posten gelöst. Gehe zurück zum Start, um die Schnitzeljagd abzuschliessen";
                }

                System.out.println("Aufgabe " + number + " wurde richtig beantwortet.");
                System.out.println(user);
                String placeOfNextPosten = QuestionList.getQuestionByNumber(user.getNextPostenNumber()).getPlace();
                String response = "Die Antwort ist richtig! Den nächsten Posten findest du hier: " + placeOfNextPosten;
                user.setPostennummer(user.getPostennummer() + 1);
                return response;
            }
        }

        return "Die Antwort ist nicht korrekt...";
    }


}
