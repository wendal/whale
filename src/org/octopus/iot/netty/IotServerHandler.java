package org.octopus.iot.netty;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.octopus.iot.service.CmdHandler;
import org.octopus.iot.service.IotSensorService;

/**
 * Handles a server-side channel.
 */
@Sharable
@IocBean(singleton=false)
public class IotServerHandler extends SimpleChannelInboundHandler<String> {
	
	public static final String version = "1.0";
	
	@Inject Dao dao;
	
	@Inject IotSensorService iotSensorService;
	
	CmdHandler cmd = new NettyCmdHandler();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // Send greeting for a new connection.
        //resp(ctx, "ok", "ver,"+version);
    }

    public void channelRead0(ChannelHandlerContext ctx, String req) throws Exception {
        cmd._exec(ctx, req, iotSensorService, dao);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}