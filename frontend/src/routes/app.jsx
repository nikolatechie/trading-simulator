import React from "react";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import { useState } from "react";
import { AppBar } from "../components/app-bar.jsx";
import { Drawer } from "../components/drawer.jsx";
import { AppContent } from "../components/app-content.jsx";

const mdTheme = createTheme();

export default function App() {
  const [open, setOpen] = useState(true);
  const [selectedPage, setSelectedPage] = useState("Dashboard");

  const toggleDrawer = () => {
    setOpen(!open);
  };

  const handleSelectPage = (page) => {
    setSelectedPage(page);
  };

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
          <AppContent selectedPage={selectedPage} />
        </Box>
      </Box>
    </ThemeProvider>
  );
}
