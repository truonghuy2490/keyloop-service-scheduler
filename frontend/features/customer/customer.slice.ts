import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { Customer } from "./customer.types";

interface CustomerState {
  currentCustomer: Customer | null;
}

const initialState: CustomerState = {
  currentCustomer: null,
};

const customerSlice = createSlice({
  name: "customer",
  initialState,
  reducers: {
    setCurrentCustomer: (state, action: PayloadAction<Customer>) => {
      state.currentCustomer = action.payload;
    },
  },
});

export const { setCurrentCustomer } = customerSlice.actions;

export default customerSlice.reducer;
