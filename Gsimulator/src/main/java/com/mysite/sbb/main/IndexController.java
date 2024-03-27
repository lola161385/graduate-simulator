package com.mysite.sbb.main;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.sbb.sugang.Sugang;

import jakarta.validation.Valid;


@Controller
public class IndexController {

    @Autowired
    private ShowService showService; // ShowService 주입
    

    @GetMapping("/main/hello")
    public String hello(Model model, @RequestParam(value = "semester", required = false) String semester) {
        List<Sugang> sugangs;
        if (semester == null || semester.equals("all")) {
            sugangs = showService.getSugangsForCurrentUser();
        } else {
            sugangs = showService.getSugangsForCurrentUserAndSemester(semester);
        }
        model.addAttribute("sugangs", sugangs); // 모델에 sugangs 추가
        return "main_form";
    }
//test
    @PostMapping("/main/hello")
    public String sugangModify(@Valid MainForm mainForm, BindingResult BindingResult, Principal principal, @RequestParam("id") Integer id,
                           @RequestParam String semester, @RequestParam String subjectName, @RequestParam String credit, 
                           @RequestParam String grade, @RequestParam String subjectType, @RequestParam String culture) {
    if(BindingResult.hasErrors()){
        return "main_form";
    }
    Sugang sugang = this.showService.getSugangsForCurrentUser(id);
    showService.modify(sugang, semester, subjectName, credit, grade, subjectType, culture);
    return "redirect:/main/hello";  // 수정 후 리다이렉트할 뷰 이름
}
    
}
