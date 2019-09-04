package com.smartcity.dao;

import com.smartcity.domain.User;

import java.util.List;

public interface UserDao {

    User create(User user);

    User findById(Long id);

    List<User> findAll(int from, int to);

    User findByEmail(String email);

    List<User> findByOrganizationId(Long organizationId);

    List<User> findByRoleId(Long rolesIds);

    User update(User user);

    boolean delete(Long id);

    boolean updatePassword(Long userId, String newPassword);

    List<User> findUserByCommentId(Long commentId);
}
