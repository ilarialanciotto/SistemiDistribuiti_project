package org.ilaria.progettosistemidistribuiti.Service.AI;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.DTO.AIAnalysisResultDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketAdminDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.ilaria.progettosistemidistribuiti.Model.State;
import org.ilaria.progettosistemidistribuiti.Repository.TicketRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIService {

    private final TicketAnalyzer ticketAnalyzer;
    private final TicketRepository ticketRepository;

    @Transactional
    public void AIAnalysis(TicketAdminDTO ticketDTO) {
        Ticket ticket = ticketRepository.getTicketByProblem_title(ticketDTO.getProblem_title());
        String description = ticketDTO.getDescription();
        if(ticket.getAttachment()!=null){
            description += ticket.getAttachment().getContent();
        }

        AIAnalysisResultDTO analysisResult = ticketAnalyzer.analyze(description);
        if (analysisResult != null) {
            ticketRepository.update(ticket.getId(),
                    analysisResult.getCategory(),
                    Integer.valueOf(analysisResult.getPriority_level()),
                    analysisResult.getKeyword(),
                    State.ai_analyzed.name());
        }
    }

}
