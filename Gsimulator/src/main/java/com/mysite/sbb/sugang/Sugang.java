package com.mysite.sbb.sugang;

import com.mysite.sbb.user.SiteUser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Sugang {
	// SiteUser 엔티티와의 관계를 나타냅니다.
	@ManyToOne
    private SiteUser author;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String semester; // 학년학기
    private String subjectName; // 수강과목명
    private Integer credit; // 학점  
    private String grade; // 성적
    private String subjectType; // 교양, 전공, 일반, 부전공
    private String culture; // 선택 영역
}
