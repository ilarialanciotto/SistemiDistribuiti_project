package org.ilaria.progettosistemidistribuiti.Controller;

import lombok.AllArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.DTO.AttachmentDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.UserDTO;
import org.ilaria.progettosistemidistribuiti.Service.AuthService;
import org.ilaria.progettosistemidistribuiti.Service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/ticket")
@AllArgsConstructor
public class Controller {

    private final AuthService authService;
    private final TicketService ticketService;

    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@RequestBody UserDTO user) {
        try {
            authService.register(user);
            return ResponseEntity.ok("User successfully registered");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user) {
        try{
            return authService.login(user);}
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("credential not valid");
        }
    }

    @PostMapping("load")
    public ResponseEntity<String> creaTicket(
            @RequestPart("ticket") TicketDTO ticketDTO,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        try{
            ticketService.load(ticketDTO,file);
            return ResponseEntity.ok("Ticket created successfully");
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
