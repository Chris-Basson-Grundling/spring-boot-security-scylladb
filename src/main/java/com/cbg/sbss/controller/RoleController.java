package com.cbg.sbss.controller;

import com.cbg.sbss.dto.RoleDto;
import com.cbg.sbss.service.RoleService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class RoleController {

  private final RoleService roleService;

  @GetMapping
  public ResponseEntity<List<RoleDto>> getAllRoles(final Authentication authentication) {
    return ResponseEntity.ok(roleService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<RoleDto> getRoleById(@PathVariable UUID id,
      final Authentication authentication) {
    return ResponseEntity.ok(roleService.findById(id));
  }

  @PostMapping
  public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto,
      final Authentication authentication) {
    return ResponseEntity.ok(roleService.save(roleDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<RoleDto> updateRole(@PathVariable UUID id,
      @Valid @RequestBody RoleDto roleDto, final Authentication authentication) {
    roleDto.setId(id);
    return ResponseEntity.ok(roleService.update(roleDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRole(@PathVariable UUID id,
      final Authentication authentication) {
    roleService.delete(id);
    return ResponseEntity.noContent().build();
  }
}