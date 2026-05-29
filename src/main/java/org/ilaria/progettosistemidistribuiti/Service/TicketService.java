package org.ilaria.progettosistemidistribuiti.Service;

import lombok.RequiredArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.DTO.AttachmentDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Attachment;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.ilaria.progettosistemidistribuiti.Model.State;
import org.ilaria.progettosistemidistribuiti.Repository.TicketRepository;
import org.ilaria.progettosistemidistribuiti.Service.Mapper.AttachmentMapper;
import org.ilaria.progettosistemidistribuiti.Service.Mapper.TicketMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketMapper ticketMapper;
    private final AttachmentMapper attachmentMapper;
    private final TicketRepository ticketRepository;

    public void load(TicketDTO ticketDTO, MultipartFile file) throws IOException {

        Ticket ticket = ticketMapper.toEntity(ticketDTO);
        ticket.setState(State.start.name());
        ticket.setStart_date(LocalDateTime.now());
        if (!file.isEmpty()) {
            String name = file.getOriginalFilename();
            String format = file.getContentType();
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            AttachmentDTO attachmentDTO = new AttachmentDTO(name, format, content);
            Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
            attachment.setTicket(ticket);
            ticket.setAttachment(attachment);
        }
        ticketRepository.save(ticket);
    }

}
