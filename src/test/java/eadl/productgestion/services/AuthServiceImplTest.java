package eadl.productgestion.services;

import eadl.productgestion.dto.request.LoginRequestDTO;
import eadl.productgestion.dto.request.RegisterRequestDTO;
import eadl.productgestion.dto.response.AuthResponse;
import eadl.productgestion.dto.response.UserResponse;
import eadl.productgestion.security.JwtService;
import eadl.productgestion.security.TokenBlacklistService;
import eadl.productgestion.services.impl.AuthServiceImpl;
import eadl.productgestion.utils.UserAccountStatus;
import eadl.productgestion.utils.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        testUserResponse = new UserResponse(
                1L,
                "Admin User",
                "admin@example.com",
                UserRole.ADMIN,
                UserAccountStatus.ACTIVE
        );
    }

    @Test
    @DisplayName("Connexion valide : retourne AuthResponse avec jeton")
    void login_whenValidCredentials_returnsAuthResponse() {
        LoginRequestDTO request = new LoginRequestDTO("admin@example.com", "Password123!");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@example.com");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateAccessToken("admin@example.com")).thenReturn("jwt-token-xyz");
        when(userService.findByEmail("admin@example.com")).thenReturn(testUserResponse);

        AuthResponse actual = authService.login(request);

        assertThat(actual).isNotNull();
        assertThat(actual.accessToken()).isEqualTo("jwt-token-xyz");
        assertThat(actual.tokenType()).isEqualTo(JwtService.TOKEN_TYPE);
        assertThat(actual.user()).isEqualTo(testUserResponse);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Connexion invalide : lève BadCredentialsException")
    void login_whenInvalidCredentials_throwsException() {
        LoginRequestDTO request = new LoginRequestDTO("admin@example.com", "wrong-password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Inscription valide : crée l'utilisateur et retourne AuthResponse")
    void register_whenValidRequest_returnsAuthResponse() {
        RegisterRequestDTO request = new RegisterRequestDTO("Admin User", "admin@example.com", "Password123!");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userService.create(request)).thenReturn(testUserResponse);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateAccessToken("admin@example.com")).thenReturn("jwt-token-xyz");

        AuthResponse actual = authService.register(request);

        assertThat(actual).isNotNull();
        assertThat(actual.accessToken()).isEqualTo("jwt-token-xyz");
        assertThat(actual.user()).isEqualTo(testUserResponse);
        verify(userService).create(request);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Déconnexion : révoque le jeton")
    void logout_whenTokenProvided_revokesToken() {
        String token = "valid-token-to-revoke";
        Date expiration = new Date(System.currentTimeMillis() + 60000);
        when(jwtService.getExpirationFromToken(token)).thenReturn(expiration);

        authService.logout(token);

        verify(tokenBlacklistService).revoke(token, expiration);
    }
}
