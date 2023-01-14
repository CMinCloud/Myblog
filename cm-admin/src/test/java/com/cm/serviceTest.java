package com.cm;

import com.cm.controller.TagController;
import com.cm.domain.dto.TagListDto;
import com.cm.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class serviceTest {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagController tagController;


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
