package com.e3.car;

import java.util.List;

import com.e3.pojo.TbItem;
import com.e3.utils.E3Result;

public interface CartService {
	public E3Result addCart(long userId,long itemId,int num);
	 E3Result mergeCart(long userId,List<TbItem> list);
	 List<TbItem> geTbItems(long userId);
	 E3Result updateCatnum(long userId,long itemId,int num);
	 E3Result delCat(long userId,long itemId);
	 E3Result clearCartItem(long userId);
}
