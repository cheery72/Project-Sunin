import Router from "next/router";
import { useState } from "react";
import { List, Image, Icon, Label, Input } from "semantic-ui-react";
import allAxios from "src/lib/allAxios";
import Comment from "./Comment";

function Comments({ list, userSeq, feedId }: any) {
  const [comment, setComment] = useState("");

  const handleComment = (e: any) => {
    setComment(e.target.value);
  };

  const writeComment = () => {
    const body = new FormData();
    body.append("content", comment);
    body.append("feedId", feedId);
    body.append("writer", userSeq);

    allAxios
      .post(`/comment`, body)
      .then(() => {
        Router.reload();
      })
      .catch(() => {
        alert("잠시 후 다시 시도해주세요.");
      });
    setComment("");
  };

  console.log(userSeq);

  return (
    <>
      {userSeq != undefined && (
        <Input
          fluid
          className="commentInput"
          placeholder="Comment"
          icon="comment"
          iconPosition="left"
          value={comment}
          onChange={handleComment}
          action={{
            icon: "send",
            onClick: writeComment,
          }}
        />
      )}

      <List relaxed="very" divided verticalAlign="middle">
        {Object.entries(list).map((object: any) => {
          return (
            <List.Item key={object}>
              <Comment item={object} userSeq={userSeq} feedId={feedId} />
            </List.Item>
          );
        })}
      </List>
    </>
  );
}

export default Comments;
