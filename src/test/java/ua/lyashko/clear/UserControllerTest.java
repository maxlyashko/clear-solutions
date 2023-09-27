package ua.lyashko.clear;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.lyashko.clear.controller.UserController;
import ua.lyashko.clear.entity.User;
import ua.lyashko.clear.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    public void testCreateUser_ValidUser_Returns201Created() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(new Date());

        Mockito.when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", "/api/users/" + user.getId()));
    }

    @Test
    public void testCreateUser_InvalidUser_Returns400BadRequest() throws Exception {
        User user = new User();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUser_ValidUser_Returns200Ok() throws Exception {
        Long userId = 1L;
        User updatedUser = new User();
        updatedUser.setEmail("updated@example.com");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");
        updatedUser.setBirthDate(new Date());

        Mockito.when(userService.updateUser(eq(userId), any(User.class))).thenReturn(Optional.of(updatedUser));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUser_InvalidUser_Returns400BadRequest() throws Exception {
        Long userId = 1L;
        User updatedUser = new User();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdatePartialUser_ValidUser_Returns200Ok() throws Exception {
        Long userId = 1L;
        User partialUser = new User();
        partialUser.setEmail("updated@example.com");

        Mockito.when(userService.updatePartialUser(eq(userId), any(User.class))).thenReturn(Optional.of(partialUser));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(partialUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUser_ValidUserId_Returns204NoContent() throws Exception {
        Long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testSearchUserByBirthDateRange_ValidDateRange_Returns200Ok() throws Exception {
        Date fromDate = new Date();
        Date toDate = new Date();

        List<User> users = new ArrayList<>();
        Mockito.when(userService.searchUsersByBirthDateRange(eq(fromDate), eq(toDate))).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/search")
                        .param("from", "2023-09-20")
                        .param("to", "2023-09-27"))
                .andExpect(status().isOk());
    }

    private String asJsonString(Object obj) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
