package com.bank.loanApp.service;

import com.bank.loanApp.dto.UpdateUserRequest;
import com.bank.loanApp.model.User;

public interface UserService {

	public User saveUser(User user);
	
	public User updateUser(Long userId, UpdateUserRequest dto);

	public User getUser(Long id);
}
