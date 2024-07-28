package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.dto.UserDto;
import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.repository.UserRepository;
import com.tech_hub.techhub.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    UserRepository userRepository;



    @GetMapping("/users")
    public String showAdminPanel(Model model) {
        return showUsers(1,"firstName","asc",model);
    }
    @GetMapping("/users/page/{pageNo}")
    public String showUsers(@PathVariable(value = "pageNo")int pageNo,
                            @RequestParam(value = "field")String field,
                            @RequestParam(value = "sortDirection")String sortDirection,
                            Model model){
        int pageSize = 6;
        Page<UserEntity> page = userService.findAllPage(pageNo,pageSize,field,sortDirection);
        List<UserEntity> users = page.getContent();

        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute( "totalItems",page.getTotalElements());
        model.addAttribute("field",field);
        model.addAttribute("sortDirection",sortDirection);
        model.addAttribute("reverseSortDir",sortDirection.equals("asc") ?"desc":"asc");
        model.addAttribute("users",users);
        return "user-management";

    }


    @GetMapping("/users/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String getCreatePage(Model model) {
        model.addAttribute("add-user",new UserDto());
        return "add-user";
    }

    @PostMapping("/users/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String createUser(@ModelAttribute("add-user")UserDto createRequest, Model model) {
        boolean result=userService.addUser(createRequest);
        if(!result && userService.getFlag()==1) {
            model.addAttribute("error", "Username or Email Already Exists");
            return "add-user";
        }else {
            return "redirect:/admin/users";
        }
    }
    @GetMapping("/users/search-user")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String searchUsersByUsername(@RequestParam String searchTerm, Model model) {
        List<UserEntity>users= new ArrayList<>(userRepository.findByName(searchTerm));
        model.addAttribute("users",users);
        return "user-management";
    }
    @GetMapping("/users/block/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String blockUser(@PathVariable("id") Long id) {
       userService.blockUser(id);
       return "redirect:/admin/users";
    }
    @GetMapping("/users/unblock/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String unBlockUser(@PathVariable("id") Long id) {
        userService.unBlockUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String editUser(@PathVariable("id") Long id,Model model) {
        UserEntity user=userService.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return "update-user";
    }

}
