package com.example.spingboard.controller;

import com.example.spingboard.dto.BoardDTO;
import com.example.spingboard.dto.PageRequestDTO;
import com.example.spingboard.dto.PageResponseDTO;
import com.example.spingboard.service.BoardService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
@Getter
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){

        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        log.info(responseDTO);

        model.addAttribute("responseDTO", responseDTO);


    }
    @PreAuthorize("hasRole('USER')") // /board/register 경로로 접근할 때 'USER'라는 권한을 사전에 체크
    @GetMapping("/register")
    public void registerGet(){

    }
    @PostMapping("/register")
    public String registerPOST(@Valid BoardDTO boardDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes
    ) {


        log.info("board POST register...");

        if (bindingResult.hasErrors()) {
            log.info("has errors...");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());

            return "redirect:/board/register";
        }
        log.info(boardDTO);
        Long bno = boardService.register(boardDTO);
        redirectAttributes.addFlashAttribute("result", bno);
        return "redirect:/board/list";
    }

    @GetMapping({"/read", "/modify"})
    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 조회 할 수 있도록 표현식으로 제한
    public void read(Long bno, PageRequestDTO pageRequestDTO, Model model){
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info(boardDTO);
        model.addAttribute("dto", boardDTO);
    }

    @PostMapping("/modify")
    @PreAuthorize("principal.username == #boardDTO.writer") //로그인한 사용자와 게시물의 작성자가 일치 할 때만 삭제
    public String modify( PageRequestDTO pageRequestDTO,
                          @Valid BoardDTO boardDTO,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes){
        log.info("board modify post....." + boardDTO);

        if(bindingResult.hasErrors()){
            log.info("has errors.......");

            String link = pageRequestDTO.getLink();

            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());

            redirectAttributes.addAttribute("bno", boardDTO.getBno());

            return "redirect:/board/modify?"+link;
        }

        boardService.modify(boardDTO);

        redirectAttributes.addFlashAttribute("result", "modified");

        redirectAttributes.addAttribute("bno", boardDTO.getBno());

        return "redirect:/board/read";
    }

    @PostMapping("/remove")
    @PreAuthorize("principal.username == #boardDTO.writer")
    public String remove(BoardDTO boardDTO, RedirectAttributes redirectAttributes){

        Long bno = boardDTO.getBno();
        log.info("remove post.." + bno);

        boardService.remove(bno);

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/board/list";
    }

}
