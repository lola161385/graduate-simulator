package com.mysite.sbb.main;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mysite.sbb.sugang.Sugang;
import com.mysite.sbb.sugang.SugangRepository;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;

@Service
public class ShowService {
    private final SugangRepository sugangRepository;
    private final UserRepository userRepository;

    public ShowService(SugangRepository sugangRepository, UserRepository userRepository) {
        this.sugangRepository = sugangRepository;
        this.userRepository = userRepository;
    }

    public List<Sugang> getSugangsForCurrentUser() {
        SiteUser currentUser = getCurrentUser();
        // 학기 기준으로 오름차순 정렬된 모든 수강 정보 반환
        return sugangRepository.findByAuthorOrderBySemesterAsc(currentUser);
    }

    
    public List<Sugang> getSugangsForCurrentUserAndSemester(String semester) {
        SiteUser currentUser = getCurrentUser();
        return sugangRepository.findByAuthorAndSemester(currentUser, semester);
    }

    private SiteUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return userRepository.findByusername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
    

	public Sugang getSugangsForCurrentUser(Integer id) {
		return sugangRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid sugang Id:" + id));
	}

	public void modify(Sugang sugang, String semester, String subjectName, String credit, String grade,
			String subjectType, String culture) {
		// TODO 자동 생성된 메소드 스텁
		
	}
	
	
}
