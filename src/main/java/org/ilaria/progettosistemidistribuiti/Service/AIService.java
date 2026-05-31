package org.ilaria.progettosistemidistribuiti.Service;

import lombok.RequiredArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.DTO.AIAnalysisResultDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.ilaria.progettosistemidistribuiti.Model.State;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIService {

    private final TicketAnalyzer ticketAnalyzer;

    public void AIAnalysis(Ticket ticket) {
        try {
            AIAnalysisResultDTO analysisResult = ticketAnalyzer.analyze(ticket.toString());
            if (analysisResult!=null) {
                ticket.setCategory_AI(analysisResult.getCategory());
                ticket.setPriority_level_AI(analysisResult.getPriority_level());
                ticket.setKeyword_AI(analysisResult.getKeyword());
                ticket.setState(State.ai_analyzed.name());
            }
        } catch (Exception e) {
            System.err.println("AI analysis failed");
        }
    }

}
