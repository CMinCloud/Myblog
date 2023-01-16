package com.cm.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MenuVo4Role {

    private List<MenuVo4Role> children;

    private Long id;

    private String label;

    private Long parentId;
}
