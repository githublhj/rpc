package com.lhj.fxwiz.io.v2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description:
 * @Author: lhj
 * @Time: 2019/5/6 21:41
 * @Version: 1.0
 */
public class NIOTest {


    public static void main(String[] args) throws IOException {


        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress(9999));

        System.out.println(" NIO NIOServer has started,listening on port："+ serverSocketChannel.getLocalAddress());

        Selector selector = Selector.open();

        //选择器，新连接山来的Channel 注册到Selector选择器中
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // todo 缺包
        //RequestHandler requestHandler = new RequestHandler();

        while (true){
            int select = selector.select();

            if (select == 0){
                continue;
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();


            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()){
                SelectionKey key = iterator.next();

                //已经连接上来了，这时候需要进行读写
                if (key.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = channel.accept();
                    System.out.println("Connection from"+clientChannel.getRemoteAddress());
                    clientChannel.configureBlocking(false);

                    //说明该Channel 要读数据了
                    clientChannel.register(selector,SelectionKey.OP_READ);

                }

                if (key.isAcceptable()){
                    SocketChannel channel = (SocketChannel) key.channel();
                    channel.read(buffer);
                    String request = new String(buffer.array()).trim();
                    buffer.clear();
                    System.out.println(String.format("From %s : %s",channel.getRemoteAddress(),request));
                    // todo
                    //String response = requestHandler.handle(request);
                    //channel.write(Bytebuffer.wrap(response.getBytes()))
                }
                iterator.remove();
            }

        }



    }
}
