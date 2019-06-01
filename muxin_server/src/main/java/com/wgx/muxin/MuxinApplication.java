package com.wgx.muxin;

import com.wgx.muxin.netty.utils.SpringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.wgx.muxin.dao.mapper")
@ComponentScan({"com.wgx.muxin","com.wgx.org"})
public class MuxinApplication {

	@Bean
	public SpringUtil getSpringUtil() {
		return new SpringUtil();
	}

	public static void main(String[] args) {
		SpringApplication.run(MuxinApplication.class, args);
	}

}
