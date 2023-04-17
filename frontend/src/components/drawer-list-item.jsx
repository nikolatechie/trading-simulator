import React from "react";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import CurrencyExchangeIcon from "@mui/icons-material/CurrencyExchange";
import FeedIcon from "@mui/icons-material/Feed";
import SwapHorizIcon from "@mui/icons-material/SwapHoriz";
import WalletIcon from "@mui/icons-material/Wallet";
import QueryStatsIcon from "@mui/icons-material/QueryStats";
import DashboardIcon from "@mui/icons-material/Dashboard";

const iconMap = {
  Dashboard: <DashboardIcon />,
  Research: <QueryStatsIcon />,
  Portfolio: <WalletIcon />,
  Trade: <SwapHorizIcon />,
  News: <FeedIcon />,
  Transactions: <CurrencyExchangeIcon />,
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
