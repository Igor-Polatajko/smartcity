package com.smartcity.service;

import com.smartcity.dao.RoleDao;
import com.smartcity.dao.UserDao;
import com.smartcity.dao.UserOrganizationDao;
import com.smartcity.domain.Role;
import com.smartcity.domain.User;
import com.smartcity.dto.RoleDto;
import com.smartcity.dto.UserDto;
import com.smartcity.mapperDto.RoleDtoMapper;
import com.smartcity.mapperDto.UserDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private UserDtoMapper userDtoMapper;
    private RoleDtoMapper roleDtoMapper;
    private UserDao userDao;
    private UserOrganizationDao userOrgDao;
    private RoleDao roleDao;

    // Visible for testing
    static final int PAGINATION_PAGE_SIZE = 10;

    @Autowired
    public UserServiceImpl(UserDao userDao, UserOrganizationDao userOrgDao, UserDtoMapper userDtoMapper, RoleDao roleDao, RoleDtoMapper roleDtoMapper) {
        this.userDao = userDao;
        this.userOrgDao = userOrgDao;
        this.userDtoMapper = userDtoMapper;
        this.roleDao = roleDao;
        this.roleDtoMapper = roleDtoMapper;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = userDtoMapper.convertUserDtoIntoUser(userDto);

        // Setting user activity status as "true"
        user.setActive(true);

        return userDtoMapper.convertUserIntoUserDto(userDao.create(user));
    }

    @Override
    public UserDto findById(Long id) {
        return userDtoMapper.convertUserIntoUserDto(userDao.findById(id));
    }

    @Override
    public List<UserDto> findAll(int pageId) {
        if (pageId < 1) {
            throw new IllegalArgumentException("Page id should be greater than 0");
        }

        int from = (pageId - 1) * PAGINATION_PAGE_SIZE;
        int to = from + PAGINATION_PAGE_SIZE;

        List<User> users = userDao.findAll(from, to);
        return this.convertUsersListToUsersDtosList(users);
    }

    @Override
    public UserDto findByEmail(String email) {
        return userDtoMapper.convertUserIntoUserDto(userDao.findByEmail(email));
    }

    @Override
    public UserDto findByUsersOrganizationsId(Long usersOrgId) {
        return userDtoMapper.convertUserIntoUserDto(userDao.findById(userOrgDao.findUserIdById(usersOrgId)));
    }

    @Override
    public UserDto update(UserDto userDto) {
        User updatedUser = userDtoMapper.convertUserDtoIntoUser(userDto);

        // Because, we don't allow to change activity state through this method
        User userFromDb = userDao.findById(userDto.getId());
        updatedUser.setActive(userFromDb.isActive());

        return userDtoMapper.convertUserIntoUserDto(userDao.update(updatedUser));
    }

    @Override
    public List<UserDto> findByOrganizationId(Long organizationId) {
        List<User> users = userDao.findByOrganizationId(organizationId);
        return this.convertUsersListToUsersDtosList(users);
    }

    @Override
    public List<UserDto> findByRoleId(Long roleId) {
        List<User> users = userDao.findByRoleId(roleId);
        return this.convertUsersListToUsersDtosList(users);
    }

    @Override
    public boolean delete(Long id) {
        return userDao.delete(id);
    }

    @Override
    public boolean activate(Long id) {
        User user = userDao.findById(id);
        user.setActive(true);
        userDao.update(user);
        return true;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByEmail(username);
        List<Role> rolesByUserId = roleDao.getRolesByUserId(user.getId());
        user.setAuthorities(rolesByUserId);
        return user;
    }

    @Override
    public boolean updatePassword(Long userId, String newPassword) {
        return userDao.updatePassword(userId, newPassword);
    }

    @Override
    public List<RoleDto> getRoles(Long id) {
        return roleDao.getRolesByUserId(id)
                .stream().map(roleDtoMapper::roleToRoleDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean setRoles(Long userId, List<Long> newRolesIds) {
        List<Long> existingRolesIds = roleDao.findAll()
                .stream().flatMap(r -> Stream.of(r.getId())).collect(Collectors.toList());

        List<Long> currentRolesIds = roleDao.getRolesByUserId(userId)
                .stream().flatMap(r -> Stream.of(r.getId())).collect(Collectors.toList());

        // Adding new roles
        for (Long roleId : newRolesIds) {
            if (!currentRolesIds.contains(roleId) && existingRolesIds.contains(roleId)) {
                roleDao.addRoleToUser(userId, roleId);
            }
        }

        // Removing non-actual roles
        for (Long roleId : currentRolesIds) {
            if (!newRolesIds.contains(roleId)) {
                roleDao.removeRoleFromUser(userId, roleId);
            }
        }

        return true;
    }

    @Override
    public List<UserDto> findUserByCommentId(Long commentId) {
        List<User> users = userDao.findUserByCommentId(commentId);
        return this.convertUsersListToUsersDtosList(users);
    }

    private List<UserDto> convertUsersListToUsersDtosList(List<User> users) {
        return users.stream().
                map(userDtoMapper::convertUserIntoUserDto)
                .collect(Collectors.toList());
    }
}
