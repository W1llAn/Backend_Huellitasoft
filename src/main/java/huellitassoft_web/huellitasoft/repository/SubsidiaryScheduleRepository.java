package huellitassoft_web.huellitasoft.repository;

import huellitassoft_web.huellitasoft.entity.SubsidiarySchedule;
import huellitassoft_web.huellitasoft.enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubsidiaryScheduleRepository extends JpaRepository<SubsidiarySchedule, Long> {
    List<SubsidiarySchedule> findBySubsidiaryIdSubsidiary(Long subsidiaryId);
    Optional<SubsidiarySchedule> findBySubsidiaryIdSubsidiaryAndDayOfWeek(Long subsidiaryId, DayOfWeek dayOfWeek);
    @Modifying
    @Query("DELETE FROM SubsidiarySchedule s WHERE s.subsidiary.idSubsidiary = :idSubsidiary")
    void deleteBySubsidiaryId(@Param("idSubsidiary") Long idSubsidiary);
}