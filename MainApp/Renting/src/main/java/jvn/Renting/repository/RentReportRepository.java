package jvn.Renting.repository;


import jvn.Renting.model.RentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentReportRepository extends JpaRepository<RentReport, Long> {

    RentReport findByRentInfoId(Long id);

}
