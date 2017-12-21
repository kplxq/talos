package com.kxd.talos.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kxd.talos.dashboard.framework.controller.BaseController;

@Controller
public class IndexController extends BaseController {
	@RequestMapping(value = "/index")
    public String index() {
        return "screen/system/index";
    }
}
