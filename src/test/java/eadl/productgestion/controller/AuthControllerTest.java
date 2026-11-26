package eadl.productgestion.controller;

import eadl.productgestion.dto.request.LoginRequestDTO;
import eadl.productgestion.dto.request.RegisterRequestDTO;
import eadl.productgestion.dto.response.AuthResponse;
import eadl.productgestion.dto.response.UserResponse;
import eadl.productgestion.exception.GlobalExceptionHandler;
import eadl.productgestion.security.JwtService;
import eadl.productgestion.services.AuthService;
import eadl.productgestion.utils.UserAccountStatus;
import eadl.productgestion.utils.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/login - Succès (200 OK)")
    void login_whenValid_returns200() throws Exception {
        UserResponse userResponse = new UserResponse(1L, "Admin", "admin@example.com", UserRole.ADMIN, UserAccountStatus.ACTIVE);
        AuthResponse response = new AuthResponse("token-abc", JwtService.TOKEN_TYPE, userResponse);

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userEmail": "admin@example.com",
                                    "userPassword": "Password123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token-abc"))
                .andExpect(jsonPath("$.tokenType").value(JwtService.TOKEN_TYPE));
    }

    @Test
    @DisplayName("POST /api/auth/login - Données invalides (400 Bad Request)")
    void login_whenInvalidBody_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userEmail": "invalid-email",
                                    "userPassword": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Succès (201 Created)")
    void register_whenValid_returns201() throws Exception {
        UserResponse userResponse = new UserResponse(1L, "Admin User", "admin@example.com", UserRole.ADMIN, UserAccountStatus.ACTIVE);
        AuthResponse response = new AuthResponse("token-abc", JwtService.TOKEN_TYPE, userResponse);

        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userName": "Admin User",
                                    "userEmail": "admin@example.com",
                                    "userPassword": "Password123!"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("token-abc"))
                .andExpect(jsonPath("$.user.userName").value("Admin User"));
    }

    @Test
    @DisplayName("POST /api/auth/logout - Succès (204 No Content)")
    void logout_whenValidToken_returns204() throws Exception {
        doNothing().when(authService).logout("sample-token");

        mockMvc.perform(post("/api/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer sample-token"))
                .andExpect(status().isNoContent());

        verify(authService).logout("sample-token");
    }
}
