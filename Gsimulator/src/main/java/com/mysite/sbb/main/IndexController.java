package com.mysite.sbb.main;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

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
//test3
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
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/main/modify/{id}")
    public String mainModify(MainForm mainForm, @PathVariable("id") Integer id, Principal principal) {
        Sugang sugang = this.showService.getSugangsForCurrentUser(id);
        if(!sugang.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        mainForm.setSemester(sugang.getSemester());
        mainForm.setSubjectName(sugang.getSubjectName());
        mainForm.setCredit(sugang.getCredit());
        mainForm.setGrade(sugang.getGrade());
        mainForm.setSubjectType(sugang.getSubjectType());
        mainForm.setCulture(sugang.getCulture());
        return "modify_form";
    }
    
    
}
