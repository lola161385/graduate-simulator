package com.mysite.sbb.sugang;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.sbb.user.SiteUser;

public interface SugangRepository extends JpaRepository<Sugang, Integer>{
	List<Sugang> findByAuthor(SiteUser author);
	List<Sugang> findByAuthorAndSemester(SiteUser author, String semester);
	List<Sugang> findByAuthorOrderBySemesterAsc(SiteUser author);
}
