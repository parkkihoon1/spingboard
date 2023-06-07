package com.example.spingboard.repository.search;

import com.example.spingboard.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardSearch {
    Page<Board> serach1(Pageable pageable);

    Page<Board> searchAll(String[] types, String keyword, Pageable pageable);
}
