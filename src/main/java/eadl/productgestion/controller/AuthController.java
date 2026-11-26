package eadl.productgestion.controller;

import eadl.productgestion.dto.request.LoginRequestDTO;
import eadl.productgestion.dto.request.RegisterRequestDTO;
import eadl.productgestion.dto.response.ApiError;
import eadl.productgestion.dto.response.AuthResponse;
import eadl.productgestion.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/auth", "/auth"})
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentification", description = "Endpoints d'inscription, connexion et déconnexion avec jetons JWT")
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;

    @Operation(summary = "Connexion d'un utilisateur", description = "Authentifie un utilisateur et retourne un jeton JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données d'authentification invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Identifiants incorrects",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequestDTO request) {

        log.info("POST /api/auth/login -> email: {}", request.userEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Inscription d'un utilisateur", description = "Crée un nouveau compte utilisateur et retourne un jeton JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compte créé et authentifié avec succès",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données d'inscription invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Adresse email déjà existante",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequestDTO request) {

        log.info("POST /api/auth/register -> email: {}", request.userEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Déconnexion de l'utilisateur", description = "Invalide le jeton JWT courant en l'ajoutant à la liste noire")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Déconnexion réussie"),
            @ApiResponse(responseCode = "401", description = "Jeton manquant ou invalide")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        log.info("POST /api/auth/logout");
        authService.logout(authorization.substring(BEARER_PREFIX.length()));
        return ResponseEntity.noContent().build();
    }
}
