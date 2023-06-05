import React from "react";
import NewsPage from "./news-page";
import TradePage from "./trade/trade-page";
import TransactionsPage from "./transactions-page";
import PortfolioPage from "./portfolio/portfolio-page";
import Dashboard from "./dashboard/dashboard";

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
    default:
      return <div>{props.selectedPage}</div>;
  }
};
