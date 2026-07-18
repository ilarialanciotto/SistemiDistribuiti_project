package org.ilaria.progettosistemidistribuiti.Controller;

import lombok.AllArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.DTO.*;
import org.ilaria.progettosistemidistribuiti.Service.NoteService;
import org.ilaria.progettosistemidistribuiti.Service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('admin')")
@RestController
@RequestMapping("/ticket/admin")
@AllArgsConstructor
public class AdminController {

    private final TicketService ticketService;
    private final NoteService noteService;

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

    @PostMapping(value = "/create")
    public ResponseEntity<String> createNote(@RequestBody NoteDTO noteDTO){
        try{
            noteService.create(noteDTO);}
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("Note created successfully");
    }

    @GetMapping("/viewNotes")
    public ResponseEntity<Page<NoteViewDTO>> viewNotes(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        try {
            return ResponseEntity.ok(noteService.view(pageable));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Page.empty());
        }
    }

    @GetMapping("/report")
    public ResponseEntity<TicketReportDTO> getGlobalStats() {
        return ResponseEntity.ok(ticketService.getGlobalMetrics());
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
    public ResponseEntity<String> updateState(@RequestBody RequestDTO requestDTO) {
        try {
            ticketService.modifiedState(requestDTO.getId(), requestDTO.getChange());
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

    @PostMapping("/updateNote")
    public ResponseEntity<String> updateNote(@RequestBody RequestDTO requestDTO ) {
        try {
            noteService.modifiedContent(requestDTO.getId(), requestDTO.getChange());
            return ResponseEntity.ok("Update successful");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Update failed");
        }
    }

    @PostMapping("/deleteNote")
    public ResponseEntity<String> deleteNote(@RequestBody long id) {
        try {
            noteService.deleteNote(id);
            return ResponseEntity.ok("delete successful");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("delete failed");
        }
    }

}
