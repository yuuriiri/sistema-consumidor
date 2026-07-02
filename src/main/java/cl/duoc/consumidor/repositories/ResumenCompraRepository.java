package cl.duoc.consumidor.repositories;

import cl.duoc.consumidor.models.ResumenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumenCompraRepository extends JpaRepository<ResumenCompra, Long> {
}
