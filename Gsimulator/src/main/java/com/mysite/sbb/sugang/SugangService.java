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
    
    public Sugang saveSugang(Sugang sugang) {
        return sugangRepository.save(sugang);
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
	public void modify(Sugang sugang, String smester, String subjectName, String credit, String grade, String subjectType, String culture) {
        sugang.setSemester(smester); 
        sugang.setSubjectName(subjectName);
        sugang.setCredit(credit);
        sugang.setGrade(grade);
        sugang.setSubjectType(subjectType);
        sugang.setCulture(culture);
        sugangRepository.save(sugang);
    }

}
