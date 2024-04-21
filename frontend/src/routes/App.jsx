import React from "react";
import {
  createTheme,
  StyledEngineProvider,
  ThemeProvider,
} from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import { useState, useCallback } from "react";
import { AppBar } from "../components/AppBar.jsx";
import { Drawer } from "../components/drawer/Drawer.jsx";
import { AppContent } from "../components/AppContent.jsx";
import { useNavigate } from "react-router-dom";
import { signOut } from "../auth/auth.js";

const mdTheme = createTheme();

export default function App() {
  const [open, setOpen] = useState(true);
  const [selectedPage, setSelectedPage] = useState("Dashboard");
  const navigate = useNavigate();

  const toggleDrawer = useCallback(() => {
    setOpen(!open);
  }, [open]);

  const handleSelectPage = useCallback((page) => {
    if (page === "Sign out") signOut(navigate);
    else setSelectedPage(page);
  }, [navigate]);

  return (
    <ThemeProvider theme={mdTheme}>
      <Box sx={{ display: "flex" }}>
        <CssBaseline />
        <AppBar
          open={open}
          selectedPage={selectedPage}
          toggleDrawer={toggleDrawer}
        />
        <Drawer
          open={open}
          toggleDrawer={toggleDrawer}
          selectedPage={selectedPage}
          handleSelectPage={handleSelectPage}
        />
        <Box
          component='main'
          sx={{
            backgroundColor: (theme) =>
              theme.palette.mode === "light"
                ? theme.palette.grey[100]
                : theme.palette.grey[900],
            flexGrow: 1,
            height: "100vh",
            overflow: "auto",
          }}
        >
          <Toolbar />
          <StyledEngineProvider injectFirst>
            <AppContent selectedPage={selectedPage} />
          </StyledEngineProvider>
        </Box>
      </Box>
    </ThemeProvider>
  );
}
