package com.microfinance.code.model;

import com.microfinance.code.status.BranchStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "branch")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, length = 5)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Column(name = "township", nullable = false, length = 50)
    private String township;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private BranchStatus status;

    @PrePersist
    protected void onCreate(){
        if(createdDate == null){
            LocalDateTime now = LocalDateTime.now();
            createdDate = now ;
        }
        if(status == null){
            status = BranchStatus.OPEN;
        }
    }
}