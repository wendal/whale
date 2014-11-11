package org.octopus.iot.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.octopus.iot.service.IotSensorService;

@IocBean
public class IotNettyUdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	
	private static final Log log = Logs.get();

	@Inject
	IotNettyCmdHandler iotNettyCmdHandler;
	
	@Inject
	Dao dao;
	
	@Inject
	IotSensorService iotSensorService;
	
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket dp) throws Exception {
		ByteBuf _buf = dp.content();
		int len = _buf.readableBytes();
		if (len < 1) {
			log.debug("emtry udp pack");
			return;
		}
		if (len > 16*1024) {
			log.debug("super big udp, skip it");
			return;
		}
		byte[] buf = new byte[_buf.readableBytes()];
		_buf.readBytes(buf);
		String str = new String(buf).trim();
		iotNettyCmdHandler.exec(ctx, str, iotSensorService, dao);
	}
}