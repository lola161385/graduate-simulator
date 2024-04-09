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

    public void modify(Sugang sugang, String semester, String subjectName, Integer credit, String grade, String subjectType, String culture) {
        // Method implementation needed
    }

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
                .filter(sugang -> "교양필수".equals(sugang.getSubjectType()))
                .mapToInt(Sugang::getCredit)
                .sum();
    }
    public Integer getCultureCredit(SiteUser user) {
        return sugangRepository.findByAuthor(user)
                .stream()
                .filter(sugang -> "교양선택".equals(sugang.getSubjectType()))
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
                .filter(sugang -> "전공필수".equals(sugang.getSubjectType()))
                .mapToInt(Sugang::getCredit)
                .sum();
    }
    
    public Integer getMajorCredit(SiteUser user) {
        return sugangRepository.findByAuthor(user)
                .stream()
                .filter(sugang -> "전공선택".equals(sugang.getSubjectType()))
                .mapToInt(Sugang::getCredit)
                .sum();
    }
    
    public Integer getNormalCredit(SiteUser user) {
        return sugangRepository.findByAuthor(user)
                .stream()
                .filter(sugang -> "일반선택".equals(sugang.getSubjectType()))
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

    public Map<String, Double> getAverageMajorGradesPerSemesterSortedIncludingSummer(SiteUser user) {
        Map<String, Double> averageGrades = new HashMap<>();
        List<Sugang> sugangs = sugangRepository.findByAuthor(user).stream()
                .filter(sugang -> sugang.getSubjectType().contains("전공"))
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

        return averageGrades.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    String[] parts1 = o1.split("학년 |학기");
                    String[] parts2 = o2.split("학년 |학기");

                    int yearComparison = Integer.compare(Integer.parseInt(parts1[0]), Integer.parseInt(parts2[0]));
                    if (yearComparison != 0) {
                        return yearComparison;
                    }

                    int semester1 = parseSemester(parts1[1]);
                    int semester2 = parseSemester(parts2[1]);
                    return Integer.compare(semester1, semester2);
                }
            }))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new));
    }

    private int parseSemester(String semester) {
        if (semester.contains("계절")) {
            if (semester.startsWith("1학기")) {
                return 3;  // "1학기 계절"은 3으로 매핑
            } else if (semester.startsWith("2학기")) {
                return 4;  // "2학기 계절"은 4로 매핑
            }
        }
        return Integer.parseInt(semester.replaceAll("[^0-9]", ""));  // "1학기", "2학기" 등은 숫자로 직접 변환
    }

    private double gradeToPoint(String grade) {
        switch (grade) {
            case "A+": return 4.5;
            case "A": return 4.0;
            case "B+": return 3.5;
            case "B": return 3.0;
            case "C+": return 2.5;
            case "C": return 2.0;
            case "D+": return 1.5;
            case "D": return 1.0;
            case "F":  return 0.0;
            default:   return 0.0; // 'P' or 'NP' or any other grade not included in the grading scale
        }
    }
}
