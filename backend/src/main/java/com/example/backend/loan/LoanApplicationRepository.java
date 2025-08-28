package com.example.backend.loan;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface LoanApplicationRepository extends JpaRepository<LoanApplicationEntity, UUID> {

    // NEW: fetch last 10 by createdAt desc
    List<LoanApplicationEntity> findTop10ByOrderByCreatedAtDesc();
}
