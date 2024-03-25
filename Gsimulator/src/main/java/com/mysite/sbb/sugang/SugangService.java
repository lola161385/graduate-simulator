package com.mysite.sbb.sugang;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
