package com.bezkoder.spring.thymeleaf.controller;

import com.bezkoder.spring.thymeleaf.Codes;
import com.bezkoder.spring.thymeleaf.QuestionList;
import com.bezkoder.spring.thymeleaf.entity.User;
import com.bezkoder.spring.thymeleaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/admin/load")
    public String getAdminPage(Model model){
        List<User> data = userRepository.findAll();
        model.addAttribute("data", data);

        return "admin";
    }

    @DeleteMapping("/admin/delete")
    @Transactional
    public ResponseEntity<String> alleDatenLoeschen() {
        userRepository.deleteAll();
        Codes.resetIndex();
        return ResponseEntity.status(HttpStatus.OK).body("Alle Daten wurden erfolgreich gelöscht.");
    }

    @GetMapping("/admin/changeModus")
    public ResponseEntity<String> changeModus(@RequestParam int modus) {
        alleDatenLoeschen();
        QuestionList.addQuestionsToMap(modus);
        return ResponseEntity.status(HttpStatus.OK).body("Alle Daten wurden erfolgreich gelöscht.");
    }


}
