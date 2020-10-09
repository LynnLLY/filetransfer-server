package com.transfer.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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

    /**
     * 合并文件所用的定时器
     */
    @Bean
    public void merge() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    // 获取concurrentHashMap中已经完成的文件路径
                    Map<String, Object> fileMap = MapRecord.fileMap;
                    Set<String> strings = fileMap.keySet();

                    Iterator<String> iterator = strings.iterator();
                    // 遍历文件路径集合
                    if (iterator.hasNext()) {
                        String next = iterator.next();
                        String[] split = next.split("\\_");
                        String s = split[0];
                        long count = strings.stream().filter(x -> x.contains(s)).count();
                        System.out.println("同一文件片断数：" + count);
                        // 当count=5时，即可开始合并文件。之后需删除对应的keys.
                        if (count == 5) {
                            // 开始合并
                            String osName = System.getProperties().getProperty("os.name");
                            String cmd = "";
                            System.out.println("===========操作系统是:" + osName);
                            if (osName.toLowerCase().contains("linux") || osName.toLowerCase().contains("mac")) {
                                // copy part1 part2 > final
                                cmd = String.format("cat %s_0 %s_1 %s_2 %s_3 %s_4 > %s ", s, s, s, s, s, s);
                                executeLinuxCmd(cmd);
                            }

                            for (int i = 0; i < 5; i++) {
                                fileMap.remove(s + "_" + i);
                                File file = new File(s + "_" + i);
                                file.delete();
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 3000);
    }

    public String executeLinuxCmd(String cmd) {
        System.out.println("执行命令[ " + cmd + "]");
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(cmd);
            String line;
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer out = new StringBuffer();
            while ((line = stdoutReader.readLine()) != null) {
                out.append(line);
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            process.destroy();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
