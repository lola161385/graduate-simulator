package com.mysite.sbb.main;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Checkcs {
	//requirements.js 의 값과 데이터값이 일치해야함
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String year; // 입학년도 분류
    private String cs1;
    private String cs2; 
    private String cs3;
    private String cs4;
    private String cs5;
    private String cs6; 
}
