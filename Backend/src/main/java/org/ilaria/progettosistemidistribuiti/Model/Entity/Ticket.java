package org.ilaria.progettosistemidistribuiti.Model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

        private String problem;
        private String urgency_percepite;
        private Integer priority_level_AI = -1;
        private LocalDateTime start_date;
        private String category_AI;
        private String keyword_AI;
        private String state = "sent";

        @OneToOne(cascade = CascadeType.ALL)
        @JoinColumn(name = "attachment_id", referencedColumnName = "id")
        Attachment attachment;

        @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        private List<Note> notes = new ArrayList<>();

        @Override
        public String toString() {
                return "problem_title: " + problem_title + " description: " + description +
                        " category: " + problem + " urgency_percepite: " + urgency_percepite +
                        " priority_level_AI: " + priority_level_AI + " start_date:" + start_date +
                        " state: " + state + " attachment: " + attachment + " note: " + notes;
        }
}
