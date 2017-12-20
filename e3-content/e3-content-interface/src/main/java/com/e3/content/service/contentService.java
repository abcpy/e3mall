package com.e3.content.service;

import java.util.List;

import com.e3.pojo.TbContent;
import com.e3.utils.E3Result;

public interface contentService {
	E3Result addContent(TbContent tbContent);
	List<TbContent> getContent(long cid);
}
