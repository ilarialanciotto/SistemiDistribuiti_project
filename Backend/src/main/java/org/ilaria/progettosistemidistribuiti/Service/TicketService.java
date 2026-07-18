package org.ilaria.progettosistemidistribuiti.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.DTO.*;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Attachment;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.ilaria.progettosistemidistribuiti.Model.Level;
import org.ilaria.progettosistemidistribuiti.Model.Problem;
import org.ilaria.progettosistemidistribuiti.Repository.TicketRepository;
import org.ilaria.progettosistemidistribuiti.Service.AI.AIService;
import org.ilaria.progettosistemidistribuiti.Service.Mapper.AttachmentMapper;
import org.ilaria.progettosistemidistribuiti.Service.Mapper.TicketAdminMapper;
import org.ilaria.progettosistemidistribuiti.Service.Mapper.TicketMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketAdminMapper ticketAdminMapper;
    private final TicketMapper ticketMapper;
    private final AttachmentMapper attachmentMapper;
    private final TicketRepository ticketRepository;
    private final AIService aiService;

    @PersistenceContext
    private EntityManager entityManager;

    public void load(TicketDTO ticketDTO, MultipartFile file) throws IOException {

        if(ticketDTO==null && file==null) return;
        LocalDateTime now = LocalDateTime.now();
        if(file!=null && !file.isEmpty() ){
            String name = file.getOriginalFilename();
            String extension = name.substring(name.lastIndexOf(".")).toLowerCase();
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);

            if(ticketDTO!=null){
                if (extension.equals(".csv")
                        || extension.equals(".txt")
                        ||  extension.equals(".json")
                        || extension.equals(".log")) {
                    AttachmentDTO attachmentDTO = new AttachmentDTO(name, content, extension);
                    saveSingleTicket(ticketDTO,now,attachmentDTO);
                    return;
                } else {
                    saveSingleTicket(ticketDTO,now,null);
                    throw new RuntimeException("invalid file extension");
                }
            }

            byte[] bytes = file.getBytes();

            if (extension.equals(".json")) {
                parseAndSaveJson(bytes,now);
            } else if (extension.equals(".csv")){
                parseAndSaveCSV(new String(bytes, StandardCharsets.UTF_8),now);
            }else throw new RuntimeException("invalid file extension");
        }
        if(file==null) saveSingleTicket(ticketDTO,now,null);
    }

    private void saveSingleTicket(TicketDTO ticketDTO, LocalDateTime now, AttachmentDTO attachmentDTO) {
        if (ticketDTO.getProblem_title() == null || ticketDTO.getProblem_title().isEmpty()
                || ticketDTO.getDescription() == null || ticketDTO.getDescription().isEmpty()
                || ticketDTO.getUrgency_percepite() == null) throw new RuntimeException("invalid ticket format");

        if(ticketDTO.getProblem() == null) ticketDTO.setProblem(Problem.generic);

        Ticket ticket = ticketMapper.toEntity(ticketDTO);
        ticket.setStart_date(now);
        ticket.setProblem(ticketDTO.getProblem().name());
        ticket.setUrgency_percepite(ticketDTO.getUrgency_percepite().name());

        if (attachmentDTO!=null) {
            Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
            attachment.setTicket(ticket);
            ticket.setAttachment(attachment);

        }
        aiService.AIAnalysis(ticket);
    }

    private void parseAndSaveCSV(String content, LocalDateTime now) {
        try {
            content.lines().forEach(row -> {
                if (!row.isBlank()) {
                    String[] chunk = row.split("\\|");
                    if (chunk.length >= 2) {
                        TicketDTO nuovoDto = new TicketDTO();
                        nuovoDto.setProblem_title(chunk[0].trim());
                        nuovoDto.setDescription(chunk[1].trim());
                        if (chunk.length >= 3) {
                            nuovoDto.setProblem(Problem.valueOf(chunk[2].trim().toLowerCase()));
                        }
                        if (chunk.length == 4) {
                            nuovoDto.setUrgency_percepite(Level.valueOf(chunk[3].trim()));
                        }
                        saveSingleTicket(nuovoDto, now, null);
                    }
                }
            });
        }catch (Exception e) {
            throw new RuntimeException("Format CSV not valid");
        }

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

    @Transactional
    public void deleteTicket(long id) {
        ticketRepository.deleteById(id);
    }

    @Transactional
    public void modifiedState(long id, String newState) {
        ticketRepository.updateState(id,newState);
    }

    public Page<TicketAdminDTO> view(Pageable pageable) {
        Page<Ticket> ticketPage = ticketRepository.findAll(pageable);

        return ticketPage.map(ticketAdminMapper::toDto);
    }

    public Page<TicketAdminDTO> search(String problem, String keyword, Integer priority, String state,
                                       LocalDateTime startDate, Pageable pageable) {

        if (problem == null && keyword == null && priority == null && state == null && startDate == null) {

            return ticketRepository.findAll(pageable).map(ticketAdminMapper::toDto);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ticket> query = cb.createQuery(Ticket.class);
        Root<Ticket> ticket = query.from(Ticket.class);
        List<Predicate> predicates = new ArrayList<>();

        if (problem != null && !problem.isBlank()) {
            String lowerCat = problem.toLowerCase();
            predicates.add (cb.equal(cb.lower(ticket.get("category_AI")), lowerCat));
        }

        if (state != null && !state.isBlank()) {
            predicates.add(cb.equal(cb.lower(ticket.get("state")), state.toLowerCase()));
        }

        if (priority != null) {
            predicates.add(cb.equal(ticket.get("priority_level_AI"), priority));
        }

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(ticket.get("start_date"), startDate));
        }

        if (keyword != null && !keyword.isBlank()) {
            String matchKeyword = "%" + keyword.toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(ticket.get("keyword_AI")), matchKeyword));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        TypedQuery<Ticket> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Ticket> ticketResultList = typedQuery.getResultList();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Ticket> countRoot = countQuery.from(Ticket.class);
        List<Predicate> countPredicates = new ArrayList<>();

        if (problem != null && !problem.isBlank()) {
            String lowerCat = problem.toLowerCase();
            countPredicates.add(cb.equal(cb.lower(countRoot.get("category_AI")), lowerCat));
        }
        if (state != null && !state.isBlank()) {
            countPredicates.add(cb.equal(cb.lower(countRoot.get("state")), state.toLowerCase()));
        }
        if (priority != null) {
            countPredicates.add(cb.equal(countRoot.get("priority_level_AI"), priority));
        }
        if (startDate != null) {
            countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("start_date"), startDate));
        }
        if (keyword != null && !keyword.isBlank()) {
            String matchKeyword = "%" + keyword.toLowerCase() + "%";
            countPredicates.add(cb.like(cb.lower(countRoot.get("keyword_AI")), matchKeyword));
        }

        countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();
        List<TicketAdminDTO> dtoList = ticketResultList.stream()
                .map(ticketAdminMapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, totalElements);
    }

    public TicketReportDTO getGlobalMetrics() {
        long total = ticketRepository.count();

        if (total == 0) {
            return new TicketReportDTO(0, 0, 0, Collections.emptyList());
        }

        long closed = ticketRepository.countByStateIgnoreCase("done");
        int closedRate = (int) Math.round(((double) closed / total) * 100);
        long criticalUnresolved = ticketRepository.countCriticalUnresolved(4, "done");
        List<Object[]> distributionRows = ticketRepository.countHighPriorityByCategory();
        long totalCritical = distributionRows.stream().mapToLong(row -> (Long) row[1]).sum();
        List<CategoryReportDTO> distributionList = distributionRows.stream().map(row -> {
            String cat = (String) row[0];
            long count = (Long) row[1];
            int pct = totalCritical > 0 ? (int) Math.round(((double) count / totalCritical) * 100) : 0;
            return new CategoryReportDTO(cat, count, pct);
        }).collect(Collectors.toList());

        return new TicketReportDTO(total, closedRate, criticalUnresolved, distributionList);
    }

    public Ticket getTicket(long idTicket) {
        try {
            return ticketRepository.findById(idTicket);
        }catch (Exception e) {
            return null;
        }
    }
}
