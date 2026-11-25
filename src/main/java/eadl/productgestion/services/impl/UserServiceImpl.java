package eadl.productgestion.services.impl;

import eadl.productgestion.dto.request.UserRequest;
import eadl.productgestion.dto.response.UserResponse;
import eadl.productgestion.entity.User;
import eadl.productgestion.exception.DuplicateUserEmailException;
import eadl.productgestion.exception.UserNotFoundException;
import eadl.productgestion.mappers.UserMapper;
import eadl.productgestion.repositories.UserRepository;
import eadl.productgestion.services.UserService;
import eadl.productgestion.utils.UserAccountStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        checkEmailUniqueness(request.userEmail(), null);

        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);
        log.info("Utilisateur créé: id={}, email={}", saved.getId(), saved.getUserEmail());
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse findById(Long id) {
        return userMapper.toResponse(getUserOrThrow(id));
    }

    @Override
    public UserResponse findByEmail(String email) {
        return userRepository.findByUserEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Utilisateur introuvable par email: {}", email);
                    return new UserNotFoundException(email);
                });
    }

    @Override
    public List<UserResponse> findAll() {
        return userMapper.toResponseList(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = getUserOrThrow(id);
        checkEmailUniqueness(request.userEmail(), id);

        user.setUserName(request.userName());
        user.setUserEmail(request.userEmail());
        user.setUserPassword(request.userPassword());
        user.setUserRole(request.userRole());
        user.setUserAccountStatus(request.userAccountStatus());

        User updated = userRepository.save(user);
        log.info("Utilisateur mis à jour: id={}, email={}", updated.getId(), updated.getUserEmail());
        return userMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public UserResponse changeStatus(Long id, UserAccountStatus status) {
        User user = getUserOrThrow(id);
        user.setUserAccountStatus(status);

        User saved = userRepository.save(user);
        log.info("Statut utilisateur modifié: id={}, statut={}", id, status);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.delete(getUserOrThrow(id));
        log.info("Utilisateur supprimé: id={}", id);
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Utilisateur introuvable: id={}", id);
                    return new UserNotFoundException(id);
                });
    }

    private void checkEmailUniqueness(String email, Long currentId) {
        boolean exists = (currentId == null)
                ? userRepository.existsByUserEmail(email)
                : userRepository.existsByUserEmailAndIdNot(email, currentId);
        if (exists) {
            log.warn("Email déjà utilisé: {}", email);
            throw new DuplicateUserEmailException(email);
        }
    }
}
