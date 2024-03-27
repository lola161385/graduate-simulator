package com.mysite.sbb.main;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainForm {
	private String semester; // 학년학기
    private String subjectName; // 수강과목명
    private String credit; // 학점  
    private String grade; // 성적
    private String subjectType; // 교양, 전공, 일반, 부전공
    private String culture;
}
