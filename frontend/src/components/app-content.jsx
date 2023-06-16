import React from "react";
import NewsPage from "./news-page";
import TradePage from "./trade/trade-page";
import TransactionsPage from "./transactions/transactions-page";
import PortfolioPage from "./portfolio/portfolio-page";
import Dashboard from "./dashboard/dashboard";
import Settings from "./settings";
import ErrorPage from "../routes/error-page";

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
