package org.ilaria.progettosistemidistribuiti.Model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

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

        @Column(columnDefinition = "TEXT")
        private String description;

        private String category;
        private String urgency_percepite;
        private Integer priority_level_AI = -1;
        private LocalDateTime start_date;
        private String category_AI;
        private String keyword_AI;
        private String state = "sent";

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "attachment_id", referencedColumnName = "id")
    Attachment attachment;



}
