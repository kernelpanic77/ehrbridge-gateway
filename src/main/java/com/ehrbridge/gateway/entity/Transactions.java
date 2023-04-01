package com.ehrbridge.gateway.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
@Data
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String txnID;

    private String txn_status;
    private String hiuId;
    private String hipId;
    private String doctorId;


}
