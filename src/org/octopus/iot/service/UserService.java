package org.octopus.iot.service;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.octopus.iot.bean.IotUser;

@IocBean
public class UserService {
	
	@Inject
	Dao dao;

	public long userId(String name) {
		IotUser user = dao.fetch(IotUser.class, Cnd.where("name", "=", name));
		if (user == null)
			return -1;
		return user.getUserId();
	}
}
