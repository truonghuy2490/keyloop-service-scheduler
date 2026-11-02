import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export enum ToastType {
  Success = "success",
  Error = "error",
  Warning = "warning",
  Info = "info",
}

export interface Toast {
  id: string;
  message: string | null;
  type: ToastType;
  duration: number;
  testId?: string; // add data-testid
}

export interface ToastState {
  toasts: Toast[];
}

const initialState: ToastState = {
  toasts: [],
};

export const toastSlice = createSlice({
  name: "toastSlice",
  initialState,
  reducers: {
    enqueueToast: (
      state,
      action: PayloadAction<{
        message: string;
        type: ToastType;
        duration?: number;
        testId?: string;
      }>,
    ) => {
      state.toasts.push({
        id: crypto.randomUUID(),
        message: action.payload.message,
        type: action.payload.type,
        duration: action.payload.duration ?? 3000,
        testId: action.payload.testId,
      });
    },

    removeToast: (state, action: PayloadAction<{ id: string }>) => {
      state.toasts = state.toasts.filter(
        (toast) => toast.id !== action.payload.id,
      );
    },

    clearAllToasts: (state) => {
      state.toasts = [];
    },
  },
});

export const { enqueueToast, removeToast, clearAllToasts } = toastSlice.actions;
export default toastSlice.reducer;
