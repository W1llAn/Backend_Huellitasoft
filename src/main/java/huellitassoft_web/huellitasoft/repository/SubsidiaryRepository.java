package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.Subsidiary;
import huellitassoft_web.huellitasoft.enums.SubsidiaryState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubsidiaryRepository extends JpaRepository<Subsidiary, Long> {
    List<Subsidiary> findByState(SubsidiaryState state);
    Optional<Subsidiary> findByName(String name);
    boolean existsByName(String name);
}
