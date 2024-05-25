import React, { useState } from "react";
import { Card, CardMedia, CardContent, Typography, Box } from "@mui/material";
import { makeStyles } from "@mui/styles";
import { formatDateTime } from "../../helpers/Helpers.jsx";
import ForumIcon from "@mui/icons-material/Forum";
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import { BASE_API_URL, ENDPOINTS } from '../../data/constants.js';
import CommentSection from './CommentSection.jsx';

const useStyles = makeStyles({
  card: {
    width: "65%",
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
    cursor: "pointer",
    "&:hover": {
      color: "green"
    }
  }
});

export default function ArticleCard(props) {
  const classes = useStyles();
  const [liked, setLiked] = useState(props.article.liked);
  const [likeCount, setLikeCount] = useState(props.article.likeCount);
  const [showComments, setShowComments] = useState(false);
  const [comments, setComments] = useState(props.article.comments);

  const handleLikeClick = async () => {
    try {
      const token = localStorage.getItem("jwt");
      const response = await fetch(`${BASE_API_URL}${ENDPOINTS.NEWS}?articleId=${props.article.id}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });
      if (response.ok) {
        if (liked) {
          setLikeCount(likeCount => likeCount - 1);
        } else {
          setLikeCount(likeCount => likeCount + 1);
        }
        setLiked((liked) => !liked);
      }
    } catch (error) {
      console.log(error);
    }
  };

  const addComment = (comment) => {
    setComments([...comments, comment]);
  }

  const handleCommentClick = () => {
    setShowComments(showComments => !showComments);
  };

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
        <Box display="flex" mt={4} mb={1} mx={2} gap={4} justifyContent="flex-end">
          <Box display="flex" gap={1.5} alignItems="center">
            <Box>
              <Typography fontSize="0.9rem">{likeCount} {likeCount === 1 ? "like" : "likes"}</Typography>
            </Box>
            <Box color={liked ? "green" : "initial"} onClick={handleLikeClick}>
              <ThumbUpIcon className={classes.cardIcon} />
            </Box>
          </Box>
          <Box display="flex" gap={1.5} alignItems="center">
            <Box color={showComments ? "green" : "initial"} onClick={handleCommentClick}>
              <ForumIcon className={classes.cardIcon} />
            </Box>
            <Box>
              <Typography fontSize="0.9rem">{comments.length} {comments.length === 1 ? "comment" : "comments"}</Typography>
            </Box>
          </Box>
        </Box>
        {showComments && <CommentSection comments={comments} articleId={props.article.id} addComment={addComment} />}
      </CardContent>
    </Card>
  );
};
