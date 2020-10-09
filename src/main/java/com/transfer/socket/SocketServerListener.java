package com.transfer.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author admin
 * @version 1.0
 * @date 2020/10/4 19:05
 * @className SendFile
 * @projectName socket-transfer
 */
@Configuration
public class SocketServerListener extends HttpServlet {

    private static final long serialVersionUID = -999999999999999999L;

    //  初始化启动Socket服务
    @Override
    public void init() throws ServletException {
        super.init();
        for (int i = 0; i < 3; i++) {
            if ("instart".equals(FinalVariables.IS_START_SERVER)) {
                open();
                break;
            }
        }
    }

    @Bean
    public void open() {
        System.out.println("ok~~~~~~~~~~~~~~~~~~~");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    FileUpLoadServer fileUpLoadServer = new FileUpLoadServer(FinalVariables.SERVER_PORT);
                    fileUpLoadServer.load();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3000);
    }


}
