import Navbar from "../src/component/Navbar";
import Link from "next/link";
import { Button, Form, Grid, Segment, Image } from "semantic-ui-react";
import { useState } from "react";
import styles from "../styles/Login.module.css";
import axios from "axios";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const updateEmail = (event) => {
    setEmail(event.target.value);
  };

  const updatePassword = (event) => {
    setPassword(event.target.value);
  };

  function GoLogin() {
    axios
      .post("http://localhost:8080/user/login", {
        userId: email,
        user_password: password,
      })
      .then(({ data }) => {
        alert("로그인 성공");
        console.log(data);
      })
      .catch(() => {
        alert("로그인 실패");
      });
  }
  return (
    <>
      <Navbar />
      <div>
        <Segment placeholder>
          <br />
          <br />
          <br />
          <Grid columns={2} relaxed="very" stackable>
            <Grid.Column verticalAlign="middle">
              <Image src="/images/로고.png" size="huge" />
            </Grid.Column>

            <Grid.Column className={styles.location}>
              <Form>
                <Form.Input
                  icon="user"
                  iconPosition="left"
                  label="User Eamil"
                  placeholder="User Email"
                  onChange={updateEmail}
                />
                <Form.Input
                  icon="lock"
                  iconPosition="left"
                  label="Password"
                  type="password"
                  onChange={updatePassword}
                />
                <Button content="Login" onClick={GoLogin} primary />
              </Form>
              <br />
              <br />
              <br />
              <hr />
              <br />
              <br />
              <br />
              <div className={styles.guide_location}>
                <p>
                  아이디가 없으신가요?
                  <Link href="/signup">
                    <a>--- 회원가입 ---</a>
                  </Link>
                </p>
                <p>
                  아이디가 기억이 안나요
                  <Link href="/signup">
                    <a>--- 아이디 찾기 ---</a>
                  </Link>
                </p>
                <p>
                  비밀번호가 기억이 안나요
                  <Link href="/signup">
                    <a>--- 비밀번호 찾기 ---</a>
                  </Link>
                </p>
              </div>
            </Grid.Column>
          </Grid>
        </Segment>
      </div>
    </>
  );
}