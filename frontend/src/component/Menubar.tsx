import { Header, Menu, MenuItem, Icon, Divider, Grid, GridRow, Image } from 'semantic-ui-react'
import Link from 'next/link'
import IsLogin from '../lib/customIsLogin'
// import GetUserData from 'src/lib/getUserData'
import { useEffect, useState } from 'react'
import userAxios from 'src/lib/userAxios'
import { useRouter } from 'next/router';


export default function Menuvar(){

  const isLogin = IsLogin
  const [userInfo, setUserInfo] = useState(null)
  const router = useRouter()

  function goProfile(){
    if (userInfo) {
      router.push(`/profile/${userInfo['userSeq']}`)
    }
  }

  useEffect(() => {
    if (isLogin) {
      userAxios
      .get(`/api/v1/users`, {

      })
      .then(({ data }) => {
        setUserInfo(data.body.user)
      })
      .catch((e: any) => {
        console.log(e)
        // alert("잘못된 접근입니다.");
      });
    }
  }, [])

  // let userData = null;
  // const tester = getUserData
  // console.log('test', tester)

  // async function loadUserData() {
  //   const pending = GetUserData();
  //   const result = await pending;
  //   return result;
  // }

  // useEffect(() => {
  //   userData = loadUserData();
  //   console.log('값이 있어야됨', userData)
  // }, [])

  

  return (
    <>
      <Menu vertical>
        <MenuItem>
          <Grid>
            <GridRow>
              {/* {userInfo?
                <img src={userInfo['profileImageUrl']} alt="프로필사진" width="50px" onClick={goProfile} style={{cursor: "pointer", textShadow:"123"}} title="내 프로필로 이동합니다."/>
              :
              <img src="/images/디폴트프로필사진.png" alt="프로필사진" width="50px"/>
              }
              <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
              {userInfo?
              userInfo['username']:
              "비회원"}
              <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
              {userInfo?
              `${userInfo['suninDays']}일`:
              "0일"}

              {userInfo?
              <>
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
              <Link href="/info"><a title="회원정보 수정 페이지로 이동합니다."><Icon name='cog' size='large' color='grey' circular/></a></Link>
              </>
              :
              ""
              } */}
              {userInfo?
              <>
                <img src={userInfo['profileImageUrl']} alt="프로필사진" width="50px" onClick={goProfile} style={{cursor: "pointer", textShadow:"123"}} title="내 프로필로 이동합니다."/>
                <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                {userInfo['username']}
                <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
                {userInfo['suninDays']}일
                <Image 
                  src={
                    userInfo['suninDays']<3?"/images/suninimage/씨앗.png":
                    userInfo['suninDays']<6?"/images/suninimage/새싹.jpg":
                    userInfo['suninDays']<9?"/images/suninimage/봉오리.jpg":
                    userInfo['suninDays']<12?"/images/suninimage/꽃.png":
                    "/images/suninimage/나무.png"
                    } width="50px" title="씨앗->새싹->봉오리->꽃->나무"/>
                {/* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; */}
                <Link href="/info"><a title="회원정보 수정 페이지로 이동합니다."><Icon name='cog' size='large' color='grey' circular/></a></Link>
              </>
              :
              <>
                <img src="/images/디폴트프로필사진.png" alt="프로필사진" width="50px"/>
                <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                비회원
                <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                0일 
              </>}
              
            </GridRow>
          </Grid>
          
        </MenuItem>
        <Menu.Item
          name='personal'
        >
          <Header as='h4'><Link href="/feed/personal"><a>개인</a></Link></Header>
        </Menu.Item>

        <Menu.Item
          name='company'
        >
          <Header as='h4'><Link href="/feed/company"><a>기업</a></Link></Header>
        </Menu.Item>

        <Menu.Item
          name='rank'
        >
          <Header as='h4'><Link href="/rank"><a>랭킹</a></Link></Header>
        </Menu.Item>
        {isLogin?
        <Menu.Item
        name='createfeed'
      >
        <Header as='h4'><Link href="/feed/create"><a>피드 작성하기</a></Link></Header>
      </Menu.Item>
        :''}
        
        {isLogin?
        <Menu.Item
        name='chat'
      >
        <Header as='h4'>채팅</Header>
      </Menu.Item>
        :''} 
        </Menu>
    </>
  );
}