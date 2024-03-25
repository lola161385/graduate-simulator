package com.mysite.sbb.main;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.sbb.sugang.Sugang;
import com.mysite.sbb.user.SiteUser;


public interface MainRepository extends JpaRepository<Sugang, Integer> {
	List<Sugang> findByAuthorAndSemester(SiteUser author, String semester);
}
