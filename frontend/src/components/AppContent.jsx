import React from "react";
import NewsPage from "./news/NewsPage";
import TradePage from "./trade/TradePage";
import TransactionsPage from "./transactions/TransactionsPage";
import PortfolioPage from "./portfolio/PortfolioPage";
import Dashboard from "./dashboard/Dashboard";
import Settings from "./Settings";
import ErrorPage from "../routes/ErrorPage";

export const AppContent = (props) => {
  switch (props.selectedPage) {
    case "Dashboard":
      return <Dashboard />;
    case "Portfolio":
      return <PortfolioPage />;
    case "Trade":
      return <TradePage />;
    case "News":
      return <NewsPage />;
    case "Transactions":
      return <TransactionsPage />;
    case "Settings":
      return <Settings />;
    default:
      return <ErrorPage />;
  }
};
