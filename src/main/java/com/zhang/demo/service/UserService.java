package com.zhang.demo.service;

import java.util.List;

import com.zhang.demo.model.User;

public interface UserService {

	User findById(Integer id);

	List<User> findAll();
}
