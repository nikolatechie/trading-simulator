import { Box, Typography } from '@mui/material';
import React from 'react';
import { formatDateTime } from '../../helpers/Helpers';

export default function CommentSection({ comments }) {
  return (
    <Box display="flex" flexDirection="column" gap={2} width="100%">
      {comments.length > 0 ? comments.map((comment, index) => (
        <Box key={index}>
          <Box display="flex" gap={1} alignItems="center">
            <Typography fontWeight="bold">{comment.author}</Typography>
            <Typography fontWeight="light" fontSize="0.9rem">
              ({formatDateTime(comment.postedDateTime)})
            </Typography>
          </Box>
          <Typography>{comment.content}</Typography>
        </Box>
      )) : <Typography fontStyle="italic" sx={{ opacity: "70%" }}>
        Be the first person to comment this article.
      </Typography>
      }
    </Box>
  );
};