import React from "react";
import NewsPage from "./news-page";

export const AppContent = React.memo((props) => {
  if (props.selectedPage === "News") return <NewsPage />;
  return <div>{props.selectedPage}</div>;
});
