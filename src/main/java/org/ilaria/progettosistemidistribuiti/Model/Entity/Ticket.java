package org.ilaria.progettosistemidistribuiti.Model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ilaria.progettosistemidistribuiti.Model.Category;
import org.ilaria.progettosistemidistribuiti.Model.Level;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String problem_title;
        private String description;
        private Category category;
        private Level urgency_percepite;
        private LocalDateTime start_date;
        private String state;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "attachment_id", referencedColumnName = "id")
    Attachment attachment;



}
