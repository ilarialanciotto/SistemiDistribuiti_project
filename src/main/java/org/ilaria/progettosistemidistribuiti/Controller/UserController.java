package org.ilaria.progettosistemidistribuiti.Controller;

import lombok.AllArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketDTO;
import org.ilaria.progettosistemidistribuiti.Service.AI.AIService;
import org.ilaria.progettosistemidistribuiti.Service.TicketService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/ticket/user")
@AllArgsConstructor
public class UserController {

    private final TicketService ticketService;

    @PostMapping(value = "/load")
    public ResponseEntity<String> createTicket(@RequestBody TicketDTO ticketDTO) throws IOException {
        ticketService.load(ticketDTO, null);
        return ResponseEntity.ok("Ticket created successfully");
    }

    @PostMapping(value = "/load", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createTicket(
            @RequestParam(value = "ticket", required = false) String ticketJson,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            TicketDTO ticketDTO = null;
            if (ticketJson != null  && !ticketJson.isBlank() && !ticketJson.contains("\"problem_title\":\"\"")) {
                ObjectMapper mapper = new ObjectMapper();
                ticketDTO = mapper.readValue(ticketJson, TicketDTO.class);
            }
            ticketService.load(ticketDTO, file);
            return ResponseEntity.ok("Ticket created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Load failed");
        }
    }

}
