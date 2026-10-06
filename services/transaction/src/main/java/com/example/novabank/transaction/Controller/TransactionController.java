package com.example.novabank.transaction.Controller;

import com.example.novabank.transaction.DTO.Request.TransferMoneyRequest;
import com.example.novabank.transaction.Service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/request-transfer-money")
    public ResponseEntity<?> requestTransferMoney(@RequestBody @Valid TransferMoneyRequest transferMoneyRequest)
            throws JsonProcessingException {
        transactionService.requestTransferMoney(transferMoneyRequest);
        return ResponseEntity.ok(Map.of("message",
                "transfer request under processing, we will notify you when complete"
        ));
    }

    @GetMapping("/get-user-transactions")
    public ResponseEntity<?> getUserTransactions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (page == null || size == null) {
            // No pagination
            var response = transactionService.getUserTransactions(null);
            return ResponseEntity.ok(Map.of("message", response));
        } else {
            Pageable pageable = PageRequest.of(page, size);
            var response = transactionService.getUserTransactions(pageable);
            return ResponseEntity.ok(Map.of("message", response));
        }
    }

    @GetMapping("/get-today-totals")
    public ResponseEntity<?> getTodayTotals() {
        var response = transactionService.getTodayTotals();
        return ResponseEntity.ok(Map.of("message", response));
    }

}