package com.smartcity.dao;

import com.smartcity.domain.Organization;
import com.smartcity.domain.Role;
import com.smartcity.domain.User;
import com.smartcity.exceptions.DbOperationException;
import com.smartcity.exceptions.NotFoundException;
import com.smartcity.mapper.OrganizationMapper;
import com.smartcity.mapper.RoleMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserDaoImplTest extends BaseTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private CommentDao commentDao;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void beforeEachSetUp() {
        // Initializing test user
        user = new User();
        user.setEmail("example@gmail.com");
        user.setPassword("12345");
        user.setSurname("Johnson");
        user.setName("John");
        user.setPhoneNumber("0626552521415");
        user.setActive(true);

        // Creating user
        userDao.create(user);
    }

    @Test
    void testCreate_successFlow() {
        // Instantiating new user object
        User newUser = new User();
        newUser.setEmail("someAnotherEmail@gmail.com");
        newUser.setPassword("12345");
        newUser.setSurname("Johnson");
        newUser.setName("John");
        newUser.setPhoneNumber("0626552521415");

        // Should return reference to that same object
        assertEquals(newUser, userDao.create(newUser));
        assertThat(newUser.getId()).isNotNull();
    }

    @Test
    void testCreate_duplicateUsername() {
        // A user with this name already exists
        // Should throw DbOperationException
        assertThrows(DbOperationException.class, () -> userDao.create(user));
    }


    @Test
    void testCreate_omittedNotNullFields() {
        // Creating empty user item
        User emptyUserItem = new User();
        assertThrows(DbOperationException.class, () -> userDao.create(emptyUserItem));
    }

    @Test
    void testFind_successFlow() {
        // Getting user
        User resultUser = userDao.findById(user.getId());

        // Encrypting user password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        assertThat(user).isEqualToIgnoringGivenFields(resultUser, "id", "createdDate", "updatedDate");
    }

    @Test
    void testFindUser_invalidId() {
        assertThrows(NotFoundException.class, () -> userDao.findById(Long.MAX_VALUE));
    }

    @Test
    void findAll_successFlow() {
        // Clearing table
        clearTables("Users");

        // Initializing users list
        List<User> users = this.getListOfUsers();

        for (User user : users) {
            // Adding more users to database
            userDao.create(user);

            // Encode passwords
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // First 5 users (should return 2, because there is only 2 in db)
        List<User> resultUserList = userDao.findAll(0, 5);
        for (int i = 0; i < users.size(); i++) {
            assertThat(users.get(i)).isEqualToIgnoringGivenFields(resultUserList.get(i),
                    "id", "createdDate", "updatedDate");
        }

        // Only second user
        List<User> secondUser = userDao.findAll(1, 2);

        assertThat(users.get(1)).isEqualToIgnoringGivenFields(resultUserList.get(1),
                "id", "createdDate", "updatedDate");

    }

    @Test
    void findAll_EmptyUsersTable() {
        clearTables("Users");
        List<User> resultUserList = userDao.findAll(1, 5);

        assertTrue(resultUserList.isEmpty());
    }

    @Test
    void testFindByEmail_successFlow() {
        User resultUser = userDao.findByEmail(user.getEmail());

        // Encrypting user password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        assertThat(user).isEqualToIgnoringGivenFields(resultUser, "id", "createdDate", "updatedDate");
    }

    @Test
    void testFindByEmail_incorrectEmail() {
        assertThrows(NotFoundException.class, () -> userDao.findByEmail("not_existing_email@gmail.com"));
    }

    @Test
    void testFindByOrganizationId_successFlow() {
        clearTables("Users_organizations", "Organizations");


        OrganizationMapper organizationMapper = new OrganizationMapper();

        // Inserting organization to DB
        template.update(
                "INSERT INTO Organizations (name, address, created_date, updated_date)" +
                        "VALUES('org', 'somewhere', '2019-05-05', '2019-05-05')");

        // Getting organizationsFromDb
        List<Organization> organizations = template.query(
                "SELECT * FROM Organizations", organizationMapper);

        if (!organizations.isEmpty()) {
            // Inserting row to Users_organizations table
            template.update(
                    "INSERT INTO Users_organizations(user_id, organization_id, created_date, updated_date)" +
                            " VALUES (? ,? ,'2019-05-05','2019-05-05');",
                    user.getId(), organizations.get(0).getId());

            List<User> users = userDao.findByOrganizationId(organizations.get(0).getId());

            // Checking if there are any users related to organization
            assertFalse(users.isEmpty());
        }
        else {
            fail();
        }
    }

    @Test
    void testFindByOrganizationId_emptyTable() {
        clearTables("Users_organizations");
        List<User> resultUser = userDao.findByOrganizationId(1L);
        assertTrue(resultUser.isEmpty());
    }

    @Test
    void testFindByOrganizationId_incorrectOrganizationId() {
        List<User> resultUser = userDao.findByOrganizationId(Long.MAX_VALUE);
        assertTrue(resultUser.isEmpty());
    }


    @Test
    void testFindByRoleId_successFlow() {
        clearTables("Users_roles", "Roles");


        RoleMapper roleMapper = new RoleMapper();

        // Inserting role to DB
        template.update(
                "INSERT INTO Roles (name, created_date, updated_date) " +
                        "VALUES ('ROLE_ADMIN', '2019-05-05','2019-05-05')");

        // Getting roleFromDb
        List<Role> roles = template.query(
                "SELECT * FROM Roles", roleMapper);

        if (!roles.isEmpty()) {
            // Inserting row to Users_roles table
            template.update(
                    "INSERT INTO Users_roles (user_id, role_id, created_date, updated_date) " +
                            "VALUES (? , ?, '2019-05-05','2019-05-05')",
                    user.getId(), roles.get(0).getId());

            List<User> users = userDao.findByOrganizationId(roles.get(0).getId());

            // Checking if there are any users with particular role
            assertFalse(users.isEmpty());
        }
        else {
            fail();
        }

    }

    @Test
    void testFindByRoleId_emptyTable() {
        clearTables("Users_roles");
        List<User> resultUser = userDao.findByRoleId(1L);
        assertTrue(resultUser.isEmpty());
    }

    @Test
    void testFindByRoleId_incorrectOrganizationId() {
        List<User> resultUser = userDao.findByRoleId(Long.MAX_VALUE);
        assertTrue(resultUser.isEmpty());
    }

    @Test
    void testUpdate_successFlow() {
        // Creating updated user
        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setEmail("updated@gmail.com");
        updatedUser.setSurname("Smith");
        updatedUser.setName("Den");
        updatedUser.setPhoneNumber("0333333333");
        updatedUser.setCreatedDate(user.getCreatedDate());
        userDao.update(updatedUser);
        User resultUser = userDao.findById(user.getId());
        assertThat(updatedUser).isEqualToIgnoringGivenFields(resultUser,
                "createdDate", "updatedDate", "password");
    }

    @Test
    void testUpdate_invalidId() {
        user.setId(Long.MAX_VALUE);
        assertThrows(NotFoundException.class, () -> userDao.update(user));
    }

    @Test
    void testUpdate_omittedNotNullFieldsExceptId() {
        // Creating empty user item
        User someUser = new User();

        // Setting id of an existing user
        someUser.setId(user.getId());

        assertThrows(DbOperationException.class, () -> userDao.update(someUser));
    }

    @Test
    void testDelete_successFlow() {
        // Deleting user from db
        assertTrue(userDao.delete(user.getId()));
    }

    @Test
    void testDelete_invalidId() {
        // Deleting not existing user from db
        assertThrows(NotFoundException.class, () -> userDao.delete(Long.MAX_VALUE));
    }

    @AfterEach
    void AfterEachTearDown() {
        clearTables("Users");
    }

    private List<User> getListOfUsers() {
        List<User> users = new ArrayList<>();

        User user1 = new User();
        user1.setEmail("some@email.com");
        user1.setPassword("qwerty");
        user1.setSurname("Test");
        user1.setName("User");
        user1.setPhoneNumber("06558818");
        user1.setActive(true);
        User user2 = new User();
        user2.setEmail("another@email.com");
        user2.setPassword("trewq");
        user2.setSurname("tset");
        user2.setName("Resu");
        user2.setPhoneNumber("05811451");
        user2.setActive(false);

        users.add(user1);
        users.add(user2);

        return users;
    }

}
