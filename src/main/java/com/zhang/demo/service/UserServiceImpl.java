package com.zhang.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zhang.demo.model.User;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Override
	public User findById(Integer id) {

		return null;
	}

	@Override
	public List<User> findAll() {
		return null;
	}

}
