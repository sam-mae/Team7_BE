package com.example.tily.roadmap;

import com.example.tily._core.errors.exception.Exception404;
import com.example.tily.roadmap.relation.GroupRole;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.roadmap.relation.UserRoadmapRepository;
import com.example.tily.step.Step;
import com.example.tily.step.StepRepository;
import com.example.tily.step.reference.Reference;
import com.example.tily.step.reference.ReferenceRepository;
import com.example.tily.step.relation.UserStep;
import com.example.tily.step.relation.UserStepRepository;
import com.example.tily.til.Til;
import com.example.tily.til.TilRepository;
import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RoadmapService {
    private final RoadmapRepository roadmapRepository;
    private final StepRepository stepRepository;
    private final ReferenceRepository referenceRepository;
    private final TilRepository tilRepository;
    private final UserRoadmapRepository userRoadmapRepository;
    private final UserStepRepository userStepRepository;

    @Transactional
    public RoadmapResponse.CreateRoadmapDTO createIndividualRoadmap(RoadmapRequest.CreateIndividualRoadmapDTO requestDTO, User user){

        Roadmap roadmap = Roadmap.builder()
                .creator(user)
                .category(Category.CATEGORY_INDIVIDUAL)
                .name(requestDTO.getName())
                .stepNum(0L)
                .build();
        roadmapRepository.save(roadmap);

        UserRoadmap userRoadmap = UserRoadmap.builder()
                .roadmap(roadmap)
                .user(user)
                .role(GroupRole.ROLE_MASTER)
                .build();
        userRoadmapRepository.save(userRoadmap);

        return new RoadmapResponse.CreateRoadmapDTO(roadmap);
    }

    @Transactional
    public RoadmapResponse.CreateRoadmapDTO createGroupRoadmap(RoadmapRequest.CreateGroupRoadmapDTO requestDTO, User user){

        Roadmap roadmap = Roadmap.builder()
                .creator(user)
                .category(Category.CATEGORY_GROUP)
                .name(requestDTO.getRoadmap().getName())
                .description(requestDTO.getRoadmap().getDescription())
                .isPublic(requestDTO.getRoadmap().getIsPublic())
                .currentNum(1L)
                .code(generateRandomCode())
                .isRecruit(true)
                .stepNum((long) requestDTO.getSteps().size())
                .build();
        roadmapRepository.save(roadmap);

        List<RoadmapRequest.CreateGroupRoadmapDTO.StepDTO> stepDTOS = requestDTO.getSteps();

        for(RoadmapRequest.CreateGroupRoadmapDTO.StepDTO stepDTO : stepDTOS){
            // step 저장
            String title = stepDTO.getTitle() ;
            String stepDescription = stepDTO.getDescription();

            Step step = Step.builder().roadmap(roadmap).title(title).description(stepDescription).build();
            stepRepository.save(step);

            // reference 저장
            RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs referenceDTOs = stepDTO.getReferences();

            // (1) youtube
            List<RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO> youtubeDTOs = referenceDTOs.getYoutube();
            for(RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO youtubeDTO : youtubeDTOs){
                String link = youtubeDTO.getLink();

                Reference reference = Reference.builder().step(step).category("youtube").link(link).build();
                referenceRepository.save(reference);
            }

            // (2) reference
            List<RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO> webDTOs = referenceDTOs.getWeb();
            for(RoadmapRequest.CreateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO webDTO : webDTOs){
                String link = webDTO.getLink();

                Reference reference = Reference.builder().step(step).category("web").link(link).build();
                referenceRepository.save(reference);
            }
        }

        UserRoadmap userRoadmap = UserRoadmap.builder()
                .roadmap(roadmap)
                .user(user)
                .role(GroupRole.ROLE_MASTER)
                .progress(0)
                .build();
        userRoadmapRepository.save(userRoadmap);

        return new RoadmapResponse.CreateRoadmapDTO(roadmap);
    }

    public RoadmapResponse.FindGroupRoadmapDTO findGroupRoadmap(Long id, User user){
        Roadmap roadmap = roadmapRepository.findById(id).orElseThrow(
                () -> new Exception404("해당 로드맵을 찾을 수 없습니다")
        );

        List<Step> stepList = stepRepository.findByRoadmapId(id);

        Map<Long, List<Reference>> youtubeMap = new HashMap<>();
        Map<Long, List<Reference>> webMap = new HashMap<>();

        for(Step step : stepList){
            List<Reference> referenceList = referenceRepository.findByStepId(step.getId());

            List<Reference> youtubeList = new ArrayList<>();
            List<Reference> webList = new ArrayList<>();

            for(Reference reference : referenceList){
                if(reference.getCategory().equals("youtube")){
                    youtubeList.add(reference);
                }
                else if(reference.getCategory().equals("web")){
                    webList.add(reference);
                }
            }

            youtubeMap.put(step.getId(), youtubeList);
            webMap.put(step.getId(), webList);
        }

        Til latestTil = tilRepository.findFirstByOrderBySubmitDateDesc();
        Long recentTilId = latestTil != null ? latestTil.getId() : null;

        return new RoadmapResponse.FindGroupRoadmapDTO(roadmap, stepList, youtubeMap, webMap, user, recentTilId);
    }

    @Transactional
    public void updateGroupRoadmap(Long id, RoadmapRequest.UpdateGroupRoadmapDTO requestDTO){
        // 로드맵 업데이트
        Roadmap roadmap = roadmapRepository.findById(id).orElseThrow(
                () -> new Exception404("해당 로드맵을 찾을 수 없습니다")
        );

        String name = requestDTO.getRoadmap().getName();
        String description = requestDTO.getRoadmap().getDescription();
        String code = requestDTO.getRoadmap().getCode();
        Boolean isPublic = requestDTO.getRoadmap().getIsPublic();
        Boolean isRecruit = requestDTO.getRoadmap().getIsRecruit();

        roadmap.update(name, description, code, isPublic, isRecruit);

        // 스텝 업데이트
        List<RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO> stepDTOs = requestDTO.getSteps();

        for(RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO stepDTO : stepDTOs){
            Step step;

            step = stepRepository.findById(stepDTO.getId()).orElseThrow(
                    () -> new Exception404("해당 스텝을 찾을 수 없습니다.")
            );

            String title = stepDTO.getTitle() ;
            String stepDescription = stepDTO.getDescription();

            step.update(title,stepDescription);

            // reference 업데이트
            List<RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO> referenceDTOs = new ArrayList<>();
            referenceDTOs.addAll(stepDTO.getReferences().getWeb());
            referenceDTOs.addAll(stepDTO.getReferences().getYoutube());

            for(RoadmapRequest.UpdateGroupRoadmapDTO.StepDTO.ReferenceDTOs.ReferenceDTO referenceDTO : referenceDTOs){
                Reference reference;

                reference = referenceRepository.findById(referenceDTO.getId()).orElseThrow(
                        () -> new Exception404("해당 참조를 찾을 수 없습니다")
                );

                String link = referenceDTO.getLink();

                reference.update(link);
            }
        }
    }

    private static String generateRandomCode() {
        String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";

        String combinedChars = upperAlphabet + lowerAlphabet + numbers;

        Random random = new Random();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(combinedChars.length());
            char randomChar = combinedChars.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    @Transactional
    public RoadmapResponse.FindAllMyRoadmapDTO findAllMyRoadmaps(User user) {

        List<Roadmap> roadmaps = userRoadmapRepository.findByUserId(user.getId(), true);      // 내가 속한 로드맵 조회
        return new RoadmapResponse.FindAllMyRoadmapDTO(roadmaps);
    }

    @Transactional
    public RoadmapResponse.FindRoadmapByQueryDTO findAll(String category, String name, int page, int size) {

        // 생성일자를 기준으로 내림차순
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Slice<Roadmap> roadmaps = roadmapRepository.findAllByOrderByCreatedDateDesc(Category.getCategory(category), name, pageable);
        return new RoadmapResponse.FindRoadmapByQueryDTO(Category.getCategory(category), roadmaps);
    }

    @Transactional
    public void applyRoadmap(RoadmapRequest.ApplyRoadmapDTO requestDTO, Long id, User user){
        Roadmap roadmap = roadmapRepository.findById(id).
                orElseThrow(() -> new Exception404("해당 로드맵을 찾을 수 없습니다"));

        UserRoadmap userRoadmap = UserRoadmap.builder()
                .roadmap(roadmap)
                .user(user)
                .role(GroupRole.ROLE_MEMBER)
                .content(requestDTO.getContent())
                .isAccept(false)
                .progress(0)
                .build();

        userRoadmapRepository.save(userRoadmap);
    }

    @Transactional
    public RoadmapResponse.ParticipateRoadmapDTO participateRoadmap(RoadmapRequest.ParticipateRoadmapDTO requestDTO, User user){
        String code = requestDTO.getCode();
        Roadmap roadmap = roadmapRepository.findByCode(code)
                .orElseThrow(() -> new Exception404("해당 로드맵을 찾을 수 없습니다"));

        UserRoadmap userRoadmap = UserRoadmap.builder()
                .roadmap(roadmap)
                .user(user)
                .role(GroupRole.ROLE_MEMBER)
                .isAccept(true)
                .progress(0)
                .build();

        userRoadmapRepository.save(userRoadmap);

        return new RoadmapResponse.ParticipateRoadmapDTO(roadmap);
    }

    @Transactional
    public RoadmapResponse.FindRoadmapMembersDTO findRoadmapMembers(Long groupsId){
        List<UserRoadmap> userRoadmaps = userRoadmapRepository.findByRoadmap_IdAndIsAcceptTrue(groupsId);

        return new RoadmapResponse.FindRoadmapMembersDTO(userRoadmaps);
    }

    @Transactional
    public void changeMemberRole(RoadmapRequest.ChangeMemberRoleDTO requestDTO, Long groupsId, Long membersId){
        UserRoadmap userRoadmap = userRoadmapRepository.findByRoadmap_IdAndUser_IdAndIsAcceptTrue(groupsId, membersId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다"));

        userRoadmap.updateRole(requestDTO.getRole());
    }

    @Transactional
    public void dismissMember(Long groupsId, Long membersId){
        UserRoadmap userRoadmap = userRoadmapRepository.findByRoadmap_IdAndUser_IdAndIsAcceptTrue(groupsId, membersId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다"));

        userRoadmap.updateRole(GroupRole.ROLE_NONE);
    }

    @Transactional
    public RoadmapResponse.FindAppliedUsersDTO findAppliedUsers(Long groupsId, User user){
        List<UserRoadmap> userRoadmaps = userRoadmapRepository.findByRoadmap_IdAndIsAcceptFalse(groupsId);

        // 해당 페이지로 들어온 사용자 찾기
        UserRoadmap currentUser = userRoadmapRepository.findByRoadmap_IdAndUser_IdAndIsAcceptTrue(groupsId, user.getId())
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다"));

        return new RoadmapResponse.FindAppliedUsersDTO(userRoadmaps, currentUser.getRole());
    }

    @Transactional
    public void acceptApplication(Long groupsId, Long membersId){
        UserRoadmap userRoadmap = userRoadmapRepository.findByRoadmap_IdAndUser_IdAndIsAcceptFalse(groupsId, membersId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다"));

        userRoadmap.updateIsAccept(true);
    }

    @Transactional
    public void rejectApplication(Long groupsId, Long membersId){
        UserRoadmap userRoadmap = userRoadmapRepository.findByRoadmap_IdAndUser_IdAndIsAcceptFalse(groupsId, membersId)
                .orElseThrow(() -> new Exception404("해당 사용자를 찾을 수 없습니다"));

        userRoadmap.updateRole(GroupRole.ROLE_NONE);
    }

    @Transactional
    public RoadmapResponse.FindTilOfStepDTO findTilOfStep(Long groupsId, Long stepsId, Boolean isSubmit, Boolean isMember, String name){
        List<UserRoadmap> userRoadmaps = userRoadmapRepository.findByRoadmap_Id(groupsId);
        List<UserStep> userSteps = userStepRepository.findByStep_Id(stepsId);

        List<User> userRoadmapUsers = userRoadmaps.stream()
                .filter(userRoadmap -> Objects.equals(GroupRole.ROLE_MEMBER.equals(userRoadmap.getRole()), isMember))
                .map(UserRoadmap::getUser)
                .collect(Collectors.toList());

        List<User> userStepUsers = userSteps.stream()
                .filter(userStep -> Objects.equals(userStep.getIsSubmit(), isSubmit))
                .map(UserStep::getUser)
                .collect(Collectors.toList());

        List<User> users = userStepUsers.stream()
                .filter(userRoadmapUsers::contains)
                .distinct()
                .collect(Collectors.toList());

        if(name != null){
            users = users.stream()
                    .filter(user -> user.getName().equals(name))
                    .collect(Collectors.toList());
        }

        List<Pair<Til, User>> pairs = new ArrayList<>();

        for(User user : users){ // stream으로 해보니 가독성이 떨어져서, for문을 선택
            List<Til> tils = tilRepository.findByWriter_Id(user.getId());

            for(Til til : tils){
                Pair<Til, User> pair = Pair.of(til, user);
                pairs.add(pair);
            }
        }
        return new RoadmapResponse.FindTilOfStepDTO(pairs);
    }
}
