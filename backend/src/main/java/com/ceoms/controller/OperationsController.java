package com.ceoms.controller;

import com.ceoms.dto.response.ApiResponse;
import com.ceoms.entity.*;
import com.ceoms.service.impl.AttendanceService;
import com.ceoms.service.impl.ExpenseBillService;
import com.ceoms.service.impl.RegistrationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Operations")
public class OperationsController {

    private final RegistrationService registrationService;
    private final ExpenseBillService expenseBillService;
    private final AttendanceService attendanceService;

    @PostMapping("/registrations")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Registration> register(@RequestBody Map<String, Long> body) {
        return ApiResponse.success(registrationService.register(body.get("eventId")));
    }

    @DeleteMapping("/registrations/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Void> cancelRegistration(@PathVariable Long id) {
        registrationService.cancel(id);
        return ApiResponse.success("Registration cancelled", null);
    }

    @GetMapping("/registrations/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<Registration>> myRegistrations() {
        return ApiResponse.success(registrationService.getMyRegistrations());
    }

    @GetMapping("/registrations/event/{eventId}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPER_ADMIN')")
    public ApiResponse<List<Registration>> eventRegistrations(@PathVariable Long eventId) {
        return ApiResponse.success(registrationService.getEventRegistrations(eventId));
    }

    @PostMapping("/expenses")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPER_ADMIN')")
    public ApiResponse<Expense> createExpense(@RequestBody ExpenseBillService.ExpenseRequest request) {
        return ApiResponse.success(expenseBillService.createExpense(request));
    }

    @GetMapping("/expenses/event/{eventId}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPER_ADMIN')")
    public ApiResponse<List<Expense>> getExpenses(@PathVariable Long eventId) {
        return ApiResponse.success(expenseBillService.getEventExpenses(eventId));
    }

    @PostMapping(value = "/bills", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPER_ADMIN')")
    public ApiResponse<Bill> uploadBill(
            @RequestParam(required = false) Long expenseId,
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) String billNumber,
            @RequestParam java.math.BigDecimal amount,
            @RequestParam(required = false) java.time.LocalDate billDate,
            @RequestParam("file") MultipartFile file) {
        ExpenseBillService.BillRequest req = new ExpenseBillService.BillRequest();
        req.setExpenseId(expenseId);
        req.setVendorId(vendorId);
        req.setBillNumber(billNumber);
        req.setAmount(amount);
        req.setBillDate(billDate);
        return ApiResponse.success(expenseBillService.uploadBill(req, file));
    }

    @GetMapping("/bills")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPER_ADMIN')")
    public ApiResponse<List<Bill>> getBills() {
        return ApiResponse.success(expenseBillService.getAllBills());
    }

    @PostMapping("/bills/{id}/approve")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Bill> approveBill(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ApiResponse.success(expenseBillService.approveBill(id, body.get("comment")));
    }

    @PostMapping("/bills/{id}/reject")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Bill> rejectBill(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ApiResponse.success(expenseBillService.rejectBill(id, body.get("comment")));
    }

    @PostMapping("/attendance/check-in")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPER_ADMIN', 'STUDENT')")
    public ApiResponse<Attendance> checkIn(@RequestBody Map<String, Object> body) {
        return ApiResponse.success(attendanceService.checkIn(
                Long.valueOf(body.get("eventId").toString()),
                body.get("userId") != null ? Long.valueOf(body.get("userId").toString()) : null,
                body.get("attendeeName") != null ? body.get("attendeeName").toString() : null,
                com.ceoms.entity.enums.AttendanceType.valueOf(body.getOrDefault("type", "STUDENT").toString()),
                com.ceoms.entity.enums.CheckInMethod.valueOf(body.getOrDefault("method", "MANUAL").toString())
        ));
    }

    @GetMapping("/attendance/event/{eventId}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'SUPER_ADMIN')")
    public ApiResponse<List<Attendance>> eventAttendance(@PathVariable Long eventId) {
        return ApiResponse.success(attendanceService.getEventAttendance(eventId));
    }

    @GetMapping("/attendance/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<Attendance>> myAttendance() {
        return ApiResponse.success(attendanceService.getMyAttendance());
    }
}
