package com.diligence.dfs.common.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络服务端
 * 主要作用是完成端口绑定，并对外提供服务
 * @author Diligence
 * @create 2023 - 03 - 23 1:12
 */
@Slf4j
public class NetServer {
	// 定义 bossGroup，只用于处理连接请求
	private EventLoopGroup boss;
	// 定义 workGroup，处理业务请求
	private EventLoopGroup worker;

	private boolean supportEpoll;

	// 基础的消息处理器
	private BaseChannelInitializer baseChannelInitializer;

	/**
	 * 绑定端口
	 * 服务端开始提供服务
	 * @param ports 端口
	 * @throws InterruptedException 异常
	 */
	private void internalBind(List<Integer> ports) throws InterruptedException {
		try {
			// netty 服务端的常规写法
			// 实例化一个 netty 服务端
			ServerBootstrap bootstrap = new ServerBootstrap();

			// 服务端基本配置
			// 将boss和worker组合在一起，并
			// 使用EpollServerSocketChannel或NioServerSocketChannel创建Channel，
			// ChannelOption.ALLOCATOR和 ChildOption.ALLOCATOR被设置为默认的 PooledByteBufAllocator，
			// 最后将baseChannelInitializer作为Channel的子处理器。
			bootstrap.group(boss, worker)
					.channel(supportEpoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
					.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					.childHandler(baseChannelInitializer);

			// 将 ResourceLeakDetector 的级别设置为高级，以便可以更好地检测资源泄漏
			ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

			List<ChannelFuture> channelFeture = new ArrayList<>();

			// 将多个端口的服务器使用 bootstrap 绑定，
			// 然后通过 sync()同步方法以便服务器可以顺利启动，并在日志中打印出服务器启动的信息，
			// 最后将 ChannelFuture 添加到 channelFeture 列表中
			for (int port : ports) {
				ChannelFuture future = bootstrap.bind(port).sync();
				log.info("Netty Server started on port ：{}", port);
				channelFeture.add(future);
			}

			// 将 ChannelFuture列表中的每个 ChannelFuture 逐一遍历，并为其添加 closeFuture监听器，
			// 当所有的 ChannelFuture都完成后，将调用 channel.close()方法关闭所有的 Channel。
			for (ChannelFuture future : channelFeture) {
				// future.channel().closeFuture()方法可用于监听 Channel 的关闭事件，
				// 当 Channel关闭时，此方法会返回一个 ChannelFutuer 实例，可用于检查 Channel 的关闭状态。
				future.channel().closeFuture().addListener((ChannelFutureListener) future1 -> future1.channel().close());
			}

			// 将ChannelFuture列表中的每个ChannelFuture逐一遍历，并调用closeFuture()方法去监听Channel的关闭事件，
			// 然后使用 sync() 方法阻塞当前线程，直到所有的 Channel 都完成关闭操作。
			for (ChannelFuture future : channelFeture) {
				// 服务端开始提供服务
				future.channel().closeFuture().sync();
			}
		} finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}
}
