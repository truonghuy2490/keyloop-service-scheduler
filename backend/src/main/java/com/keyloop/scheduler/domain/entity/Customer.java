package com.keyloop.scheduler.domain.entity;

import com.keyloop.scheduler.domain.common.SoftDeletableAuditableEntity;

public class Customer extends SoftDeletableAuditableEntity {

    private String fullName;
    private String email;
    private String phone;

    protected Customer() {
    }

    private Customer(Builder builder) {
        super(builder);
        this.fullName = builder.fullName;
        this.email = builder.email;
        this.phone = builder.phone;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
            extends SoftDeletableAuditableEntity.Builder<Builder> {

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

        public Customer build() {
            return new Customer(this);
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
