package com.mysite.sbb.main;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.sbb.sugang.Sugang;

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
}
