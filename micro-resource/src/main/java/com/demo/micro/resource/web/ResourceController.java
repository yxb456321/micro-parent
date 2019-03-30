package com.demo.micro.resource.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.micro.common.domain.RestResponse;
import com.demo.micro.resource.entity.Comment;
import com.demo.micro.resource.entity.Info;
import com.demo.micro.resource.entity.InfoPageParams;
import com.demo.micro.resource.entity.UserInfo;
import com.demo.micro.resource.entity.UserRole;
import com.demo.micro.resource.service.ResourceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/resource")
@Api(value = "资源服务", tags = "ResourceApi", description="包含应用、资源等信息维护")
public class ResourceController {

	private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);
	
	@Autowired
	private ResourceService resourceService;
	
	
	@ApiOperation("通过id删除评论")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value="评论id", required=true, dataType="Long", paramType="query")
	})
	@DeleteMapping("/removeCommentById")
	public RestResponse<Nullable> removeCommentById(@RequestParam Long id){
		logger.info("[begin]参数:{}",id);
        resourceService.removeComment(id);
        logger.info("[end]结果。");
        return RestResponse.success();
	}
	
	@ApiOperation("发布评论")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "comment", value="评论", required=true, dataTypeClass=Comment.class, paramType="body")
	})
	@PostMapping("/addComment")
	public RestResponse<Long> addComment(@RequestBody Comment comment){
		logger.info("[begin]参数:{}",comment);
        Long newId = resourceService.addComment(comment);
        logger.info("[end]结果。{}", newId);
        return RestResponse.success(newId);
	}
	
	
	@ApiOperation("通过username获取用户信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "username", value="用户名", required=true, dataType="String", paramType="query")
	})
	@GetMapping("/getUserInfo")
	public RestResponse<UserInfo> getUserInfo(@RequestParam String username){
		logger.info("[begin]参数:{}",username);
        UserInfo userInfo = resourceService.getUserInfo(username);
        logger.info("[end]结果。{}", userInfo);
        return RestResponse.success(userInfo);
	}
	
	@ApiOperation("通过id删除信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value="信息id", required=true, dataType="Long", paramType="query")
	})
	@DeleteMapping("/removeInfoById")
	public RestResponse<Nullable> removeInfoById(@RequestParam Long id){
		logger.info("[begin]参数:{}",id);
        resourceService.removeInfoById(id);
        logger.info("[end]结果。");
        return RestResponse.success();
	}
	
	@ApiOperation("通过id查询信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value="信息id", required=true, dataType="Long", paramType="query")
	})
	@GetMapping("/getInfoById")
	public RestResponse<Info> getInfoById(@RequestParam Long id){
		logger.info("[begin]参数:{}",id);
        Info info = resourceService.getInfoById(id);
        logger.info("[end]结果。{}", info);
        return RestResponse.success(info);
	}
	
	@ApiOperation("发布信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "info", value="信息", required=true, dataTypeClass=Info.class, paramType="body")
	})
	@PostMapping("/addInfo")
	public RestResponse<Long> addInfo(@RequestBody Info info){
		logger.info("[begin]参数:{}",info);
        Long addInfoId = resourceService.addInfo(info);
        logger.info("[end]结果。{}", addInfoId);
        return RestResponse.success(addInfoId);
	}
	
	@ApiOperation(value="分页条件查询信息")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "pageNo", value = "请求页码", required = true, dataType = "int", paramType="query"),
		@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType="query"),
		@ApiImplicitParam(name = "queryParams", value = "查询条件（title和infoType）", required = false, dataTypeClass = InfoPageParams.class, paramType = "body")
    })
    @PostMapping(value = "/pageInfo")
    public RestResponse<Page<Info>> pageInfo(@RequestBody InfoPageParams queryParams
			,@RequestParam Integer pageNo, @RequestParam Integer pageSize){
        logger.info("[begin]pageNo:{} pageSize:{} params:{} y:{}",pageNo, pageSize, queryParams);
        Page<Info> infoList = resourceService.pageInfo(pageNo, pageSize, queryParams);
        logger.info("[end]成功。{}", infoList);
        return RestResponse.success(infoList);
    }
	
	@ApiOperation(value="根据位置获取指定范围内，指定数量的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "infoType", value = "信息类型", required = false, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "名称（模糊搜索）", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "x", value = "经度（数值较大的）", required = true, dataType = "Double", paramType = "query"),
            @ApiImplicitParam(name = "y", value = "纬度（数值较小的）", required = true, dataType = "Double", paramType = "query"),
            @ApiImplicitParam(name = "range", value = "要获取信息的半径", required = true, dataType = "Double", paramType = "query"),
            @ApiImplicitParam(name = "number", value = "要获取信息的数量", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping(value = "/findRangedInfoList")
    public RestResponse<List<Info>> findInfoList(@RequestParam(value="infoType", required=false) Long infoType,
                                                 @RequestParam(value="title", required=false) String title,
                                                 @RequestParam("x") Double x, @RequestParam("y") Double y,
                                          @RequestParam("range") Double range, @RequestParam("number") Integer number){
        logger.info("[begin]title:{} infoType:{} x:{} y:{}",title, infoType, x, y);
        List<Info> infoList = resourceService.findInfoList(infoType, title, x, y, range, number);
        logger.info("[end]成功。{}", infoList);
        return RestResponse.success(infoList);
    }
	
	@ApiOperation(value="登录")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType="path")
    @PostMapping(value = "/login")
    public RestResponse<UserRole> login(@RequestBody UserInfo userInfo, HttpServletResponse response,
                                        HttpServletRequest request, HttpSession httpSession){
        logger.info("[web-begin]登录：{}", userInfo);
        UserRole userRole = resourceService.login(userInfo);
        logger.info("[web-end]登陆成功。{}", userRole);
        return RestResponse.success(userRole);
    }

	
	@ApiOperation("hi")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "str", value = "str", required = false, dataType = "String", paramType="query")
	})
	@GetMapping(value = "/hi")
	public RestResponse<Nullable> pageApplicationByConditions(@RequestParam String str){
		System.out.println("Hi! this is resourceServer." + str);
		return RestResponse.success();
	}

}
