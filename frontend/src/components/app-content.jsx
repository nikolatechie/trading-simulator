import React from "react";
import NewsPage from "./news-page";
import { TradePage } from "./trade-page";

export const AppContent = React.memo((props) => {
  switch (props.selectedPage) {
    case "News":
      return <NewsPage />;
    case "Trade":
      return <TradePage />;
    default:
      return <div>{props.selectedPage}</div>;
  }
});
