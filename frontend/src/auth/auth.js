export const isUserAuthenticated = () => {
  const jwt = localStorage.getItem("jwt");
  return jwt === null ? false : true;
};
