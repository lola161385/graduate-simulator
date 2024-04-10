package com.mysite.sbb.user;

import java.util.List;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.sugang.Sugang;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
 
	@Column(unique = true)
	private String username;

	private String password;
	
	private String enteryear;
	
	private String usergroup;
	
	private String major;

	@Column(unique = true)
	private String email;
	
	// 사용자 삭제 시 연결된 질문, 답변, 투표 정보, 수강 정보도 삭제됨
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Question> questions;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Answer> answers;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Sugang> sugangs;
}
