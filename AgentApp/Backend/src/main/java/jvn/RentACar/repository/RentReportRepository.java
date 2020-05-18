package jvn.RentACar.repository;

import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.model.RentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentReportRepository extends JpaRepository<RentReport, Long> {

//    List<RentReport> findAllByLogicalStatusNot(LogicalStatus logicalStatus);

    List<RentReport> findAll();

}
