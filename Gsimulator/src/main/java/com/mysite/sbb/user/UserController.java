package com.mysite.sbb.user;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	
	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm) {
		return "signup_form";
	}

	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "signup_form";
		}
		if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
			return "signup_form";
		}
		
		try {
			boolean isValidName = validateRealNameWithFlask(userCreateForm.getUsername(),userCreateForm.getRealname());
			
            if (!isValidName) {
                bindingResult.reject("realname", "이름과 학번이 일치하지 않습니다.");
                return "signup_form";
            }
            
			userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1(), userCreateForm.getEnteryear(), userCreateForm.getUsergroup(), userCreateForm.getMajor());
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			return "signup_form";
		} catch (Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "signup_form";
		}

		return "redirect:/";
	}

	@GetMapping("/login")
	public String login() {
		return "login_form";
	}
	
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        String username = principal.getName(); // 현재 로그인된 사용자의 이름 가져오기
        SiteUser user = userService.getUserInfo(username); // 사용자 정보 조회
        
        UserForm userForm = new UserForm();
        userForm.setUsername(user.getUsername());
        userForm.setEmail(user.getEmail());
        userForm.setEnteryear(user.getEnteryear());
        userForm.setUsergroup(user.getUsergroup());
        userForm.setMajor(user.getMajor());
        
        model.addAttribute("user", user); // 모델에 사용자 정보 추가
        model.addAttribute("userForm", userForm);
        return "profile_form"; // 사용자 프로필 페이지 반환
    }
    
    @PostMapping("/profile/modify")
    public String modifyProfile(@Valid UserForm userForm, BindingResult bindingResult, Principal principal, Model model) {
        if (bindingResult.hasErrors()) {
            return "profile_form"; // 유효성 검사 실패 시, 프로필 수정 폼으로 리다이렉트
        }

        String uname = principal.getName(); // 현재 로그인된 사용자의 이름 가져오기
        try {
            userService.updateUser(uname , userForm.getEmail(), userForm.getEnteryear(), userForm.getUsergroup(), userForm.getMajor());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "정보 수정에 실패하였습니다.");
            return "profile_form";
        }

        return "redirect:/user/profile"; // 성공적으로 수정되면 프로필 페이지로 리다이렉트
    }
    
    private boolean validateRealNameWithFlask(String username, String realName) {
        String urlStr = "http://localhost:7001/get";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> request = new HashMap<>();
        request.put("realName", realName);
        request.put("userName", username);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(urlStr, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return true;
        }
        return false;
    }

}
