package org.ilaria.progettosistemidistribuiti.Model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String format;

    @OneToOne(mappedBy = "attachment")
    private Ticket ticket;

    @Override
    public String toString(){
        return "Content: " + content;
    }
}
