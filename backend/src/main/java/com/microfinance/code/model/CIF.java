package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cif")
public class CIF {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name = "cif_id")
    private String cifId;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "gender")
    private String gender;
    @Column(name = "job")
    private String job;
    @Column(name = "income_amount")
    private Double incomAmounte;
    @Column(name = "nrc_number")
    private String NRC;
    @Column(name = "front_NRC_photo")
    private String frontNRCUrl;
    @Column(name = "back_NRC_photo")
    private String backNRCUrl;
    @Column(name = "user_photo")
    private String userPhotoURL;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "register_date")
    private LocalDateTime registerDate;
    @Column(name = "approve_date")
    private LocalDateTime approveDate;
    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private Status status;
    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    private enum Status {
        APPROVE("Approve"),
        REJECT("Reject");
        private final String displayName;
        Status(String displayName){
            this.displayName = displayName;
        }
        public String getDisplayName(){
            return displayName;
        }
    }
}
