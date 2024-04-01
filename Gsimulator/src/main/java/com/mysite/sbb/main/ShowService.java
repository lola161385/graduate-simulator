package com.mysite.sbb.main;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    public Integer getMajorCredits(SiteUser user) {
        return sugangRepository.findByAuthor(user)
                .stream()
                .filter(sugang -> "전공 필수".equals(sugang.getSubjectType()))
                .mapToInt(Sugang::getCredit)
                .sum();
    }
    
    public Integer getMajorCredit(SiteUser user) {
        return sugangRepository.findByAuthor(user)
                .stream()
                .filter(sugang -> "전공 선택".equals(sugang.getSubjectType()))
                .mapToInt(Sugang::getCredit)
                .sum();
    }
    
    public Integer getNormalCredit(SiteUser user) {
        return sugangRepository.findByAuthor(user)
                .stream()
                .filter(sugang -> "일반 선택".equals(sugang.getSubjectType()))
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
    
    //전체 평균 계산 로직
    public Map<String, Double> getAverageGradesPerSemester(SiteUser user) {
        Map<String, Double> averageGrades = new HashMap<>();
        List<Sugang> sugangs = sugangRepository.findByAuthor(user);

        Map<String, List<Sugang>> sugangsBySemester = sugangs.stream()
                .collect(Collectors.groupingBy(Sugang::getSemester));

        sugangsBySemester.forEach((semester, sugangList) -> {
            double totalPoints = 0;
            int totalCredits = 0;
            for (Sugang sugang : sugangList) {
                if (!sugang.getGrade().equals("P") && !sugang.getGrade().equals("NP")) {
                    totalPoints += gradeToPoint(sugang.getGrade()) * sugang.getCredit();
                    totalCredits += sugang.getCredit();
                }
            }
            double averageGrade = totalCredits > 0 ? totalPoints / totalCredits : 0;
            averageGrades.put(semester, averageGrade);
        });

        return averageGrades;
    }

    private double gradeToPoint(String grade) {
        switch (grade) {
            case "A+": return 4.5;
            case "A0": return 4.0;
            case "B+": return 3.5;
            case "B0": return 3.0;
            case "C+": return 2.5;
            case "C0": return 2.0;
            case "D+": return 1.5;
            case "D0": return 1.0;
            case "F":  return 0.0;
            default:   return 0.0; // 'P' or 'NP' or any other grade
        }
    }
    
 // 학기별 평균 학점을 계산하고, 학년 및 학기 순으로 정렬하여 반환하는 메소드
    public Map<String, Double> getAverageGradesPerSemesterSortedIncludingSummer(SiteUser user) {
        Map<String, Double> averageGrades = getAverageGradesPerSemester(user);
    
        // 학기를 "1학년 1학기", "1학년 2학기", "1학년 계절학기", ... 순서로 정렬
        Map<String, Double> sortedAverageGrades = averageGrades.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    String[] parts1 = o1.split("학년 |학기");
                    String[] parts2 = o2.split("학년 |학기");
    
                    int yearComparison = Integer.compare(Integer.parseInt(parts1[0]), Integer.parseInt(parts2[0]));
                    if (yearComparison != 0) {
                        return yearComparison;
                    }
    
                    // "계절학기"를 포함한 학기 비교
                    int semester1 = "계절".equals(parts1[1]) ? 3 : Integer.parseInt(parts1[1]);
                    int semester2 = "계절".equals(parts2[1]) ? 3 : Integer.parseInt(parts2[1]);
                    return Integer.compare(semester1, semester2);
                }
            }))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new // 순서를 유지하는 Map 사용
            ));
    
        return sortedAverageGrades;
    }
    
    //전공평균 계산 로직
    public Map<String, Double> getAverageMajorGradesPerSemesterSortedIncludingSummer(SiteUser user) {
        Map<String, Double> averageGrades = new HashMap<>();
        List<Sugang> sugangs = sugangRepository.findByAuthor(user).stream()
                .filter(sugang -> sugang.getSubjectType().contains("전공")) // 전공 과목만 필터링
                .collect(Collectors.toList());

        Map<String, List<Sugang>> sugangsBySemester = sugangs.stream()
                .collect(Collectors.groupingBy(Sugang::getSemester));

        sugangsBySemester.forEach((semester, sugangList) -> {
            double totalPoints = 0;
            int totalCredits = 0;
            for (Sugang sugang : sugangList) {
                if (!sugang.getGrade().equals("P") && !sugang.getGrade().equals("NP")) {
                    totalPoints += gradeToPoint(sugang.getGrade()) * sugang.getCredit();
                    totalCredits += sugang.getCredit();
                }
            }
            double averageGrade = totalCredits > 0 ? totalPoints / totalCredits : 0;
            averageGrades.put(semester, averageGrade);
        });

        // 학기별 데이터를 정렬
        return averageGrades.entrySet().stream()
        	    .sorted(Map.Entry.<String, Double>comparingByKey((o1, o2) -> {
        	        String[] parts1 = o1.split("학년 |학기");
        	        String[] parts2 = o2.split("학년 |학기");

        	        int yearComparison = Integer.compare(Integer.parseInt(parts1[0]), Integer.parseInt(parts2[0]));
        	        if (yearComparison != 0) {
        	            return yearComparison;
        	        }

        	        // "계절학기"를 포함한 학기 비교
        	        // "계절학기"의 경우 파싱되지 않으므로, 조건문으로 처리
        	        int semester1 = parts1[1].equals("계절") ? 3 : Integer.parseInt(parts1[1]);
        	        int semester2 = parts2[1].equals("계절") ? 3 : Integer.parseInt(parts2[1]);
        	        return Integer.compare(semester1, semester2);
        	    }))
        	    .collect(Collectors.toMap(
        	        Map.Entry::getKey,
        	        Map.Entry::getValue,
        	        (e1, e2) -> e1,
        	        LinkedHashMap::new)); // 순서를 유지하는 Map 사용
    }


}
