package org.ilaria.progettosistemidistribuiti.Service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import org.ilaria.progettosistemidistribuiti.Model.DTO.AIAnalysisResultDTO;
import org.ilaria.progettosistemidistribuiti.Model.DTO.TicketDTO;
import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.springframework.context.annotation.Lazy;


@Lazy
@AiService
public interface TicketAnalyzer {

    @SystemMessage("""
        Sei un analista di ticket IT. Il tuo compito è leggere il ticket e produrre un JSON.
        REGOLE RIGIDE:
        1. Estrai 5 parole chiave, inserisci le 5 parole come stringa del campo keyword
        2. Classifica in: network, database, bug, configuration, other, inseriscilo nel cambo category
        3. Priorità da 1 a 5, inseriscilo nel campo priority_level
        Rispondi SOLO con il JSON, nient'altro.
        """)
    AIAnalysisResultDTO analyze(String ticket);
}