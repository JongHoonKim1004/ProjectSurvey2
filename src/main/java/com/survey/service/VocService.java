package com.survey.service;

import com.survey.dto.ReplyDTO;
import com.survey.dto.VOC_DTO;
import com.survey.dto.VocReplyDTO;
import com.survey.entity.Reply;
import com.survey.entity.VOC;
import com.survey.repository.ReplyRepository;
import com.survey.repository.UsersRepository;
import com.survey.repository.VOCRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class VocService {
    @Autowired
    private VOCRepository vocRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ReplyService replyService;

    // Convert DTO to Entity
    public VOC convertDTO(VOC_DTO dto){
        VOC voc = new VOC();
        if(dto.getVocId() != null){
            voc.setVocId(dto.getVocId());
        }
        voc.setTitle(dto.getTitle());
        voc.setContent(dto.getContent());
        voc.setWriter(usersRepository.findByUsersId(dto.getWriter()));
        if(dto.getRegDate() != null){
            voc.setRegDate(dto.getRegDate());
        } else {
            voc.setRegDate(LocalDateTime.now());
        }
        if(dto.getSurveyId() != null){
            voc.setSurveyId(dto.getSurveyId());
        }
        return voc;
    }

    // Convert Entity to DTO
    public VOC_DTO convertEntity(VOC voc){
        VOC_DTO dto = new VOC_DTO();
        dto.setVocId(voc.getVocId());
        dto.setTitle(voc.getTitle());
        dto.setContent(voc.getContent());
        dto.setWriter(voc.getWriter().getUsersId());
        dto.setRegDate(voc.getRegDate());
        dto.setSurveyId(voc.getSurveyId());
        if(replyRepository.findByVocId(vocRepository.findByVocId(dto.getVocId())) != null){
            dto.setReply(true);
        } else dto.setReply(false);

        return dto;
    }

    // Create
    @Transactional
    public VOC_DTO save(VOC_DTO dto){
        VOC voc = convertDTO(dto);
        VOC saved = vocRepository.save(voc);
        log.info("VOC saved: {}", saved.getVocId());

        return convertEntity(saved);
    }

    // Read
    // Get One
    public VOC_DTO findByVocId(String vocId){
        VOC voc = vocRepository.findByVocId(vocId);
        VOC_DTO dto = convertEntity(voc);
        return dto;
    }

    // For Customer
    public List<VOC_DTO> findByWriter(String writer){
        List<VOC_DTO> vocDTOList = new ArrayList<>();
        List<VOC> vocList = vocRepository.findByWriter(usersRepository.findByUsersId(writer));
        for(VOC voc : vocList){
            VOC_DTO dto = convertEntity(voc);
            vocDTOList.add(dto);
        }

        return vocDTOList;
    }



    // For Admin
    public List<VOC_DTO> findAll(){
        List<VOC_DTO> vocDTOList = new ArrayList<>();
        List<VOC> vocList = vocRepository.findAll();
        for(VOC voc : vocList){
            VOC_DTO dto = convertEntity(voc);
            vocDTOList.add(dto);
        }
        return vocDTOList;
    }

    // Get VOC and Reply List
    public VocReplyDTO getVocAndReplyList(String vocId){
        VocReplyDTO dto = new VocReplyDTO();
        VOC voc = vocRepository.findByVocId(vocId);
        VOC_DTO vocDTO = convertEntity(voc);
        dto.setVoc(vocDTO);

        List<Reply> replyList = replyRepository.findByVocId(voc);
        List<ReplyDTO> replyDTOList = new ArrayList<>();
        for (Reply reply : replyList){
            ReplyDTO replyDTO = replyService.convertEntity(reply);
            replyDTOList.add(replyDTO);
        }
        dto.setReply(replyDTOList);

        return dto;
    }

    // Update
    @Transactional
    public void update(VOC_DTO dto){
        VOC voc = convertDTO(dto);
        VOC saved = vocRepository.save(voc);
        log.info("VOC updated: {}", saved.getVocId());
    }

    // Delete
    @Transactional
    public void delete(String vocId){
        VOC voc = vocRepository.findByVocId(vocId);
        vocRepository.delete(voc);

        log.info("VOC deleted: {}", voc.getVocId());
    }

}
