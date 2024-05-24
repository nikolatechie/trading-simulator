import React from "react";
import { Card, CardMedia, CardContent, Typography, Box } from "@mui/material";
import { makeStyles } from "@mui/styles";
import { formatDateTime } from "../../helpers/Helpers.jsx";
import ForumIcon from "@mui/icons-material/Forum";
import ThumbUpIcon from '@mui/icons-material/ThumbUp';

const useStyles = makeStyles({
  card: {
    width: "65%",
    paddingBottom: 10,
    marginBottom: 30,
    borderRadius: 0,
    transition: "box-shadow 0.4s, background-color 0.3s, width 0.3s",
    "&:hover": {
      boxShadow: "0px 0px 45px rgba(0, 0, 0, 0.4)"
    },
  },
  cardIcon: {
    transform: "scale(1.2)",
    transition: "0.3s",
    opacity: "75%",
    cursor: "pointer",
    "&:hover": {
      opacity: "100%"
    }
  }
});

export default function ArticleCard(props) {
  const classes = useStyles();

  const handleLikeClick = () => {
    console.log("like");
  }

  const handleCommentClick = () => {
    console.log("comment");
  }

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
      <Box display="flex" my={2} mx={3} gap={4} justifyContent="flex-end">
        <Box onClick={handleLikeClick}>
          <ThumbUpIcon className={classes.cardIcon} />
        </Box>
        <Box onClick={handleCommentClick}>
          <ForumIcon className={classes.cardIcon} />
        </Box>
      </Box>
    </Card>
  );
};
