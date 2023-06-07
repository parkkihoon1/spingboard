package com.example.spingboard.service;

import com.example.spingboard.dto.MemberJoinDTO;

public interface MemberService {
    static class MidExistException extends Exception{

    }

    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;
}
