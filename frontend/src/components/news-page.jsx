import React, { useEffect, useReducer } from "react";
import { ArticleCard } from "./article-card";
import { Box, CircularProgress } from "@mui/material";
import { ActionTypes } from "../data/constants";

const initialState = {
  articles: [],
  page: -1,
  isLoading: false,
  hasMore: true,
};

function reducer(state, action) {
  switch (action.type) {
    case ActionTypes.FETCH_REQUEST:
      return { ...state, isLoading: true };
    case ActionTypes.FETCH_SUCCESS:
      return {
        ...state,
        articles: [...state.articles, ...action.payload.content],
        hasMore: !action.payload.last,
        isLoading: false,
      };
    case ActionTypes.FETCH_FAILURE:
      return {
        ...state,
        isLoading: false,
      };
    case ActionTypes.INCREMENT_PAGE:
      return {
        ...state,
        page: state.page + 1,
      };
    default:
      throw new Error("Invalid reducer action type:", action.type);
  }
}

export default function NewsPage() {
  const [state, dispatch] = useReducer(reducer, initialState);

  useEffect(() => {
    const fetchNewsArticles = async () => {
      dispatch({ type: ActionTypes.FETCH_REQUEST });
      try {
        const token = localStorage.getItem("jwt");
        const response = await fetch(
          `http://localhost:8080/api/news?page=${state.page}&size=10`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        const data = await response.json();
        if (response.ok) {
          dispatch({ type: ActionTypes.FETCH_SUCCESS, payload: data });
        } else {
          alert(data.errorMessage);
          dispatch({ type: ActionTypes.FETCH_FAILURE });
        }
      } catch (err) {
        console.log(err);
        dispatch({ type: ActionTypes.FETCH_FAILURE });
        alert("Couldn't fetch news articles. Please try again later.");
      }
    };
    if (state.page >= 0) fetchNewsArticles();
  }, [state.page]);

  useEffect(() => {
    // lazy loading
    const options = {
      rootMargin: "0px",
      threshold: 1.0,
    };

    const observer = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting && state.hasMore) {
        dispatch({ type: ActionTypes.INCREMENT_PAGE });
      }
    }, options);

    observer.observe(document.querySelector("#bottom-of-list"));

    return () => {
      observer.disconnect();
    };
  }, [state.hasMore]);

  return (
    <Box
      sx={{
        my: 5,
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
      }}
    >
      {state.articles.map((article) => (
        <ArticleCard key={article.id} article={article} />
      ))}
      <div id='bottom-of-list' />
      {state.isLoading && <CircularProgress sx={{ mt: 2 }} />}
    </Box>
  );
}
