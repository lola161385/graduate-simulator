package com.mysite.sbb;

//import java.util.Optional;

import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//import com.mysite.sbb.inform.Sugang;
//import com.mysite.sbb.inform.SugangRepository;
//import com.mysite.sbb.user.SiteUser;
//import com.mysite.sbb.user.UserRepository;

@SpringBootTest
class GsimulatorApplicationTests {
	
	//@Autowired
    //private SugangRepository sugangRepository;
	//@Autowired
	//private UserRepository userRepository;
	
	@Test
	void testJpa() {
		/*
		Optional<SiteUser> os = this.userRepository.findByusername("2020100030");
		Optional<SiteUser> os1 = this.userRepository.findByusername("2020100030");
		if(os.isPresent()) {
			Sugang s1 = new Sugang();
			SiteUser user = os.get();
			s1.setCredit(3);
			s1.setSemester("2학년2학기");
			s1.setSubjectName("객체지향");
			s1.setGrade("A");
			s1.setSubjectType("전공");
			s1.setCulture(null);
			s1.setAuthor(user);
			this.sugangRepository.save(s1);
		}
		if(os1.isPresent()) {
			Sugang s2 = new Sugang();
			SiteUser user1 = os1.get();
			s2.setCredit(3);
			s2.setSemester("1학년1학기");
			s2.setSubjectName("인식테스트");
			s2.setGrade("A+");
			s2.setSubjectType("교양");
			s2.setCulture(null);
			s2.setAuthor(user1);
			this.sugangRepository.save(s2);
		}
		*/
	}
	
}
