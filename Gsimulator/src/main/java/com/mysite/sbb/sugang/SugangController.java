package com.mysite.sbb.sugang;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/sugang")
public class SugangController {
	private final SugangService sugangService;
	private final UserRepository userRepository;
	private static final Logger logger = LoggerFactory.getLogger(SugangController.class);

	@GetMapping("/send")
	public String login(RedirectAttributes redirectAttributes) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    if (principal instanceof UserDetails) {
	    	return "flask_form";
	    } else {
	        return "redirect:/user/login";
	    }
	}
	
	@GetMapping("/closed")
	public String closed() {
		return "server_closed";
	}
	
	@PostMapping("/send")
	public String sendToFlask(RedirectAttributes redirectAttributes) {
	    String urlStr = "http://localhost:7001/get";
	    // 현재 인증된 사용자의 UserDetails 가져오기
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    String username = null;
	    if (principal instanceof UserDetails) {
	        username = ((UserDetails)principal).getUsername();
	    } else {
	        return "redirect:/user/login";
	    }

	    // UserRepository를 통해 SiteUser 객체 조회
	    Optional<SiteUser> siteUserOpt = userRepository.findByusername(username);
	    if (siteUserOpt.isEmpty()) {
	        return "redirect:/user/login";
	    }
	    SiteUser siteUser = siteUserOpt.get();

	    // 인증 정보를 포함하는 맵 생성
	    Map<String, String> authInfo = new HashMap<>();
	    authInfo.put("id", username);

	    // RestTemplate을 사용하여 요청을 보냄
	    RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<Map<String, String>> entity = new HttpEntity<>(authInfo, headers);
	    
	    try {
	        ResponseEntity<String> response = restTemplate.exchange(urlStr, HttpMethod.POST, entity, String.class);
	        if (response.getStatusCode() == HttpStatus.OK) {
	        	logger.info("Response from Flask: {}", response.getBody());
	        	ObjectMapper objectMapper = new ObjectMapper();
	        	TypeReference<HashMap<Integer, Sugang>> typeRef = new TypeReference<>() {};
	        	Map<Integer, Sugang> sugangMap = objectMapper.readValue(response.getBody(), typeRef);

	        	for (Sugang sugang : sugangMap.values()) {
	        		sugang.setAuthor(siteUser); // Sugang 객체에 사용자 정보 설정
	        		sugangService.saveIfSubjectNameAndSemesterNotExist(sugang);
	        	}
	        } else {
	            // 에러 처리 로직...
	            redirectAttributes.addFlashAttribute("errorMessage", "로그인 실패: " + response.getBody());
	            return "redirect:/sugang/closed";
	        }
	        return "redirect:/";

	    } catch (Exception e) {
	        return "redirect:/sugang/closed";
	    }
	}
}

