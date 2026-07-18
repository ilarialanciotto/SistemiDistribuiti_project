package org.ilaria.progettosistemidistribuiti.Controller;

import lombok.AllArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.DTO.UserDTO;
import org.ilaria.progettosistemidistribuiti.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/ticket/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO user) {
        try {
            authService.register(user);
            return ResponseEntity.ok("User successfully registered");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user) {
        try{
            return authService.login(user);}
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("credential not valid");
        }
    }

}
