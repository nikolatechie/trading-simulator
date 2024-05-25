import React, { useState } from 'react';
import { Box, Button, TextField, Typography } from '@mui/material';
import { formatDateTime } from '../../helpers/Helpers';
import PersonIcon from '@mui/icons-material/Person';
import { BASE_API_URL, ENDPOINTS } from '../../data/constants';

export default function CommentSection(props) {
  const [comment, setComment] = useState("");

  const getFullName = async () => {
    const token = localStorage.getItem("jwt");
    try {
      const response = await fetch(`${BASE_API_URL}${ENDPOINTS.USER_FULL_NAME}`, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        }
      });
      if (response.ok) {
        const data = await response.json();
        return data.fullName;
      }
      return null;
    } catch (error) {
      console.log(error);
      return null;
    }
  }

  const addComment = async (event) => {
    event.preventDefault();
    if (comment.length < 1) {
      alert("A comment must have a length between 1 and 1000.");
      return;
    }

    const token = localStorage.getItem("jwt");
    try {
      const response = await fetch(`${BASE_API_URL}${ENDPOINTS.ADD_COMMENT}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          articleId: props.articleId,
          content: comment
        })
      });
      if (response.ok) {
        const author = await getFullName();
        if (author === null) {
          throw new Error("An error occurred while fetching user's full name.");
        }
        props.addComment({ author, content: comment, postedDateTime: new Date().toISOString() });
        setComment("");
        alert("Your comment has been posted!");
      } else {
        const data = await response.json();
        throw new Error(data.errorMessage);
      }
    } catch (error) {
      console.log(error);
      alert(error);
    }
  };

  const handleCommentChange = (event) => {
    setComment(event.target.value);
  };

  return (
    <Box display="flex" flexDirection="column" gap={2} width="100%">
      <Box display="flex" flexDirection="column" component="form" noValidate onSubmit={addComment} gap={1} mb={2}>
        <TextField
          label="Add a comment"
          value={comment}
          onChange={handleCommentChange}
          name="content"
          multiline
          rows={3}
          variant="outlined"
          fullWidth
        />
        <Button type="submit" variant="contained" sx={{ width: "5rem" }}>Add</Button>
      </Box>
      {props.comments.length > 0 ?
        <>
          <Typography variant='h5' fontWeight="bold" my={1}>
            {props.comments.length} {props.comments.length === 1 ? "comment" : "comments"}
          </Typography>
          {props.comments.map((comment, index) => (
            <Box key={index} display="flex" gap={2} alignItems="center">
              <PersonIcon fontSize="large" />
              <Box>
                <Box display="flex" gap={1} alignItems="center">
                  <Typography fontWeight="bold">{comment.author}</Typography>
                  <Typography fontWeight="light" fontSize="0.9rem">
                    ({formatDateTime(comment.postedDateTime)})
                  </Typography>
                </Box>
                <Typography>{comment.content}</Typography>
              </Box>
            </Box>
          ))}
        </> : <Typography fontStyle="italic" sx={{ opacity: "70%" }}>
          Be the first person to comment this article.
        </Typography>
      }
    </Box>
  );
};