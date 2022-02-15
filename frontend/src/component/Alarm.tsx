import axios from "axios";
import { useEffect, useState } from "react";
// import { Icon, Button, Image, Modal } from "semantic-ui-react";
import { Button, Header, Icon, Segment, TransitionablePortal } from "semantic-ui-react";
import styles from '../../styles/alarm.module.css'
import SockJS from "sockjs-client";
import Stomp from "webstomp-client";

export default function Alarm(){

  // const [open, setOpen] = useState(false)

  const [open, setOpen] = useState(false)

  function handleClick() {
    setOpen(true)
  }
  function handleClose() {
    setOpen(false)
  }
  const FromUserId = 2
  const toUserId = 1;
  const listUser = [FromUserId,toUserId];
  const message = "";

  useEffect(() => {
    // connect()
  })
  
  // useEffect(() => {
    // function follower(){
    //   axios
    //   // 메시지를 받아야함
    //   .get(`http://localhost:8080/follower/follower/`+ 19,{})
    //   .then(({ data }) => {
    //     stompClient.send(`/send/`+19+`/`+messages)

    //     console.log(data);
    //     // console.log(localStorage.getItem('token'));
    //     // connect();
    //     // send()
    //   })
    //   .catch((e: any) => {
    //     console.log(e)
    //   });
    // }
  // }, [])


  return (
    <>
      {/* <Modal
        open={open}
        onClose={() => setOpen(false)}
        onOpen={() => setOpen(true)}
        trigger={<Button><Icon name="bell"/>알림</Button>}
      >
        <Modal.Header>알림</Modal.Header>
        <Modal.Content image>
          <Modal.Description className={styles.alarm_content}>
            <h1></h1>
            <p>댓글 알림</p>
            <p>좋아요 알림</p>
            <p>팔로우 알림</p>
            <p>채팅 알림</p>
            
          </Modal.Description>
        </Modal.Content>
        <Modal.Actions>
          <Button primary onClick={() => setOpen(false)}>
            닫기
          </Button>
        </Modal.Actions>
      </Modal> */}
      <div>
        <Button
          content={open ? <><Icon name="bell"></Icon>알림닫기</> : <><Icon name="bell"></Icon>알림보기</>}
          // negative={open}
          // positive={!open}
          positive={true}
          // onClick={follower}
        />
        <TransitionablePortal onClose={handleClose} open={open}>
          <Segment
            style={{ right: '7%', position: 'fixed', top: '5%', zIndex: 1000 }}
            className={ styles.alarm_content }
          >
            <Header>알림</Header>
            <p>-------------------------------------------------------</p>
            <p>ㅁㅁㅁ 님이 작성한 글에 댓글이 달렸습니다.</p>
            <p>댓글 알림</p>
            <p>좋아요 알림</p>
            <p>팔로우 알림</p>
            <p>채팅 알림</p>
          </Segment>
        </TransitionablePortal>
      </div>
    </>
  );
}

