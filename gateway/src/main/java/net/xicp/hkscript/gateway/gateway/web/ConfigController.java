package net.xicp.hkscript.gateway.gateway.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.dubbo.common.URL;



@Controller
@RequestMapping("/config")
public class ConfigController {
    
    @Autowired
    private ConfigService configService;
    
    
//    @Autowired
//    private RegistryServerSync registryServerSync;
//    
    private Logger logger = Logger.getLogger(ConfigController.class);

    protected  ModelAndView newView(){
        ModelAndView mav=new ModelAndView();
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        mav.addObject("rootContextPath",new RootContextPath(request.getContextPath()));
        
        return mav;
    }
    
    @RequestMapping(value = "/list.mc", method = RequestMethod.GET)
    public ModelAndView list(){
        ModelAndView modelView = this.newView();
        List<Config> configs = this.configService.list();
//        List<URL> services = this.registryServerSync.getServices();
        modelView.addObject("configs", configs);
//        modelView.addObject("services", services);
        modelView.setViewName("config");
        return modelView;
    }
    
    
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Map save(Config config){
        
        Map ret = new HashMap();
        try{
            this.configService.save(config);
            ret.put("ret", 200);
        }
        catch(Exception e){
            logger.error("error when save", e);
            ret.put("ret", 500);
            ret.put("msg", e.getMessage());
            
        }
        return ret;
    }
    
    
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @ResponseBody
    public Map delete(@RequestBody List<Integer> ids){
        
        Map ret = new HashMap();
        try{
            this.configService.remove(ids);
            ret.put("ret", 200);
        }
        catch(Exception e){
            logger.error("error when save", e);
            ret.put("ret", 500);
            ret.put("msg", e.getMessage());
            
        }
        return ret;
    }
    
    
    @RequestMapping(value = "/services", method = RequestMethod.GET)
    @ResponseBody
    public Map services(){
        Map ret = new HashMap();
        return ret;
        
    }
}
