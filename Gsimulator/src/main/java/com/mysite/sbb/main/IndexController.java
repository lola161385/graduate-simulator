package com.mysite.sbb.main;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
import com.mysite.sbb.sugang.SugangService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class IndexController {

    @Autowired
    private final ShowService showService;
    private final SugangService sugangService;
    private final UserRepository userRepository;
    
    @GetMapping("/main/sgc")
    public String SGC(Model model, Principal principal) {	
    	SiteUser currentUser = showService.getCurrentUser();
    	Checkcs checkcs = showService.getCheckcsByYear(currentUser.getEnteryear().toString());
        List<Sugang> sugangs = showService.getSugangsForCurrentUser();
        
        Integer totalCredits = showService.getTotalCredits(currentUser);
        Integer culturetotalCredits = showService.getCultureTotalCredits(currentUser);
        Integer cultureCredits = showService.getCultureCredits(currentUser);
        Integer cultureCredit = showService.getCultureCredit(currentUser);
        Integer majortotalCredits = showService.getMajorTotalCredits(currentUser);
        Integer majorCredits = showService.getMajorCredits(currentUser);
        Integer majorCredit = showService.getMajorCredit(currentUser);
        Integer normalCredit = showService.getNormalCredit(currentUser);
        Long chapelCount = showService.getChapelCount(currentUser);
        
        model.addAttribute("site_user", currentUser);
        model.addAttribute("sugangs", sugangs); // 모델에 sugangs 추가
        model.addAttribute("totalCredits", totalCredits); // 모델에 총학점 추가
        model.addAttribute("culturetotalCredits", culturetotalCredits);
        model.addAttribute("cultureCredits", cultureCredits);
        model.addAttribute("cultureCredit", cultureCredit);
        model.addAttribute("majortotalCredits", majortotalCredits);
        model.addAttribute("majorCredits", majorCredits);
        model.addAttribute("majorCredit", majorCredit);
        model.addAttribute("normalCredit", normalCredit);
        model.addAttribute("chapelCount", chapelCount);
        model.addAttribute("hasTakenCs1", showService.hasTakenCultureSubject(currentUser, checkcs.getCs1()));
        model.addAttribute("hasTakenCs2", showService.hasTakenCultureSubject(currentUser, checkcs.getCs2()));
        model.addAttribute("hasTakenCs3", showService.hasTakenCultureSubject(currentUser, checkcs.getCs3()));
        model.addAttribute("hasTakenCs4", showService.hasTakenCultureSubject(currentUser, checkcs.getCs4()));
        model.addAttribute("hasTakenCs5", showService.hasTakenCultureSubject(currentUser, checkcs.getCs5()));
        model.addAttribute("hasTakenCs6", showService.hasTakenCultureSubject(currentUser, checkcs.getCs6()));
        
        model.addAttribute("sumCreditsCs1", showService.getSumOfCreditsForCulture(currentUser, checkcs.getCs1()));
        model.addAttribute("sumCreditsCs2", showService.getSumOfCreditsForCulture(currentUser, checkcs.getCs2()));
        model.addAttribute("sumCreditsCs3", showService.getSumOfCreditsForCulture(currentUser, checkcs.getCs3()));
        model.addAttribute("sumCreditsCs4", showService.getSumOfCreditsForCulture(currentUser, checkcs.getCs4()));
        model.addAttribute("sumCreditsCs5", showService.getSumOfCreditsForCulture(currentUser, checkcs.getCs5()));
        model.addAttribute("sumCreditsCs6", showService.getSumOfCreditsForCulture(currentUser, checkcs.getCs6()));
        
    	if (principal != null) {
            String username = principal.getName();
            Optional<SiteUser> siteUserOpt = userRepository.findByUsername(username);
            if (siteUserOpt.isPresent()) {
                model.addAttribute("site_user", siteUserOpt.get());
            } else {
                // 오류 처리
            }
        }
        return "gs_form";
    }

    @GetMapping("/main/sugang")
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

    @PostMapping("/main/sugang")
    public String sugangModify(@Valid MainForm mainForm, BindingResult BindingResult, Principal principal, @RequestParam("id") Integer id,
                           @RequestParam String semester, @RequestParam String subjectName, @RequestParam Integer credit, 
                           @RequestParam String grade, @RequestParam String subjectType, @RequestParam String culture) {
    if(BindingResult.hasErrors()){
        return "main_form";
    }
    Sugang sugang = this.showService.getSugangsForCurrentUser(id);
    showService.modify(sugang, semester, subjectName, credit, grade, subjectType, culture);
    return "redirect:/main/sugang";  // 수정 후 리다이렉트할 뷰 이름
}
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/main/modify/{id}")
    public String mainModify(Model model, @PathVariable("id") Integer id, Principal principal) {
        Sugang sugang = this.showService.getSugangsForCurrentUser(id);
        if(!sugang.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        MainForm mainForm = new MainForm();
        mainForm.setSemester(sugang.getSemester());
        mainForm.setSubjectName(sugang.getSubjectName());
        mainForm.setCredit(sugang.getCredit());
        mainForm.setGrade(sugang.getGrade());
        mainForm.setSubjectType(sugang.getSubjectType());
        mainForm.setCulture(sugang.getCulture());
        
        model.addAttribute("sugang", sugang);
        model.addAttribute("mainForm", mainForm); // mainForm을 모델에 추가
        return "modify_form";
    }
    
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/main/modify/{id}")
    public String questionModify(@Valid MainForm mainForm, BindingResult bindingResult, 
            Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "main_form";
        }
        Sugang sugang = this.sugangService.getSugang(id);
        if (!sugang.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        
        this.sugangService.modify(sugang, mainForm.getSemester(), mainForm.getSubjectName(), mainForm.getCredit(), mainForm.getGrade(), mainForm.getSubjectType(), mainForm.getCulture());
        return "redirect:/main/sugang";
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/main/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Sugang sugang = this.sugangService.getSugang(id);
        if (!sugang.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.sugangService.delete(sugang);
        return "redirect:/main/sugang";
    }
    
    
}
