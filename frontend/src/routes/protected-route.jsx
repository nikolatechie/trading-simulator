import React from "react";
import { Navigate } from "react-router-dom";

const isAuth = () => {
  const jwt = localStorage.getItem("jwt");
  return jwt === null ? false : true;
};

export default function ProtectedRoute({ children }) {
  return isAuth() ? children : <Navigate to='/login' replace />;
}
