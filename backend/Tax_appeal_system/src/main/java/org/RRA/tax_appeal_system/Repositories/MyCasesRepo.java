package org.RRA.tax_appeal_system.Repositories;

import org.RRA.tax_appeal_system.Models.MyCases;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyCasesRepo extends JpaRepository<MyCases, String> {

    List<MyCases> findByNotePreparator(String notePreparator);
}
