package eadl.productgestion.controller;

import eadl.productgestion.dto.request.UserRequest;
import eadl.productgestion.dto.response.UserResponse;
import eadl.productgestion.exception.DuplicateUserEmailException;
import eadl.productgestion.exception.GlobalExceptionHandler;
import eadl.productgestion.exception.UserNotFoundException;
import eadl.productgestion.services.UserService;
import eadl.productgestion.utils.UserAccountStatus;
import eadl.productgestion.utils.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/v1/users - Succès (201 Created + Location)")
    void create_whenValid_returns201AndLocation() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "Jean Mbarga",
                "jean.mbarga@example.com",
                UserRole.CLIENT,
                UserAccountStatus.ACTIVE
        );

        when(userService.create(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userName": "Jean Mbarga",
                                    "userEmail": "jean.mbarga@example.com",
                                    "userPassword": "P@ssword123",
                                    "userRole": "CLIENT",
                                    "userAccountStatus": "ACTIVE"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/users/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userName").value("Jean Mbarga"))
                .andExpect(jsonPath("$.userEmail").value("jean.mbarga@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/users - Données invalides (400 Bad Request)")
    void create_whenInvalid_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userName": "",
                                    "userEmail": "invalid-email",
                                    "userPassword": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.violations").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/users - Doublon email (409 Conflict)")
    void create_whenDuplicateEmail_returns409() throws Exception {
        when(userService.create(any(UserRequest.class)))
                .thenThrow(new DuplicateUserEmailException("jean.mbarga@example.com"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userName": "Jean Mbarga",
                                    "userEmail": "jean.mbarga@example.com",
                                    "userPassword": "P@ssword123"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"));
    }

    @Test
    @DisplayName("GET /api/v1/users - Liste des utilisateurs (200 OK)")
    void findAll_returns200AndList() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "Jean Mbarga",
                "jean.mbarga@example.com",
                UserRole.CLIENT,
                UserAccountStatus.ACTIVE
        );

        when(userService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userName").value("Jean Mbarga"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - Utilisateur existant (200 OK)")
    void findById_whenExists_returns200() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "Jean Mbarga",
                "jean.mbarga@example.com",
                UserRole.CLIENT,
                UserAccountStatus.ACTIVE
        );

        when(userService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userEmail").value("jean.mbarga@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - Inexistant (404 Not Found)")
    void findById_whenNotFound_returns404() throws Exception {
        when(userService.findById(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/v1/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/{id}/status - Statut modifié (200 OK)")
    void changeStatus_whenValid_returns200() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "Jean Mbarga",
                "jean.mbarga@example.com",
                UserRole.CLIENT,
                UserAccountStatus.BLOCKED
        );

        when(userService.changeStatus(eq(1L), eq(UserAccountStatus.BLOCKED))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/users/1/status")
                        .param("status", "BLOCKED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userAccountStatus").value("BLOCKED"));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} - Suppression valide (204 No Content)")
    void deleteById_whenValid_returns204() throws Exception {
        doNothing().when(userService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }
}
