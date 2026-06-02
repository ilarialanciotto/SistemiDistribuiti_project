package org.ilaria.progettosistemidistribuiti.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.Category;
import org.ilaria.progettosistemidistribuiti.Model.DTO.AttachmentDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketAdminDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Attachment;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.ilaria.progettosistemidistribuiti.Model.Level;
import org.ilaria.progettosistemidistribuiti.Repository.TicketRepository;
import org.ilaria.progettosistemidistribuiti.Service.Mapper.AttachmentMapper;
import org.ilaria.progettosistemidistribuiti.Service.Mapper.TicketAdminMapper;
import org.ilaria.progettosistemidistribuiti.Service.Mapper.TicketMapper;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketAdminMapper ticketAdminMapper;
    private final TicketMapper ticketMapper;
    private final AttachmentMapper attachmentMapper;
    private final TicketRepository ticketRepository;

    public void load(TicketDTO ticketDTO, MultipartFile file) throws IOException {

        if(ticketDTO==null && file==null) return;
        LocalDateTime now = LocalDateTime.now();
        if(file!=null && !file.isEmpty() ){
            String name = file.getOriginalFilename();
            String format = file.getContentType();
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            if (ticketDTO!=null) {
                AttachmentDTO attachmentDTO = new AttachmentDTO(name, content, format);
                saveSingleTicket(ticketDTO,now,attachmentDTO);
                return;
            }

            String contentType = file.getContentType();
            byte[] bytes = file.getBytes();

            if (contentType != null && contentType.equals(MediaType.APPLICATION_JSON_VALUE)) {
                parseAndSaveJson(bytes,now);
            } else {
                parseAndSaveText(new String(bytes, StandardCharsets.UTF_8),now);
            }
        }
        if(file==null) saveSingleTicket(ticketDTO,now,null);
    }

    private void saveSingleTicket(TicketDTO ticketDTO, LocalDateTime now, AttachmentDTO attachmentDTO) {
        Ticket ticket = ticketMapper.toEntity(ticketDTO);
        ticket.setStart_date(now);
        ticket.setCategory(ticketDTO.getCategory().name());
        ticket.setUrgency_percepite(ticketDTO.getUrgency_percepite().name());

        if (attachmentDTO!=null) {
            Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
            attachment.setTicket(ticket);
            ticket.setAttachment(attachment);

        }
        ticketRepository.save(ticket);
    }

    private void parseAndSaveText(String content, LocalDateTime now) {
        content.lines().forEach(row -> {
            if (!row.isBlank()) {
                String[] chunk = row.split("\\|");
                if (chunk.length >= 2) {
                    TicketDTO nuovoDto = new TicketDTO();
                    nuovoDto.setProblem_title(chunk[0].trim());
                    nuovoDto.setDescription(chunk[1].trim());
                    if (chunk.length >= 3) {
                        nuovoDto.setCategory(Category.valueOf(chunk[2].trim().toLowerCase()));
                    }
                    if (chunk.length >= 4) {
                        nuovoDto.setUrgency_percepite(Level.valueOf(chunk[3].trim()));
                    }
                    saveSingleTicket(nuovoDto, now, null);
                }
            }
        });
    }

    private void parseAndSaveJson(byte[] content,LocalDateTime now) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<TicketDTO> listaDalJson = mapper.readValue(content, new TypeReference<List<TicketDTO>>(){});
            for (TicketDTO dto : listaDalJson) {
                saveSingleTicket(dto,now,null);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Format Json not valid");
        }
    }

    public LinkedList<TicketAdminDTO> view() {
        LinkedList<TicketAdminDTO> ticketList = new LinkedList<>();
        for (Ticket ticket : ticketRepository.findAll()) {
            ticketList.add(ticketAdminMapper.toDto(ticket));
        }
        return ticketList;
    }

    @Transactional
    public void modifiedState(TicketAdminDTO ticketAdmin, String newState) {
        ticketRepository.updateState(ticketAdmin.getProblem_title(),newState);
    }

    public List<TicketAdminDTO> search(String category, String keyword, Integer priority, String state, LocalDateTime startDate) {
        return ticketRepository.findAll().stream()
                .filter(t -> category == null || t.getCategory().equalsIgnoreCase(category) || (t.getCategory_AI() != null && t.getCategory_AI().equalsIgnoreCase(category)))
                .filter(t -> state == null || t.getState().equalsIgnoreCase(state))
                .filter(t -> priority == null || t.getPriority_level_AI().equals(priority))
                .filter(t -> startDate == null || t.getStart_date().isAfter(startDate))
                .filter(t -> keyword == null ||
                        t.getProblem_title().toLowerCase().contains(keyword.toLowerCase()) ||
                        t.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                        (t.getKeyword_AI() != null && t.getKeyword_AI().toLowerCase().contains(keyword.toLowerCase())))
                .map(ticketAdminMapper::toDto)
                .collect(Collectors.toList());
    }
}
