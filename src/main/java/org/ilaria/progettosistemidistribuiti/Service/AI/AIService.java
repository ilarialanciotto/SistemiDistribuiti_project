package org.ilaria.progettosistemidistribuiti.Service.AI;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.DTO.AIAnalysisResultDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketAdminDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.ilaria.progettosistemidistribuiti.Model.State;
import org.ilaria.progettosistemidistribuiti.Repository.TicketRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIService {


    private final TicketAnalyzer ticketAnalyzer;
    private final TicketRepository ticketRepository;

    @Async
    @Transactional
    public void AIAnalysis(Ticket ticket) {
        String description = ticket.getDescription();
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
