package org.octopus.iot.mvc;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.octopus.iot.IotKeys;
import org.octopus.iot.service.IotCmdHandler;

@IocBean
public class IotHttpCmdHandler extends IotCmdHandler {
	
	List<NutMap> list = new ArrayList<NutMap>();

	public Object resp(Object ctx, String code, String msg) {
		NutMap map = new NutMap();
		map.put("ok", "true");
		map.put("msg", msg);
		list.add(map);
		return null;
	}

	public Long uid(Object ctx) {
		return (Long) Mvcs.getReq().getAttribute(IotKeys.UID);
	}

	public void uid(Object ctx, long uid) {
		Mvcs.getReq().setAttribute(IotKeys.UID, uid);
	}

	public void close(Object tx) {
		// nop for http
	}

	public void end(Object ctx) {
		try {
			if (Mvcs.getReq().getRequestURI().endsWith(".json")) {
				Mvcs.write((HttpServletResponse)ctx, list, JsonFormat.compact());
			} else {
				Writer writer = ((HttpServletResponse)ctx).getWriter();
				for (NutMap map : list) {
					writer.write(String.format("%s,%s\n", map.get("ok"), map.get("msg")));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();//不常见
		}
	}
}
