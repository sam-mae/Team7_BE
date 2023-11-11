# Team7_BE

</br>
<img width="700" alt="스크린샷 2023-11-11 오전 5 20 44" src="https://github.com/Step3-kakao-tech-campus/Team7_BE/assets/131665728/e8df524e-6202-4056-af96-d0c50eadfb1a">
</br>
</br>

# 간단한 프로젝트 소개
</br>
<img width="700" alt="스크린샷 2023-11-11 오전 5 29 43" src="https://github.com/Step3-kakao-tech-campus/Team7_BE/assets/131665728/a9a9ca0a-7945-4b72-9594-e3352ccc5271">
</br>
</br>
</br>

**1. 개발 공부의 방향성 제시**
</br>
**2. 동일한 주제에 대한 TIL 공유 기능**
</br>
**3. 깃허브 업로드 자동화**
</br>
</br>

# 관련 주소

| 문서 | 
|:--------:|
| [7조 배포 주소](https://kc29be941feb6a.user-app.krampoline.com) |
| [API 문서](https://www.notion.so/API-d7c21dd77c1643348c98b01c8f3d9f2a) |
| [피그마](https://www.figma.com/file/CBibyBNZ1jmESyVs0jnjSt/3%EB%8B%A8%EA%B3%84-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%99%80%EC%9D%B4%EC%96%B4-%ED%94%84%EB%A0%88%EC%9E%84?type=design&node-id=0-1&mode=design&t=0h0155bB1sb2wp98-0) |
| [7조 노션](https://www.notion.so/2a6af605e8184499b21492cb7aabf6f5?v=0b907fed27634982ace606d37a4a6c88) |



# 기능 소개

<table>
  <tr>
    <th>기능</th>
    <th>설명</th>
  </tr>
  <tr>
    <td>로그인/회원가입</td>
    <td> gmail(smtp)와 redis를 사용하여 이메일 인증 시스템을 구현했지만, 배포환경 변화로 인해 크램폴린에 배포하지는 못함 </br> 로그인과 재발급 요청에 따른 access token, refresh token 발급을 통해 JWT 관리 </td>
  </tr>
  <tr>
    <td> 검색 기능 구현 </td>
    <td> 메인 페이지에서 TIL 제목으로 검색, 장미밭 클릭으로 특정 날짜의 TIL 조회, 로드맵 페이지에서 로드맵 이름으로 검색, 상세 페이지에서 구성원 이름으로 검색 기능 구현 </td>
  </tr>
  <tr>
    <td> 이미지 업로드 </td>
    <td> S3 구축과 연동을 통해 프로필 사진 업로드, TIL 사진 업로드, 삭제 기능 구현 (크램폴린에서 연동하지 못함) </td>
  </tr>
  <tr>
    <td> 알림 기능 구현 </td>
    <td> 자신이 제출한 TIL에 달린 댓글에 대한 알림 기능 구현 </td>
  </tr>
  <tr>
    <td> 댓글 기능 구현 </td>
    <td> 제출한 TIL에 댓글 작성과 수정, 삭제 기능 구현 </td>
  </tr>
  <tr>
    <td> 삭제 기능 구현 </td>
    <td> 삭제 기능을 Soft delete로 구현하여, 데이터 무결성을 유지하고, 삭제된 데이터의 이력을 추적하거나 데이터 분석에 활용할 수 있도록 함. </br> JPQL과 @Where을 이용하여 실제로 데이터가 삭제되지 않고 삭제된 것처럼 보이도록 구현. </td>
  </tr>
  <tr>
    <td> 예외 처리 </td>
    <td> 예외 코드를 ExceptionCode로 일괄 관리를 통해 유지 보수성와 중복 최소화 함 </td>
  </tr>
  <tr>
    <td> 권한 처리 </td>
    <td> 로드맵에서의 사용자의 역할(master, manager, member, none)에 맞는 권한 처리 </td>
  </tr>
  <tr>
    <td> 테스트 코드 작성  </td>
    <td> Junit을 이용하여 컨트룰러 단위 테스트를 진행하며 프로덕션 환경에서의 안전성 확보함 </br> </td>
  </tr>
  <tr>
    <td> 성능 개선 </td>
    <td> lazy fetching 전략과 fetch join 사용으로 N+1 문제 개선 및 쿼리 효율성을 증가시킴 </br> 또한 엔티티 간 양방향 연관 관계가 아닌 단방향 연관 관계 설정을 통해 불필요한 참조와 의존성을 줄임 </td>
  </tr>
  <tr>
    <td> 가독성 및 리팩토링 </td>
    <td> DTO를 class가 아닌 record로 구현해 depth를 줄이고 재사용성과 가독성을 높임 </td>
  </tr>
</table>

</br>
</br>

# 기술 스택

![java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![java](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![java](https://img.shields.io/badge/Amazon_AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)
![java](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![java](https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white)
</br>
</br>
</br>
</br>
</br>

# 아키택쳐 구조
<img width="800" alt="스크린샷 2023-11-11 오후 8 02 21" src="https://github.com/Step3-kakao-tech-campus/Team7_BE/assets/131665728/aff5dd73-0cc2-4da4-8e53-f838630b7afd">
</br>
</br>


# Til-y 팀원들

<table>
  <tr>
    <td>김동영</td>
    <td>조준서</td>
    <td>이한홍</td>
    <td>김수현</td>
    <td>이상명</td>
  </tr>
  <tr>
    <td><img src="https://github.com/ehddud1006.png" alt="김동영" width="100" height="100"></td>
    <td><img src="https://github.com/monsta-zo.png" alt="조준서" width="100" height="100"></td>
    <td><img src="https://github.com/hoyaii.png" alt="이한홍" width="100" height="100"></td>
    <td><img src="https://github.com/suuding.png" alt="김수현" width="100" height="100"></td>
    <td><img src="https://github.com/sam-mae.png" alt="이상명" width="100" height="100"></td>
  </tr>
  <tr>
    <td>FE</td>
    <td>FE</td>
    <td>BE</td>
    <td>BE</td>
    <td>BE</td>
  </tr>
  <tr>
    <td>조장</td>
    <td>기확리더</td>
    <td>테크리더</td>
    <td>스케줄러</td>
    <td>리마인더</td>
  </tr>
</table>
</br>

# ERD
![ERD_v2-2](https://github.com/Step3-kakao-tech-campus/Team7_BE/assets/131665728/aa678bea-6094-4aaf-aa0a-44938f42e745)
