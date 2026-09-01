package com.ceoms.repository;

import com.ceoms.entity.Bill;
import com.ceoms.entity.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long>, JpaSpecificationExecutor<Bill> {
    long countByApprovalStatus(ApprovalStatus status);
}
