package com.e3.sso.userService;

import com.e3.pojo.TbUser;
import com.e3.utils.E3Result;

public interface UserService {
	E3Result checkData(String param,int type);
	E3Result createUser(TbUser tbUser);
	E3Result login(String username,String password);
	E3Result getUserByToken(String token);
}
