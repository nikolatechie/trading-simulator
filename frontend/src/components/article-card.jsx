import React from "react";
import { Card, CardMedia, CardContent, Typography } from "@mui/material";
import { MONTHS_SHORT } from "../data/constants";
import { makeStyles } from "@mui/styles";

const formatDateTime = (dateTime) => {
  const year = dateTime.substring(0, 4);
  const month = Number(dateTime.substring(5, 7));
  const day = dateTime.substring(8, 10);
  const hour = dateTime.substring(11, 13);
  const min = dateTime.substring(14, 16);
  return `${day}-${MONTHS_SHORT[month]}-${year} at ${hour}:${min}`;
};

const useStyles = makeStyles({
  card: {
    width: "65%",
    paddingBottom: 20,
    borderRadius: 0,
    transition: "box-shadow 0.4s, background-color 0.3s, width 0.3s",
    "&:hover": {
      width: "66%",
      boxShadow: "0px 0px 25px rgba(0, 0, 0, 0.2)",
      backgroundColor: "#f5f5f5",
    },
  },
});

export const ArticleCard = (props) => {
  const classes = useStyles();

  return (
    <Card className={classes.card}>
      <a href={props.article.url}>
        <CardMedia
          component='img'
          image={props.article.urlToImage}
          alt={props.article.title}
        />
      </a>
      <CardContent>
        <Typography variant='h5' component='h2'>
          <a href={props.article.url}>{props.article.title}</a>
        </Typography>
        <Typography color='textSecondary' gutterBottom>
          {props.article.author} | {formatDateTime(props.article.publishedAt)}
        </Typography>
        <Typography variant='body1' component='p'>
          {props.article.description}
        </Typography>
      </CardContent>
    </Card>
  );
};
