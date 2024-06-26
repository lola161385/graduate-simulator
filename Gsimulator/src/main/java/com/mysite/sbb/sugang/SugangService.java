package com.mysite.sbb.sugang;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.user.SiteUser;

@Service
public class SugangService {
	private final SugangRepository sugangRepository;
    
    @Autowired
    public SugangService(SugangRepository sugangRepository) {
        this.sugangRepository = sugangRepository;
    }
    
    // 저장
    public Sugang saveSugang(Sugang sugang) {
        return sugangRepository.save(sugang);
    }
    // 삭제
    public void delete(Sugang sugang) {
        this.sugangRepository.delete(sugang);
    }

	public List<Sugang> findByAuthor(SiteUser siteUser) {
		// TODO 자동 생성된 메소드 스텁
		return null;
	}
	public List<Sugang> findByAuthorAndSemester(SiteUser siteUser, String semester) {
		// TODO 자동 생성된 메소드 스텁
		return null;
	}

	public Sugang getSugang(Integer id) {
		Optional<Sugang> sugang = this.sugangRepository.findById(id);
		if (sugang.isPresent()) {
			return sugang.get();
		} else {
			throw new DataNotFoundException("question not found");
		}
	}
	
	// 수강정보 수정
	public void modify(Sugang sugang, String smester, String subjectName, Integer credit, String grade, String subjectType, String culture) {
        sugang.setSemester(smester); 
        sugang.setSubjectName(subjectName);
        sugang.setCredit(credit);
        sugang.setGrade(grade);
        sugang.setSubjectType(subjectType);
        sugang.setCulture(culture);
        sugangRepository.save(sugang);
    }
	
	// 에브리타임에서 정보를 받아올때 중복 값 필터링
	public Optional<Sugang> saveIfSubjectNameAndSemesterNotExist(Sugang sugang) {
        Optional<Sugang> existingSugang = sugangRepository.findByAuthorAndSubjectNameAndSemester(sugang.getAuthor(), sugang.getSubjectName(), sugang.getSemester());
        if (existingSugang.isEmpty()) {
            return Optional.of(sugangRepository.save(sugang));
        } else {
            // 이미 존재하는 경우, 저장하지 않음
            return Optional.empty();
        }
    }
	
}
