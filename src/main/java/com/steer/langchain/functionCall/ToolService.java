package com.steer.langchain.functionCall;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Service;

@Service
public class ToolService {
    @Tool("某个地区有多少个名字") //匹配该句话，并调用该函数
    public Integer countName(@P("地区") String region ,@P("名字") String name) {
        System.out.println("region:"+region);
        System.out.println("name:"+name);
        //处理自定义业务逻辑
        return 1;
    }

    @Tool("退票") //匹配该句话，并调用该函数
    public String cancelBooking(@P("预定号") String bookNum ,@P("名字") String name) {
        System.out.println("bookNum:"+bookNum);
        System.out.println("name:"+name);
        //处理自定义业务逻辑
        return "退票成功";
    }
}
