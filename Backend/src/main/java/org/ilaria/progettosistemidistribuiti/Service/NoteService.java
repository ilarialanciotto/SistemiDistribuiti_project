package org.ilaria.progettosistemidistribuiti.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.DTO.NoteDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.NoteViewDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Note;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.ilaria.progettosistemidistribuiti.Repository.NoteRepository;
import org.ilaria.progettosistemidistribuiti.Service.Mapper.NoteMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NoteService {

    private final NoteMapper noteMapper;
    private final NoteRepository noteRepository;
    private final TicketService ticketService;

    public Page<NoteViewDTO> view(Pageable pageable) {
        Page<Note> notePage = noteRepository.findAll(pageable);
        return notePage.map(noteMapper::toDtoView);
    }

    @Transactional
    public void modifiedContent(long id, String newContent) {
        noteRepository.updateContent(id,newContent);
    }

    @Transactional
    public void deleteNote(long id) {
        noteRepository.deleteNote(id);
    }

    @Transactional
    public void create(NoteDTO noteDTO) {
        if(noteDTO.getId_Ticket()==null ) throw new RuntimeException("invalid note format");
        Ticket ticket = ticketService.getTicket(noteDTO.getId_Ticket());
        if (ticket == null || noteDTO.getContent() == null || noteDTO.getContent().isEmpty()
                || noteDTO.getTitle() == null || noteDTO.getTitle().isEmpty())
            throw new RuntimeException("invalid note format");
        List<Note> notes = ticket.getNotes();
        Note note = noteMapper.toEntity(noteDTO);
        note.setTicket(ticket);
        notes.add(note);
        noteRepository.save(note);
    }
}
