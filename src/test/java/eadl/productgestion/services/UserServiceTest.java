package eadl.productgestion.services;

import eadl.productgestion.dto.request.UserRequest;
import eadl.productgestion.dto.response.UserResponse;
import eadl.productgestion.entity.User;
import eadl.productgestion.exception.DuplicateUserEmailException;
import eadl.productgestion.exception.UserNotFoundException;
import eadl.productgestion.mappers.UserMapper;
import eadl.productgestion.repositories.UserRepository;
import eadl.productgestion.services.impl.UserServiceImpl;
import eadl.productgestion.utils.UserAccountStatus;
import eadl.productgestion.utils.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRequest testRequest;
    private UserResponse testResponse;

    @BeforeEach
    void setUp() {
        lenient().when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        testUser = User.builder()
                .id(1L)
                .userName("Jean Mbarga")
                .userEmail("jean.mbarga@example.com")
                .userPassword("P@ssword123")
                .userRole(UserRole.CLIENT)
                .userAccountStatus(UserAccountStatus.ACTIVE)
                .build();

        testRequest = new UserRequest(
                "Jean Mbarga",
                "jean.mbarga@example.com",
                "P@ssword123",
                UserRole.CLIENT,
                UserAccountStatus.ACTIVE
        );

        testResponse = new UserResponse(
                1L,
                "Jean Mbarga",
                "jean.mbarga@example.com",
                UserRole.CLIENT,
                UserAccountStatus.ACTIVE
        );
    }

    @Test
    @DisplayName("Création valide d'un utilisateur")
    void create_whenValid_returnsUserResponse() {
        when(userRepository.existsByUserEmail("jean.mbarga@example.com")).thenReturn(false);
        when(userMapper.toEntity(testRequest)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testResponse);

        UserResponse actual = userService.create(testRequest);

        assertThat(actual).isNotNull();
        assertThat(actual.userEmail()).isEqualTo("jean.mbarga@example.com");
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Création avec email déjà existant")
    void create_whenDuplicateEmail_throwsDuplicateUserEmailException() {
        when(userRepository.existsByUserEmail("jean.mbarga@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(testRequest))
                .isInstanceOf(DuplicateUserEmailException.class)
                .hasMessageContaining("jean.mbarga@example.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Consultation par identifiant existant")
    void findById_whenExists_returnsUserResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(testResponse);

        UserResponse actual = userService.findById(1L);

        assertThat(actual).isNotNull();
        assertThat(actual.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Consultation par identifiant inexistant")
    void findById_whenNotFound_throwsUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Consultation par email existant")
    void findByEmail_whenExists_returnsUserResponse() {
        when(userRepository.findByUserEmail("jean.mbarga@example.com")).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(testResponse);

        UserResponse actual = userService.findByEmail("jean.mbarga@example.com");

        assertThat(actual).isNotNull();
        assertThat(actual.userEmail()).isEqualTo("jean.mbarga@example.com");
    }

    @Test
    @DisplayName("Consultation par email inexistant")
    void findByEmail_whenNotFound_throwsUserNotFoundException() {
        when(userRepository.findByUserEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("unknown@example.com"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("unknown@example.com");
    }

    @Test
    @DisplayName("Récupération de tous les utilisateurs")
    void findAll_returnsListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(userMapper.toResponseList(List.of(testUser))).thenReturn(List.of(testResponse));

        List<UserResponse> actual = userService.findAll();

        assertThat(actual).hasSize(1);
    }

    @Test
    @DisplayName("Modification valide d'un utilisateur")
    void update_whenValid_returnsUpdatedUserResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUserEmailAndIdNot("jean.mbarga@example.com", 1L)).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testResponse);

        UserResponse actual = userService.update(1L, testRequest);

        assertThat(actual).isNotNull();
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Modification d'un utilisateur inexistant")
    void update_whenNotFound_throwsUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, testRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Modification avec email déjà pris par un autre utilisateur")
    void update_whenDuplicateEmail_throwsDuplicateUserEmailException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUserEmailAndIdNot("jean.mbarga@example.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> userService.update(1L, testRequest))
                .isInstanceOf(DuplicateUserEmailException.class)
                .hasMessageContaining("jean.mbarga@example.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Modification du statut du compte")
    void changeStatus_whenValid_returnsUpdatedUserResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(testResponse);

        UserResponse actual = userService.changeStatus(1L, UserAccountStatus.BLOCKED);

        assertThat(actual).isNotNull();
        assertThat(testUser.getUserAccountStatus()).isEqualTo(UserAccountStatus.BLOCKED);
    }

    @Test
    @DisplayName("Suppression d'un utilisateur existant")
    void deleteById_whenValid_deletesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteById(1L);

        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("Suppression d'un utilisateur inexistant")
    void deleteById_whenNotFound_throwsUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");

        verify(userRepository, never()).delete(any());
    }
}
