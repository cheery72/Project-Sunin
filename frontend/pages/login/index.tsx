import Navbar from "../../src/component/Navbar";
import Link from "next/link";
import { Button, Form, Grid, Segment, Image, Header } from "semantic-ui-react";
import { useState } from "react";
import styles from "../../styles/Login.module.css";
import axios from "axios";
import { useRouter } from "next/router";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const updateEmail = (event: any) => {
    setEmail(event.target.value);
  };

  const updatePassword = (event: any) => {
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

  const router = useRouter()

  function goNaverLogin(){
    router.push('/login/naver')
  }

  function goGoogleLogin(){
    router.push('/login/google')
  }

  function goKakaoLogin(){
    router.push('/login/kakao')
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
              <Header textAlign="center">sns로 간편 로그인</Header> <br />
              <Image src="images/btnG_완성형.png" size="medium" onClick={goNaverLogin} className={ styles.social_login_button }/> <br />
              <Image src="images/btn_google_signin_light_normal_web.png" size="medium" onClick={goGoogleLogin} className={ styles.social_login_button }/> <br />
              <Image src="images/kakao_login_large_narrow.png" size="medium" onClick={goKakaoLogin} className={ styles.social_login_button }/>
              {/* <Form>
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
                <br />
                <Button color="green" onClick={goNaverLogin}>네이버로 로그인하기</Button><br />
                
                <Button color="google plus" onClick={goGoogleLogin}>구글로 로그인하기</Button><br />
                <Button color="yellow" onClick={goKakaoLogin}>카카오로 로그인하기</Button>
              </Form> */}
              {/* <br />
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
              </div> */}
            </Grid.Column>
          </Grid>
        </Segment>
      </div>
    </>
  );
}