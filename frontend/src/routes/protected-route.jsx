import React from "react";
import { Navigate } from "react-router-dom";
import { isUserAuthenticated } from "../auth/auth.js";

export default function ProtectedRoute({ children }) {
  return isUserAuthenticated() ? children : <Navigate to='/login' replace />;
}
