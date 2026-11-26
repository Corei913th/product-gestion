package eadl.productgestion.services.impl;

import eadl.productgestion.dto.request.LoginRequestDTO;
import eadl.productgestion.dto.request.RegisterRequestDTO;
import eadl.productgestion.dto.response.AuthResponse;
import eadl.productgestion.dto.response.UserResponse;
import eadl.productgestion.security.JwtService;
import eadl.productgestion.security.TokenBlacklistService;
import eadl.productgestion.services.AuthService;
import eadl.productgestion.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JwtService jwtService;

    private final TokenBlacklistService tokenBlacklistService;


    @Override
    public AuthResponse login(LoginRequestDTO request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userEmail(), request.userPassword())
        );

        String accessToken = jwtService.generateAccessToken(authentication.getName());

        UserResponse user = userService.findByEmail(authentication.getName());

        return new AuthResponse(
                accessToken,
                JwtService.TOKEN_TYPE,
                user
        );

    }


    @Override
    public AuthResponse register(RegisterRequestDTO request) {

        UserResponse user = userService.create(request);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userEmail(), request.userPassword())
        );

        String accessToken = jwtService.generateAccessToken(authentication.getName());

        return new AuthResponse(
                accessToken,
                JwtService.TOKEN_TYPE,
                user
        );
    }


    @Override
    public void logout(String token) {
        tokenBlacklistService.revoke(token, jwtService.getExpirationFromToken(token));
    }
}

