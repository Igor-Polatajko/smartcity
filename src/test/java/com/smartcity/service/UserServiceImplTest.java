package com.smartcity.service;

import com.smartcity.dao.RoleDao;
import com.smartcity.dao.UserDao;
import com.smartcity.domain.Role;
import com.smartcity.domain.User;
import com.smartcity.dto.UserDto;
import com.smartcity.mapperDto.UserDtoMapper;
import name.falgout.jeffrey.testing.junit.mockito.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDtoMapper userDtoMapper;

    private UserDto userDto;

    private User user;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userDtoMapper = new UserDtoMapper();
        userService = new UserServiceImpl(userDao, userDtoMapper, roleDao);
        userDto = new UserDto();
        userDto.setName("User");
        userDto.setSurname("Test");
        userDto.setEmail("example@gmail.com");
        user = userDtoMapper.convertUserDtoIntoUser(userDto);
    }

    @Test
    void create_successFlow() {
        Mockito.when(userDao.create(user)).then(invocationOnMock -> {
            User user = invocationOnMock.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserDto resultUserDto = userService.create(userDto);
        // Checking if the id was generated
        assertNotNull(resultUserDto.getId());
        // Checking if the user is not active
        assertFalse(resultUserDto.isActive());
    }

    @Test
    void find_successFlow() {
        Mockito.when(userDao.findById(1L)).thenReturn(user);
        UserDto resultUserDto = userService.findById(1L);
        // Checking if the correct user was returned
        assertThat(userDto).isEqualToIgnoringGivenFields(resultUserDto, "password");
    }

    @Test
    void getAll_successFlow() {
        // Initializing users list
        List<User> users = new ArrayList<>();

        User user1 = new User();
        user1.setEmail("some@email.com");
        user1.setPassword("qwerty");
        user1.setSurname("Test");
        user1.setName("User");
        user1.setPhoneNumber("06558818");

        User user2 = new User();
        user2.setEmail("another@email.com");
        user2.setPassword("trewq");
        user2.setSurname("tset");
        user2.setName("Resu");
        user2.setPhoneNumber("05811451");

        users.add(user1);
        users.add(user2);

        Mockito.when(userDao.findAll()).thenReturn(users);

        List<UserDto> resultUserList = userService.getAll();

        for (int i = 0; i < users.size(); i++) {
            assertThat(users.get(i)).isEqualToIgnoringGivenFields(
                    userDtoMapper.convertUserDtoIntoUser(resultUserList.get(i)),
                    "id", "createdDate", "updatedDate", "password");
        }

    }

    @Test
    void findByEmail_successFlow() {
        Mockito.when(userDao.findByEmail(userDto.getEmail())).thenReturn(user);
        UserDto resultUserDto = userService.findByEmail(userDto.getEmail());
        // Checking if the correct user was returned
        assertThat(userDto).isEqualToIgnoringGivenFields(resultUserDto, "password");
    }

    @Test
    void update_successFlow() {
        userDto.setName("AnotherUser");
        User updatedUser = userDtoMapper.convertUserDtoIntoUser(userDto);
        Mockito.when(userDao.update(updatedUser)).then(
                invocationOnMock -> invocationOnMock.getArgument(0));
        UserDto resultUserDto = userService.update(userDto);
        // Checking if the correct user was returned
        assertThat(userDto).isEqualToIgnoringGivenFields(resultUserDto, "password");
    }

    @Test
    void delete_successFlow() {
        Mockito.when(userDao.delete(1L)).then(invocationOnMock -> {
            userDto.setActive(false);
            return true;
        });
        boolean result = userService.delete(1L);
        // Checking if true was returned
        assertTrue(result);
        // Checking if the user is not active
        assertFalse(userDto.isActive());
    }

    @Test
    void updatePassword_successFlow() {
        Mockito.when(userDao.updatePassword(1L, "qwerty"))
                .then(invocationOnMock -> true);
        assertTrue(userService.updatePassword(1L, "qwerty"));
    }

    @Test
    void getRoles_successFlow() {

        // Initializing list of roles
        List<Role> roles = new ArrayList<>();

        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ADMIN");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("SUPERVISOR");

        roles.add(role1);
        roles.add(role2);

        // Mocking method
        Mockito.when(roleDao.getRolesByUserId(user.getId()))
                .then(invocationOnMock -> roles);

        // Testing
        List<Role> result = userService.getRoles(user.getId());

        for (int i = 0; i < roles.size(); i++) {
            assertEquals(roles.get(i), result.get(i));
        }
    }

    @Test
    void setRoles_successFlow() {

        // Initializing lists
        List<Role> existingRoles;
        List<Role> currentRoles;
        List<Long> newRolesIds;

        Role adminRole = new Role();
        adminRole.setId(1L);

        Role userRole = new Role();
        userRole.setId(2L);

        Role supervisorRole = new Role();
        supervisorRole.setId(3L);

        Role nonExistentRole = new Role();
        nonExistentRole.setId(4L);

        existingRoles = Stream.of(adminRole, userRole, supervisorRole).collect(Collectors.toList());
        currentRoles = Stream.of(adminRole, userRole).collect(Collectors.toList());
        newRolesIds = Stream.of(adminRole, supervisorRole, nonExistentRole)
                .flatMap(r -> Stream.of(r.getId())).collect(Collectors.toList());

        // Expected methods call numbers
        final int expectedAddRoleToUserCallsNumber = 1;
        final int expectedRemoveRoleFromUserCallsNumber = 1;


        // Setting up of methods mocks
        Mockito.when(roleDao.findAll()).thenReturn(existingRoles);
        Mockito.when(roleDao.getRolesByUserId(userDto.getId())).thenReturn(currentRoles);


        // Testing
        userService.setRoles(userDto.getId(), newRolesIds);


        Mockito.verify((roleDao), Mockito.times(expectedAddRoleToUserCallsNumber))
                .addRoleToUser(Mockito.any(), Mockito.any());


        Mockito.verify((roleDao), Mockito.times(expectedRemoveRoleFromUserCallsNumber))
                .removeRoleFromUser(Mockito.any(), Mockito.any());


    }

}