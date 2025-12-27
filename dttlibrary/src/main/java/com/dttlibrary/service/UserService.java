package com.dttlibrary.service;

import com.dttlibrary.model.Role;
import com.dttlibrary.model.User;
import com.dttlibrary.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    @Transactional
    public User registerUser(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent(u -> {
            throw new IllegalStateException("Username already exists: " + user.getUsername());
        });
        // Gán role mặc định cho người dùng mới
        Role memberRole = roleService.findByName("MEMBER");
        user.setRoles(Set.of(memberRole));
        return userRepository.save(user);
    }

    @Transactional
    public User saveAdmin(User user, List<Integer> roleIds) {
        if (user.getId() != null) { // Chế độ edit
            User existingUser = findById(user.getId());
            if (!StringUtils.hasText(user.getPassword())) {
                user.setPassword(existingUser.getPassword());
            }
        }
        
        if (roleIds != null && !roleIds.isEmpty()) {
            user.setRoles(roleIds.stream().map(roleService::findById).collect(Collectors.toSet()));
        } else {
            user.setRoles(null);
        }

        return userRepository.save(user);
    }
    
    @Transactional
    public User updateProfile(User user) {
        User existingUser = findById(user.getId());
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        return userRepository.save(existingUser);
    }

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = findByUsername(username);
        if (!user.getPassword().equals(currentPassword)) {
            throw new IllegalStateException("Mật khẩu hiện tại không đúng.");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    @Transactional
    public void delete(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. User not found with ID: " + id);
        }
        // Thêm logic kiểm tra xem user có đang mượn sách không
        userRepository.deleteById(id);
    }
}
