import React, { useEffect, useReducer } from "react";
import ArticleCard from "./ArticleCard";
import { Alert, Box, CircularProgress } from "@mui/material";
import { BASE_API_URL, ENDPOINTS, ActionTypes } from "../../data/constants";

const initialState = {
  articles: [],
  page: 0,
  isLoading: false,
  hasMore: true,
  canFetchArticles: true,
};

const reducer = (state, action) => {
  switch (action.type) {
    case ActionTypes.FETCH_START:
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
        canFetchArticles: false,
      };
    case ActionTypes.INCREMENT_PAGE:
      return {
        ...state,
        page: state.page + 1,
      };
    default:
      throw new Error("Invalid reducer action type:", action.type);
  }
};

export default function NewsPage() {
  const [state, dispatch] = useReducer(reducer, initialState);

  useEffect(() => {
    const fetchNewsArticles = async () => {
      dispatch({ type: ActionTypes.FETCH_START });
      try {
        const token = localStorage.getItem("jwt");
        const response = await fetch(
          `${BASE_API_URL}${ENDPOINTS.NEWS}?page=${state.page}`,
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
      }
    };
    fetchNewsArticles();
  }, [state.page]);

  useEffect(() => {
    // Lazy loading
    const options = {
      rootMargin: "0px",
      threshold: 1.0,
    };

    const observer = new IntersectionObserver(([entry]) => {
      if (
        entry.isIntersecting &&
        state.hasMore &&
        !state.isLoading &&
        state.canFetchArticles
      ) {
        dispatch({ type: ActionTypes.INCREMENT_PAGE });
      }
    }, options);

    observer.observe(document.querySelector("#bottom-of-list"));

    return () => {
      observer.disconnect();
    };
  }, [state.hasMore, state.isLoading, state.canFetchArticles]);

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
      {!state.canFetchArticles && (
        <Alert severity='error'>
          Couldn't fetch news articles. Please try again later.
        </Alert>
      )}
      <div id='bottom-of-list' />
      {state.isLoading && <CircularProgress sx={{ mt: 2 }} />}
    </Box>
  );
}
