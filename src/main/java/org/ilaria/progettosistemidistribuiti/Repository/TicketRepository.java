package org.ilaria.progettosistemidistribuiti.Repository;


import org.ilaria.progettosistemidistribuiti.Model.Entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {


    void update(Ticket ticket);
}
