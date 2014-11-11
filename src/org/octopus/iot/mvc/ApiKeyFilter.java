package org.octopus.iot.mvc;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.HttpStatusView;
import org.octopus.core.Keys;
import org.octopus.core.bean.User;
import org.octopus.iot.IotKeys;
import org.octopus.iot.bean.IotUser;

@IocBean
public class ApiKeyFilter implements ActionFilter {
	
	@Inject
	protected Dao dao;

	public View match(ActionContext ac) {
		String apikey = ac.getRequest().getHeader(IotKeys.UKEY);
		if (apikey != null) {
			IotUser iotUser = dao.fetch(IotUser.class, apikey);
			if (iotUser != null) {
				ac.getRequest().setAttribute(IotKeys.UID, iotUser.getUserId());
				return null;
			}
		}
		HttpSession session = Mvcs.getHttpSession(false);
		if (session != null) {
			User user = (User) session.getAttribute(Keys.SESSION_USER);
			if (user != null) {
				IotUser iotUser = dao.fetch(IotUser.class, Cnd.where("name", "=", user.getName()));
				if (iotUser != null) {
					ac.getRequest().setAttribute(IotKeys.UID, iotUser.getUserId());
					return null;
				}
			}
		}

		return new HttpStatusView(403);
	}
}
