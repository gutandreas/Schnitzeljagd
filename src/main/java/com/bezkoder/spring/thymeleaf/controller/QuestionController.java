package com.bezkoder.spring.thymeleaf.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bezkoder.spring.thymeleaf.Question;
import com.bezkoder.spring.thymeleaf.QuestionList;

import com.bezkoder.spring.thymeleaf.entity.User;
import com.bezkoder.spring.thymeleaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bezkoder.spring.thymeleaf.entity.Tutorial;
import com.bezkoder.spring.thymeleaf.repository.TutorialRepository;

import javax.transaction.Transactional;


@Controller
public class QuestionController {

  @Autowired
  private TutorialRepository tutorialRepository;

  @Autowired
  private UserRepository userRepository;

  @GetMapping("/tutorials")
  public String getAll(Model model, @Param("keyword") String keyword) {
    try {
      List<Tutorial> tutorials = new ArrayList<Tutorial>();

      if (keyword == null) {
        tutorialRepository.findAll().forEach(tutorials::add);
      } else {
        tutorialRepository.findByTitleContainingIgnoreCase(keyword).forEach(tutorials::add);
        model.addAttribute("keyword", keyword);
      }

      model.addAttribute("tutorials", tutorials);
    } catch (Exception e) {
      model.addAttribute("message", e.getMessage());
    }

    return "tutorials";
  }

  @GetMapping("/tutorials/new")
  public String addTutorial(Model model) {
    Tutorial tutorial = new Tutorial();
    tutorial.setPublished(true);

    model.addAttribute("tutorial", tutorial);
    model.addAttribute("pageTitle", "Create new Tutorial");

    return "tutorial_form";
  }

  @PostMapping("/tutorials/save")
  public String saveTutorial(Tutorial tutorial, RedirectAttributes redirectAttributes) {
    try {
      tutorialRepository.save(tutorial);

      redirectAttributes.addFlashAttribute("message", "The Tutorial has been saved successfully!");
    } catch (Exception e) {
      redirectAttributes.addAttribute("message", e.getMessage());
    }

    return "redirect:/tutorials";
  }

  @GetMapping("/tutorials/{id}")
  public String editTutorial(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
    try {
      Tutorial tutorial = tutorialRepository.findById(id).get();

      model.addAttribute("tutorial", tutorial);
      model.addAttribute("pageTitle", "Edit Tutorial (ID: " + id + ")");

      return "tutorial_form";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());

      return "redirect:/tutorials";
    }
  }

  @GetMapping("/tutorials/delete/{id}")
  public String deleteTutorial(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
    try {
      tutorialRepository.deleteById(id);

      redirectAttributes.addFlashAttribute("message", "The Tutorial with id=" + id + " has been deleted successfully!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
    }

    return "redirect:/tutorials";
  }

  @GetMapping("/tutorials/{id}/published/{status}")
  public String updateTutorialPublishedStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean published,
      Model model, RedirectAttributes redirectAttributes) {
    try {
      tutorialRepository.updatePublishedStatus(id, published);

      String status = published ? "published" : "disabled";
      String message = "The Tutorial id=" + id + " has been " + status;

      redirectAttributes.addFlashAttribute("message", message);
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
    }

    return "redirect:/tutorials";
  }

  // localhost:8080/qr?encryptedkey=XF98EG
  // localhost:8080/qr?encryptedkey=HJ34IK
  // localhost:8080/qr?encryptedkey=FQ90BM

  @GetMapping("/qr")
  public String loadQuestion(Model model, @RequestParam @NonNull String encryptedkey){

    Question question = QuestionList.getQuestionByEncryptedKey(encryptedkey);
    model.addAttribute("number", QuestionList.getQuestionNumberByEncryptedKey(encryptedkey));
    model.addAttribute("question", question.getQuestion());
    model.addAttribute("hint", "Tipp...");
    return "questions";
  }

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

    model.addAttribute("vorname", user.getVorname());
    model.addAttribute("zeit", "8:43");
    model.addAttribute("rang", 9);






    return "finish";
  }


  @GetMapping("/hint")
  @ResponseBody
  public String getHint(Model model, @RequestParam @NonNull int number){
    System.out.println("Hint zu Aufgabe " + number + " abgefragt.");
    return QuestionList.getHintByQuestionNumber(number);
  }

  @GetMapping("/check")
  @ResponseBody
  @Transactional
  public String checkAnswer(Model model, @RequestParam @NonNull int number, @RequestParam @NonNull String answer, @RequestParam @NonNull String code) {

    boolean codeIsValid = userRepository.existsByCode(code);

    if (!codeIsValid){
      return "Dein Code ist nicht gültig...";
    }

    boolean answerCorrect = false;
    Question question = QuestionList.getQuestionByNumber(number);

    for (String s : question.getCorrectAnswers()) {
      if (s.toLowerCase().equals(answer.toLowerCase())) {
        answerCorrect = true;
        System.out.println("Aufgabe " + number + " wurde richtig beantwortet.");
        User user = userRepository.findByCode(code).get(0);
        int postennummerBefore = user.getPostennummer();
        int postennummerAfter = postennummerBefore + 1;
        user.setPostennummer(postennummerAfter);
        System.out.println(user);
        return "Die Antwort ist richtig! Den nächsten Posten findest du hier: " + question.getNextStep();
      }
    }

    return "Die Antwort ist nicht korrekt...";
  }











}
