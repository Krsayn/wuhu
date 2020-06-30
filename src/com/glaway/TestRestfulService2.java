package com.glaway;

import com.dassault_systemes.platform.restServices.RestService;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther : Dumpling
 * @Description
 **/
@Path("/TestRestful")
public class TestRestfulService extends RestService {
	  /**
     * @Description 测试例子 访问路径类似于：https://r2019x.glaway.com/3dspace/resources/dsic/TestRestful/sayHello?name=zhouhao
     * @Author Dumpling
     * @param request
     * @return javax.ws.rs.core.Response
     **/
    @GET
    @Path("/sayHello")
    public Response sayHello(@javax.ws.rs.core.Context HttpServletRequest request)
    {
        String name = request.getParameter("name");
        String result = "Hello :" + name;
        System.out.println("222");
        return Response.ok(result).build();
    }
}