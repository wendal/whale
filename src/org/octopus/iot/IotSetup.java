package org.octopus.iot;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.octopus.iot.netty.IotNettyService;
import org.octopus.iot.service.IotService;

@IocBean
public class IotSetup implements Setup {
	
	private static final Log log = Logs.get();
	
	@Inject Dao dao;

	@Inject IotService iotService;
	
	@Override
	public void init(NutConfig nc) {
		String pkg = Iots.class.getPackage().getName();
		for (int i = 0; i < Iots.PART; i++) {
			Dao dao = Daos.ext(nc.getIoc().get(Dao.class), ""+i);
			Daos.createTablesInPackage(dao, pkg, false);
		}
		iotService.rootUser();
		nc.getIoc().get(IotNettyService.class);
		log.info("iot setup complete");
	}
	
	@Override
	public void destroy(NutConfig nc) {
	}
}
