package org.ilaria.progettosistemidistribuiti.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {

    private String title;
    private String content;
    private LocalDateTime date;
    private Long id_Ticket ;
    private String ticketTitle = "";
}