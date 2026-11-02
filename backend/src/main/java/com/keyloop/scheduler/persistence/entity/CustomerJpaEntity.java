package com.keyloop.scheduler.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.keyloop.scheduler.persistence.entity.common.SoftDeletedAuditableJpaEntity;

@Entity
@Table(name = "customer")
public class CustomerJpaEntity extends SoftDeletedAuditableJpaEntity {

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Column(name = "phone", length = 40)
    private String phone;

    protected CustomerJpaEntity() {
    }

    private CustomerJpaEntity(Builder builder) {
        super(builder);
        this.fullName = builder.fullName;
        this.email = builder.email;
        this.phone = builder.phone;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends SoftDeletedAuditableJpaEntity.Builder<Builder> {

        private String fullName;
        private String email;
        private String phone;

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public CustomerJpaEntity build() {
            return new CustomerJpaEntity(this);
        }
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
