package com.example.spingboard.service;

import com.example.spingboard.dto.BoardDTO;
import com.example.spingboard.dto.PageRequestDTO;
import com.example.spingboard.dto.PageResponseDTO;

public interface BoardService {
    Long register(BoardDTO boardDTO);

    BoardDTO readOne(Long bno);

    void modify(BoardDTO boardDTO);

    void remove(Long bno);

    PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO);
}
