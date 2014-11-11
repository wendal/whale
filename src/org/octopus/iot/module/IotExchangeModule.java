package org.octopus.iot.module;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.VoidAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.view.HttpStatusView;
import org.octopus.iot.IotKeys;
import org.octopus.iot.Iots;
import org.octopus.iot.bean.IotSensor;
import org.octopus.iot.mvc.ApiKeyFilter;
import org.octopus.iot.mvc.IotHttpCmdHandler;
import org.octopus.iot.service.IotSensorService;

@IocBean
@Filters({@By(type=ApiKeyFilter.class, args="ioc:apiKeyFilter")})
@Fail("http:500")
@At("/iot2")
public class IotExchangeModule {
	
	private static final Log log = Logs.get();
	
	@Inject
	Dao dao;
	
	@Inject
	IotSensorService iotSensorService;
	
	@Inject
	IotHttpCmdHandler iotHttpCmdHandler;
	
	@At({"/up", "/up/?"})
	@AdaptBy(type=VoidAdaptor.class)
	public void upload(long sensor_id, @Attr(IotKeys.UID)long userId, HttpServletResponse resp, HttpServletRequest req) throws IOException {
		if (req.getHeader("Content-Length") == null) {
			badReq(resp, Iots.NEED_CONTENT_LENGTH);
			return;
		}
		if (req.getContentLength() < 1) {
			badReq(resp, Iots.EMTRY_BODY);
			return;
		}
		if (req.getContentLength() > 1024*1024) {
			badReq(resp, Iots.TOOBIG);
			return;
		}
		if (sensor_id > 0) { // 图片上传
			IotSensor sensor = dao.fetch(IotSensor.class, Cnd.where("id", "=", sensor_id).and("userId", "=", userId));
			if (sensor == null) {
				badReq(resp, Iots.NOTFOUND);
				return;
			}
			// 保存一下
			File tmp = File.createTempFile("iot", ".jpg");
			try {
				Files.write(tmp, req.getInputStream());
				BufferedImage image = Images.read(tmp);
				iotSensorService.saveImage(sensor, tmp, image.getWidth(), image.getHeight());
			} catch (Exception e) {
				log.info("bad image", e);
			} finally {
				tmp.delete();
			}
		} else {
			// 普通上传
			iotHttpCmdHandler.exec(resp.getWriter(), Streams.readAndClose(req.getReader()), iotSensorService, dao);
		}
	}
	
	protected void badReq(HttpServletResponse resp, String msg) throws IOException {
		resp.setStatus(406);
		resp.getWriter().write(msg);
		return;
	}
	
	@At({"/get/?", "/get/?/?"})
	@AdaptBy(type=VoidAdaptor.class)
	@GET
	@Ok("raw")
	@Filters()
	public Object get(long sensor_id, String key) {
		IotSensor sensor = dao.fetch(IotSensor.class, sensor_id);
		if (sensor == null) {
			return HttpStatusView.HTTP_404;
		}
		return sensor.getValue() == null ? "" : sensor.getValue();
	}
}
