package com.cm;

import com.cm.controller.TagController;
import com.cm.domain.dto.TagListDto;
import com.cm.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class serviceTest {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagController tagController;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void generatePassword(){
        String i = "123456";
        String encode = passwordEncoder.encode(i);
        System.out.println(encode);
    }

    @Test
    void testAddTag(){
//        TagListDto tagListDto = new TagListDto("aaa","bbb");
//        tagService.addTag(tagListDto);
    }


    @Test
    void deleteTag(){
//        tagController.deleteTagById(6L);
    }
}
