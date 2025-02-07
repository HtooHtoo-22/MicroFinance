package com.microfinance.code.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
@Data
@Entity
@Table(name = "user_has_role")
public class UserHasRole {
    @Embeddable
    public static class UserRolePK implements Serializable{
        private Integer userId;
        private Integer roleId;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getRoleId() {
            return roleId;
        }

        public void setRoleId(Integer roleId) {
            this.roleId = roleId;
        }
        @Override // Recommended
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserRolePK that = (UserRolePK) o;
            return Objects.equals(roleId, that.roleId) && Objects.equals(userId, that.userId);
        }

        @Override // Recommended
        public int hashCode() {
            return Objects.hash(roleId, userId);
        }
    }

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;


}
