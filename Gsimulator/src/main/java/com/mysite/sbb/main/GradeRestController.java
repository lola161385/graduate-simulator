package com.mysite.sbb.main;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import java.util.Map;

@RestController
public class GradeRestController {

    private final ShowService showService;
    private final UserRepository userRepository;

    public GradeRestController(ShowService showService, UserRepository userRepository) {
        this.showService = showService;
        this.userRepository = userRepository;
    }
    // 전체 평균 api
    @GetMapping("/api/grades/average")
    public ResponseEntity<Map<String, Double>> getAverageGrades() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SiteUser currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // getAverageGradesPerSemesterSortedIncludingSummer 메소드를 사용하여 정렬된 데이터를 반환
        Map<String, Double> averageGrades = showService.getAverageGradesPerSemesterSortedIncludingSummer(currentUser);
        return ResponseEntity.ok(averageGrades);
    }
    
    // 전공평균 api
    @GetMapping("/api/grades/major/average")
    public ResponseEntity<Map<String, Double>> getAverageMajorGrades() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SiteUser currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Map<String, Double> averageGrades = showService.getAverageMajorGradesPerSemesterSortedIncludingSummer(currentUser);
        return ResponseEntity.ok(averageGrades);
    }

}
