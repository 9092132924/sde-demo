package com.sde.api.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sde.dto.SignUpRequest;
import com.sde.model.User;
import com.sde.service.UserService;

@Service
public class UserTestDataFactory {

    @Autowired
    private UserService userService;

    public User createUser(String username,
                               String fullName,
                               String password) {
        SignUpRequest createRequest = new SignUpRequest(username, fullName, password, password);
     
   

        User userView = userService.registerNewUser(createRequest);

        assertNotNull(userView.getId(), "User id must not be null!");
        assertEquals(fullName, userView.getName(), "User name update isn't applied!");

        return userView;
    }

    public User createUser(String username,
                               String fullName) {
        return createUser(username, fullName, "Test12345_");
    }


}
