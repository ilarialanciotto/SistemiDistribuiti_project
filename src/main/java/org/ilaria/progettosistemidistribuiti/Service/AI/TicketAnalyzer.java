package org.ilaria.progettosistemidistribuiti.Service.AI;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import org.ilaria.progettosistemidistribuiti.Model.DTO.AIAnalysisResultDTO;
import org.springframework.context.annotation.Lazy;


@Lazy
@AiService
public interface TicketAnalyzer {

    @SystemMessage("""
        Sei un analista di ticket IT. Il tuo compito è leggere e analizzare il ticket e produrre un JSON.
        REGOLE RIGIDE:
        1. Estrai parole chiave e inseriscile come stringa del campo keyword separate da , senza spazi
        2. Classifica in: network, database, bug, configuration, other, in base a tutto il ticket e inserisci il risultato nel cambo category
        3. Assegna una priorità da 1 a 5 secondo tutto il ticket e  inseriscila nel campo priority_level
        Rispondi SOLO con il JSON, nient'altro.
        """)
    AIAnalysisResultDTO analyze(String description);
}