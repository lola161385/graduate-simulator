package com.mysite.sbb.user;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public SiteUser create(String username, String email, String password, String enteryear, String usergroup, String major) {
		SiteUser user = new SiteUser();
		user.setUsername(username);
		user.setEmail(email);
		user.setEnteryear(enteryear);
		user.setPassword(passwordEncoder.encode(password));
		user.setUsergroup(usergroup);
		user.setMajor(major);
		this.userRepository.save(user);
		return user;
	}

	public SiteUser getUser(String username) {
		Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
		if (siteUser.isPresent()) {
			return siteUser.get();
		} else {
			throw new DataNotFoundException("siteuser not found");
		}
	}
	
	public SiteUser getUserInfo(String username) {
	    return userRepository.findByUsername(username)
	        .orElseThrow(() -> new DataNotFoundException("User not found"));
	}
	
	public void updateUser(String uname, String email, String enteryear, String usergroup, String major) {
	    SiteUser user = userRepository.findByUsername(uname)
	        .orElseThrow(() -> new DataNotFoundException("User not found"));
	    user.setEmail(email);
	    user.setEnteryear(enteryear);
	    user.setUsergroup(usergroup);
	    user.setMajor(major);
	    userRepository.save(user); // 변경된 정보를 저장
	}

}
