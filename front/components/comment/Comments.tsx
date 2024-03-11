'use client';

import { useEffect, useState } from 'react';
import { CommentItem } from '@/data/CommentItem';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Divider from '@mui/material/Divider';

async function fetchComment(id: number): Promise<{ data: CommentItem[] }> {
  const data = await fetch(`http://localhost:8080/api/v1/contents/${id}/comments`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8',
    },
  });
  return data.json();
}
const Comments = ({ contentId, refreshComments }) => {
  const [comments, setComments] = useState<CommentItem[]>([]);
  useEffect(() => {
    const commentData = async (contentId: number) => {
      const response = await fetchComment(contentId);
      setComments(response.data);
    };

    commentData(contentId);
  }, [contentId, refreshComments]);

  return (
    <List sx={{ width: '100%', maxWidth: 1500, bgcolor: 'background.paper' }}>
      {comments.map((item, index) => (
        <div key={index}>
          <ListItem alignItems="flex-start">
            <ListItemText primary={item.comment.comment} />
          </ListItem>
          {index !== comments.length - 1 && <Divider variant="middle" component="li" />}
        </div>
      ))}
    </List>
  );
};

export default Comments;
