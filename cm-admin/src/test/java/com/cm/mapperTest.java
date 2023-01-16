package com.cm;

import com.cm.mapper.RoleMenuMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class mapperTest {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Test
    void test1() {
        List<Long> menuIdByRoleId = roleMenuMapper.getMenuIdByRoleId(12L);
        System.out.println(menuIdByRoleId);
    }

}
