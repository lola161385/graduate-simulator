package com.mysite.sbb.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {
	@Size(min = 10, max = 10)
	@NotEmpty(message = "사용자ID는 필수항목입니다.")
	private String username;

	@NotEmpty(message = "비밀번호는 필수항목입니다.")
	private String password1;

	@NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
	private String password2;
	
	@NotEmpty(message = "입학년도는 필수항목입니다.")
	private String enteryear;
	
	@NotEmpty(message = "단과대학은 필수항목입니다.")
	private String usergroup;
	
	@NotEmpty(message = "학부는 필수항목입니다.")
	private String major;
	
	@NotEmpty(message = "이름은 필수항목입니다.")
	private String realname;
	
	@NotEmpty(message = "이메일은 필수항목입니다.")
	@Email
	private String email;
}
