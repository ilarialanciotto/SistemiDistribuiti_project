package org.ilaria.progettosistemidistribuiti.Controller;

import lombok.AllArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.DTO.RequestStateDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketAdminDTO;
import org.ilaria.progettosistemidistribuiti.Service.AI.AIService;
import org.ilaria.progettosistemidistribuiti.Service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('admin')")
@RestController
@RequestMapping("/ticket/admin")
@AllArgsConstructor
public class AdminController {

    private final TicketService ticketService;

    @GetMapping("/view")
    public ResponseEntity<Page<TicketAdminDTO>> viewTicket(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        try {
            return ResponseEntity.ok(ticketService.view(pageable));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Page.empty());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TicketAdminDTO>> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        try {
            Page<TicketAdminDTO> results = ticketService.search(
                    category, keyword, priority, state, startDate, pageable
            );
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Page.empty());
        }
    }

    @PostMapping("/updateState")
    public ResponseEntity<String> updateState(@RequestBody RequestStateDTO requestStateDTO) {
        try {
            ticketService.modifiedState(requestStateDTO.getTicketAdmin(), requestStateDTO.getNewState());
            return ResponseEntity.ok("Update successful");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Update failed");
        }
    }

    @PostMapping("/deleteTicket")
    public ResponseEntity<String> deleteTicket(@RequestBody long id) {
        try {
            ticketService.deleteTicket(id);
            return ResponseEntity.ok("delete successful");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("delete failed");
        }
    }

}
