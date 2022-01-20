import React from 'react'
import { Header, Divider, Form, Button } from 'semantic-ui-react'
import styles from "../../styles/signup.module.css"
import Navbar from '../../src/component/Navbar'
import Link from 'next/link'


const HeaderExampleContent = () => (
  <>
  <Navbar/>
  <div className={ styles.headeralign }>
    <Header size='huge'>회원정보 수정</Header>
    <Divider />
    </div>
    <div className={ styles.formalign }>
    <Form>
    <Form.Field>
      <label>이메일</label>
      <br/>
    </Form.Field>
    <Form.Field>
      <label>비밀번호</label>
      <Button basic color='grey'>
      변경하기
    </Button>
    </Form.Field>
    <Form.Field>
      <label>기업명</label>
      <Button basic color='grey'>
      변경하기
    </Button>
    </Form.Field>
    <Form.Field>
      <label>닉네임</label>
      <Button basic color='grey'>
      변경하기
    </Button>
    </Form.Field>
    <Form.Field>
      <label>전화번호</label>
      <Link href="info/passwordedit">
      <Button basic color='grey'>
      변경하기
    </Button>
    </Link>
    </Form.Field>
    <Form.Field>
      <label>주소</label>
      <Button basic color='grey'>
      변경하기
    </Button>
    </Form.Field >

  </Form>
  </div>
  <br/>
 </>
)

export default HeaderExampleContent