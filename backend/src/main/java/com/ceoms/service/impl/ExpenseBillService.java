package com.ceoms.service.impl;

import com.ceoms.audit.AuditService;
import com.ceoms.entity.*;
import com.ceoms.entity.enums.ApprovalStatus;
import com.ceoms.entity.enums.ExpenseCategory;
import com.ceoms.entity.enums.NotificationType;
import com.ceoms.exception.ResourceNotFoundException;
import com.ceoms.notification.NotificationService;
import com.ceoms.repository.*;
import com.ceoms.security.SecurityUtils;
import com.ceoms.storage.FileStorageService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseBillService {

    private final ExpenseRepository expenseRepository;
    private final BillRepository billRepository;
    private final BudgetRepository budgetRepository;
    private final EventRepository eventRepository;
    private final VendorRepository vendorRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    @Transactional
    public Expense createExpense(ExpenseRequest req) {
        Event event = eventRepository.findById(req.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User user = securityUtils.getCurrentUser();
        Expense expense = Expense.builder()
                .event(event)
                .budget(event.getBudget())
                .category(req.getCategory())
                .amount(req.getAmount())
                .description(req.getDescription())
                .expenseDate(req.getExpenseDate() != null ? req.getExpenseDate() : LocalDate.now())
                .uploadedBy(user)
                .build();
        expense = expenseRepository.save(expense);
        updateBudgetSpending(event);
        auditService.log(user, "EXPENSE_CREATED", "Expense", expense.getId(), null, req.getAmount().toString());
        return expense;
    }

    @Transactional
    public Bill uploadBill(BillRequest req, MultipartFile file) {
        User user = securityUtils.getCurrentUser();
        String path = fileStorageService.storeFile(file, "bills");
        Vendor vendor = null;
        if (req.getVendorId() != null) {
            vendor = vendorRepository.findById(req.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
        }
        Expense expense = null;
        if (req.getExpenseId() != null) {
            expense = expenseRepository.findById(req.getExpenseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        }
        Bill bill = Bill.builder()
                .expense(expense)
                .vendor(vendor)
                .billNumber(req.getBillNumber())
                .amount(req.getAmount())
                .billDate(req.getBillDate())
                .filePath(path)
                .fileName(file.getOriginalFilename())
                .uploadedBy(user)
                .build();
        bill = billRepository.save(bill);
        auditService.log(user, "BILL_UPLOADED", "Bill", bill.getId(), null, path);
        return bill;
    }

    @Transactional
    public Bill approveBill(Long id, String comment) {
        Bill bill = billRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bill not found"));
        bill.setApprovalStatus(ApprovalStatus.APPROVED);
        bill.setAdminComment(comment);
        billRepository.save(bill);
        auditService.log(securityUtils.getCurrentUser(), "BILL_APPROVED", "Bill", id, null, comment);
        return bill;
    }

    @Transactional
    public Bill rejectBill(Long id, String comment) {
        Bill bill = billRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bill not found"));
        bill.setApprovalStatus(ApprovalStatus.REJECTED);
        bill.setAdminComment(comment);
        billRepository.save(bill);
        if (bill.getUploadedBy() != null) {
            notificationService.send(bill.getUploadedBy(), "Bill Rejected",
                    "Your bill was rejected: " + comment, NotificationType.BILL_REJECTED, id);
        }
        auditService.log(securityUtils.getCurrentUser(), "BILL_REJECTED", "Bill", id, null, comment);
        return bill;
    }

    @Transactional(readOnly = true)
    public List<Expense> getEventExpenses(Long eventId) {
        return expenseRepository.findByEventId(eventId);
    }

    @Transactional(readOnly = true)
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    private void updateBudgetSpending(Event event) {
        if (event.getBudget() == null) return;
        BigDecimal total = expenseRepository.findByEventId(event.getId()).stream()
                .map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        event.getBudget().setActualSpending(total);
        budgetRepository.save(event.getBudget());
    }

    @Data
    public static class ExpenseRequest {
        private Long eventId;
        private ExpenseCategory category;
        private BigDecimal amount;
        private String description;
        private LocalDate expenseDate;
    }

    @Data
    public static class BillRequest {
        private Long expenseId;
        private Long vendorId;
        private String billNumber;
        private BigDecimal amount;
        private LocalDate billDate;
    }
}
