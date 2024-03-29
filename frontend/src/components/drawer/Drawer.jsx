import React from "react";
import MuiDrawer from "@mui/material/Drawer";
import { styled } from "@mui/material/styles";
import { DRAWER_WIDTH } from "../../data/constants.js";
import List from "@mui/material/List";
import Divider from "@mui/material/Divider";
import IconButton from "@mui/material/IconButton";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import {
  drawerMainListItems,
  drawerSecondaryListItems,
} from "../../data/drawerListItems.js";
import { DrawerListItem } from "./DrawerListItem.jsx";
import { Toolbar } from "@mui/material";

const DrawerStyled = styled(MuiDrawer, {
  shouldForwardProp: (prop) => prop !== "open",
})(({ theme, open }) => ({
  "& .MuiDrawer-paper": {
    position: "relative",
    whiteSpace: "nowrap",
    width: DRAWER_WIDTH,
    transition: theme.transitions.create("width", {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
    boxSizing: "border-box",
    ...(!open && {
      overflowX: "hidden",
      transition: theme.transitions.create("width", {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
      }),
      width: theme.spacing(7),
      [theme.breakpoints.up("sm")]: {
        width: theme.spacing(9),
      },
    }),
  },
}));

export const Drawer = React.memo((props) => {
  return (
    <DrawerStyled variant='permanent' open={props.open}>
      <Toolbar
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "flex-end",
          px: [1],
        }}
      >
        <IconButton onClick={props.toggleDrawer}>
          <ChevronLeftIcon />
        </IconButton>
      </Toolbar>
      <Divider />
      <List component='nav'>
        {drawerMainListItems.map((item, index) => (
          <DrawerListItem
            key={index}
            name={item}
            selectedPage={props.selectedPage}
            handleSelectPage={props.handleSelectPage}
          />
        ))}
      </List>
      <Divider />
      <List component='nav' sx={{ mt: "auto" }}>
        {drawerSecondaryListItems.map((item, index) => (
          <DrawerListItem
            key={index}
            name={item}
            selectedPage={props.selectedPage}
            handleSelectPage={props.handleSelectPage}
          />
        ))}
      </List>
    </DrawerStyled>
  );
});
