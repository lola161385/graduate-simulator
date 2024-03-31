package com.mysite.sbb.sugang;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.sbb.user.SiteUser;

public interface SugangRepository extends JpaRepository<Sugang, Integer>{
	List<Sugang> findByAuthor(SiteUser author);
	List<Sugang> findByAuthorAndSemester(SiteUser author, String semester);
	List<Sugang> findByAuthorOrderBySemesterAsc(SiteUser author);
	Optional<Sugang> findByAuthorAndSubjectNameAndSemester(SiteUser author, String subjectName, String semester);
	List<Sugang> findByAuthorAndCulture(SiteUser author, String culture);
}
