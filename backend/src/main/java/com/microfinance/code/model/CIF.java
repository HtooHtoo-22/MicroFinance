package com.microfinance.code.model;

import com.microfinance.code.status.CIFStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "cif")
public class CIF {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "cif_id", nullable = false, length = 30)
    private String cifId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "job", nullable = false, length = 50)
    private String job;

    @Column(name = "income_amount", nullable = false)
    private Double incomeAmount;

    @Column(name = "nrc_number", nullable = false, length = 20)
    private String NRC;

    @Column(name = "front_NRC_photo", nullable = false, length = 255)
    private String frontNRCUrl;

    @Column(name = "back_NRC_photo", nullable = false, length = 255)
    private String backNRCUrl;

    @Column(name = "user_photo", nullable = false, length = 255)
    private String userPhotoURL;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Column(name = "township", nullable = false, length = 50)
    private String township;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private CIFStatus status;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name ="user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate(){
        if(createdDate == null){
            LocalDateTime now = LocalDateTime.now();
            createdDate = now ;
        }
        if(status == null){

            status = CIFStatus.ACTIVE;
        }
    }


    }