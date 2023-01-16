package com.cm.controller;

import com.cm.domain.dto.MenuDto;
import com.cm.domain.entity.Menu;
import com.cm.domain.vo.MenuVo;
import com.cm.domain.vo.ResponseResult;
import com.cm.domain.vo.RoutersVo;
import com.cm.service.MenuService;
import com.cm.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public ResponseResult listMenu(MenuDto menuDto){
        return menuService.list(menuDto);
    }

    @PostMapping
    public ResponseResult addMenu(@RequestBody Menu menu){
        return menuService.addMenu(menu);
    }

    @GetMapping("/{id}")
    public ResponseResult getMenuById(@PathVariable("id") Long menuId){
        return menuService.getMenuById(menuId);
    }

    @PutMapping
    public ResponseResult update(@RequestBody Menu menu){
        return menuService.updateMenu(menu);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteById(@PathVariable("id") Long id){
        return menuService.deleteMenu(id);
    }

    @GetMapping("/treeselect")
    public ResponseResult treeSelect(){
        return menuService.menuTree(0L);
    }

    @GetMapping("roleMenuTreeselect/{id}")
    public ResponseResult roleMenuTree(@PathVariable("id") Long id){
        return menuService.roleMenuTree(id);
    }


}
