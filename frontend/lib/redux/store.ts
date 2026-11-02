import { configureStore, combineReducers } from "@reduxjs/toolkit";
import {
  persistReducer,
  FLUSH,
  REHYDRATE,
  PAUSE,
  PERSIST,
  PURGE,
  REGISTER,
} from "redux-persist";
import storage from "redux-persist/lib/storage";

import { baseApiSlice } from "@/lib/api/base-api";
import customerReducer from "@/features/customer/customer.slice";
import toastReducer from "@/features/shared/toast.slice";

// Combine application reducers
const rootReducer = combineReducers({
  // default reducers
  [baseApiSlice.reducerPath]: baseApiSlice.reducer,
  // Customer reducer
  customer: customerReducer,
  // Toast reducer
  toastSlice: toastReducer,
});

// Persist only customer state
const persistConfig = {
  key: "root",
  storage,
  whitelist: ["customer"],
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

export const makeStore = () => {
  return configureStore({
    reducer: persistedReducer,

    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        serializableCheck: {
          ignoredActions: [
            FLUSH,
            REHYDRATE,
            PAUSE,
            PERSIST,
            PURGE,
            REGISTER,
          ],
        },
      }).concat(baseApiSlice.middleware),
  });
};

export type AppStore = ReturnType<typeof makeStore>;

export type RootState = ReturnType<AppStore["getState"]>;

export type AppDispatch = AppStore["dispatch"];
