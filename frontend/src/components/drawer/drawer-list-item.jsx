import React from "react";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import CurrencyExchangeIcon from "@mui/icons-material/CurrencyExchange";
import FeedIcon from "@mui/icons-material/Feed";
import WalletIcon from "@mui/icons-material/Wallet";
import QueryStatsIcon from "@mui/icons-material/QueryStats";
import DashboardIcon from "@mui/icons-material/Dashboard";
import SettingsIcon from "@mui/icons-material/Settings";
import LogoutIcon from "@mui/icons-material/Logout";

const iconMap = {
  Dashboard: <DashboardIcon />,
  Portfolio: <WalletIcon />,
  Trade: <QueryStatsIcon />,
  News: <FeedIcon />,
  Transactions: <CurrencyExchangeIcon />,
  Settings: <SettingsIcon />,
  "Sign out": <LogoutIcon />,
};

export const DrawerListItem = (props) => {
  const icon = iconMap[props.name];

  return (
    <ListItemButton onClick={() => props.handleSelectPage(props.name)}>
      <ListItemIcon>{icon}</ListItemIcon>
      <ListItemText primary={props.name} />
    </ListItemButton>
  );
};
