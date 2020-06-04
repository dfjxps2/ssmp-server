package com.seaboxdata.auth.server.axis.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.axis2.transport.http.AxisServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class AxisConfig {

    @Bean
    public ServletRegistrationBean axisServlet() {

        /**
        ServletRegistrationBean axisServlet = new ServletRegistrationBean();
        axisServlet.setServlet(new AxisServlet());//这里的AxisServlet就是web.xml中的org.apache.axis2.transport.http.AxisServlet
        axisServlet.addUrlMappings("/services/*");
        //通过默认路径无法找到services.xml，这里需要指定一下路径，且必须是绝对路径

        String path = this.getClass().getResource("/WEB-INF").getPath();

        axisServlet.addInitParameter("axis2.repository.path", path);
        axisServlet.setLoadOnStartup(1);
        return axisServlet;
         **/


        ServletRegistrationBean axisServlet = new ServletRegistrationBean();
        axisServlet.setServlet(new AxisServlet());//这里的AxisServlet就是web.xml中的org.apache.axis2.transport.http.AxisServlet
        axisServlet.addUrlMappings("/services/*");
        //通过默认路径无法找到services.xml，这里需要指定一下路径，且必须是绝对路径
        String path = this.getClass().getResource("/WEB-INF").getPath().toString();
        log.info("The original path：" + path);
        if(path.toLowerCase().startsWith("file:")){
            log.info("去掉前面的“file:”！");
            path = path.substring(5);
        }
        if(path.indexOf("!") != -1){
            try {
                log.info("将WEB-INF/services/MyWebService/META-INF/services.xml文件拷贝到jar包同级目录下！");
                FileCopyUtils.copy("WEB-INF/services/axisServlet/META-INF/services.xml");
            } catch (IOException e) {
                log.error("AxisConfig配置加载异常, {}", e);
            }
            log.info("jar包运行！查找jar包同级目录下的“/WEB-INF”目录");
            path = path.substring(0, path.lastIndexOf("/", path.indexOf("!"))) + "/WEB-INF";
        }
        log.info("The final path：" + path);
        axisServlet.addInitParameter("axis2.repository.path", path);
        axisServlet.setLoadOnStartup(1);
        return axisServlet;
    }

}
