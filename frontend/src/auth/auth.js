export const isUserAuthenticated = () => {
  const jwt = localStorage.getItem("jwt");
  return jwt === null ? false : true;
};

export const signOut = (navigate) => {
  localStorage.removeItem("jwt");
  navigate("/");
};

export const refreshJwt = (navigate) => {
  localStorage.removeItem("jwt");
  navigate("/login");
}