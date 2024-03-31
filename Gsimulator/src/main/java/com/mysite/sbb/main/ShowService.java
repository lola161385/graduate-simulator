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
    private final CheckcsRepository checkcsRepository;

    public ShowService(SugangRepository sugangRepository, UserRepository userRepository, CheckcsRepository checkcsRepository) {
        this.sugangRepository = sugangRepository;
        this.userRepository = userRepository;
        this.checkcsRepository = checkcsRepository;
    }
    
    public Checkcs getCheckcsByYear(String year) {
        return checkcsRepository.findByYear(year)
                .orElseThrow(() -> new IllegalArgumentException("Invalid year: " + year));
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

    SiteUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return userRepository.findByusername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
    

	public Sugang getSugangsForCurrentUser(Integer id) {
		return sugangRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid sugang Id:" + id));
	}

	public void modify(Sugang sugang, String semester, String subjectName, Integer credit, String grade,
			String subjectType, String culture) {
		// TODO 자동 생성된 메소드 스텁	
	}
	
	// 사용자의 학점을 계산하는 메소드
    public Integer getTotalCredits(SiteUser user) {
        return sugangRepository.findByAuthor(user)
                .stream()
                .mapToInt(Sugang::getCredit)
                .sum();
    }
    public Integer getCultureTotalCredits(SiteUser user) {
        return sugangRepository.findByAuthor(user)
                .stream()
                .filter(sugang -> sugang.getSubjectType().contains("교양"))
                .mapToInt(Sugang::getCredit)
                .sum();
    }
    public Integer getCultureCredits(SiteUser user) {
        return sugangRepository.findByAuthor(user)
                .stream()
                .filter(sugang -> "교양 필수".equals(sugang.getSubjectType()))
                .mapToInt(Sugang::getCredit)
                .sum();
    }
    public Integer getCultureCredit(SiteUser user) {
        return sugangRepository.findByAuthor(user)
                .stream()
                .filter(sugang -> "교양 선택".equals(sugang.getSubjectType()))
                .mapToInt(Sugang::getCredit)
                .sum();
    }
    public Integer getMajorTotalCredits(SiteUser user) {
        return sugangRepository.findByAuthor(user)
                .stream()
                .filter(sugang -> sugang.getSubjectType().contains("전공"))
                .mapToInt(Sugang::getCredit)
                .sum();
    }
    public long getChapelCount(SiteUser user) {
        return sugangRepository.findByAuthor(user)
                .stream()
                .filter(sugang -> sugang.getSubjectType().contains("채플"))
                .count(); // 갯수를 세서 반환합니다.
    }
    public boolean hasTakenCultureSubject(SiteUser user, String culture) {
        return sugangRepository.findByAuthor(user)
                .stream()
                .anyMatch(sugang -> culture.equals(sugang.getCulture()));     		
    }
    
    public int getSumOfCreditsForCulture(SiteUser user, String culture) {
        return sugangRepository.findByAuthor(user)
                .stream()
                // culture 필드가 null이 아니며, 주어진 culture 값과 일치하는지 확인
                .filter(sugang -> sugang.getCulture() != null && sugang.getCulture().equals(culture))
                .mapToInt(Sugang::getCredit)
                .sum();
    }

}
