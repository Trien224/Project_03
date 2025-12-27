package com.dttlibrary.service;

import com.dttlibrary.model.Role;
import com.dttlibrary.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Role findById(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Role findByName(String roleName) {
        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            throw new EntityNotFoundException("Role not found with name: " + roleName);
        }
        return role;
    }
}
