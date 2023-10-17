package com.fastcampus.projectboardadmin.service;

import com.fastcampus.projectboardadmin.dto.UserAccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserAccountManagementService {

    public List<UserAccountDto> getUsers() { return List.of(); }

    public UserAccountDto getUser(String uerId) { return null; }

    public void deleteUser(String userId) {  }
}
