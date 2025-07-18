package com.rental.houserental.controller;

import com.rental.houserental.entity.SystemConfig;
import com.rental.houserental.service.SystemConfigService;
import com.rental.houserental.dto.request.systemconfig.SystemConfigUpdateRequestDTO;
import com.rental.houserental.dto.response.systemconfig.SystemConfigResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/admin/system-configs")
@RequiredArgsConstructor
public class AdminSystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping("")
    public String listConfigs(Model model) {
        model.addAttribute("configs", systemConfigService.getAllConfigs());
        return "admin/system-configs";
    }

    @GetMapping("/edit/{id}")
    public String editConfigForm(@PathVariable Long id, Model model) {
        SystemConfigResponseDTO config = systemConfigService.getConfigById(id);
        SystemConfigUpdateRequestDTO dto = new SystemConfigUpdateRequestDTO();
        dto.setValue(config.getValue());
        dto.setDescription(config.getDescription());
        model.addAttribute("configId", id);
        model.addAttribute("configKey", config.getKey());
        model.addAttribute("updateRequest", dto);
        return "admin/edit-system-config";
    }

    @PostMapping("/edit/{id}")
    public String updateConfig(@PathVariable Long id, @Valid @ModelAttribute("updateRequest") SystemConfigUpdateRequestDTO updateRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            SystemConfigResponseDTO config = systemConfigService.getConfigById(id);
            model.addAttribute("configId", id);
            model.addAttribute("configKey", config.getKey());
            return "admin/edit-system-config";
        }
        systemConfigService.updateConfigValueAndDescription(id, updateRequest.getValue(), updateRequest.getDescription());
        return "redirect:/admin/system-configs";
    }
} 