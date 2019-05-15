package com.smartcity.service;

import com.smartcity.dao.UserDaoImpl;
import com.smartcity.domain.User;
import com.smartcity.dto.UserDto;
import com.smartcity.mapperDto.UserDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserDtoMapper userDtoMapper;
    private UserDaoImpl userDao;

    @Autowired
    public UserServiceImpl(UserDaoImpl userDao, UserDtoMapper userDtoMapper) {
        this.userDao = userDao;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = userDtoMapper.convertUserDtoIntoUser(userDto);
        return userDtoMapper.convertUserIntoUserDto(userDao.create(user));
    }

    @Override
    public UserDto get(Long id) {
        return userDtoMapper.convertUserIntoUserDto(userDao.get(id));
    }

    @Override
    public UserDto findByEmail(String email) {
        return userDtoMapper.convertUserIntoUserDto(userDao.findByEmail(email));
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = userDtoMapper.convertUserDtoIntoUser(userDto);
        return userDtoMapper.convertUserIntoUserDto(userDao.update(user));
    }

    @Override
    public boolean delete(Long id) {
        return userDao.delete(id);
    }
}
