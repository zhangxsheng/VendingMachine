package vend.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import base.util.Page;
import vend.entity.CodeLibrary;
import vend.entity.VendOrder;
import vend.service.CodeLibraryService;
import vend.service.VendOrderService;

@Controller
@RequestMapping("/order")
public class VendOrderController{
	public static Logger logger = Logger.getLogger(VendOrderController.class);
	
	@Autowired
	VendOrderService vendOrderService;
	@Autowired
	CodeLibraryService codeLibraryService;
	/**
	 * 根据输入信息条件查询订单列表，并分页显示
	 * @param model
	 * @param vendOrder
	 * @param beginTime
	 * @param endTime
	 * @param page
	 * @param request
	 * @return
	 */
	@RequiresPermissions({"order:orders"})
	@RequestMapping(value="/orders")
	public String listVendOrder(Model model,@ModelAttribute VendOrder vendOrder,String beginTime,String endTime, @ModelAttribute Page page,HttpServletRequest request) {
		String currentPageStr = request.getParameter("currentPage");
		logger.info(currentPageStr + "===========");
		if(currentPageStr != null){
			int currentPage = Integer.parseInt(currentPageStr);
			page.setCurrentPage(currentPage);
		}
		logger.info(page.toString());
		logger.info(vendOrder.toString());
		List<VendOrder> vendOrders = vendOrderService.listVendOrder(vendOrder,beginTime,endTime, page);
		model.addAttribute("vendOrders",vendOrders);
		return "manage/order/order_list";
	}
	/**
	 * 得到消费用户订单数据
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/jorders",method=RequestMethod.POST,produces = "application/json;charset=UTF-8")
	public @ResponseBody Map<String,Object> getJson(@RequestBody Map<String, String> map,@ModelAttribute Page page) throws IOException {
		Map<String,Object> resultmap=new HashMap<String,Object>();
		String currentPageStr = map.get("page");
		String pageNumberStr = map.get("page_size");
		logger.info(currentPageStr + "===========");
		if(currentPageStr != null){
			int currentPage = Integer.parseInt(currentPageStr);
			int pageNumber = Integer.parseInt(pageNumberStr);
			page.setCurrentPage(currentPage);
			page.setPageNumber(pageNumber);
		}
		VendOrder vendOrder=new VendOrder();
		vendOrder.setUsercode(map.get("usercode"));
		List<VendOrder> vendOrders = vendOrderService.listVendOrder(vendOrder, "", "", page);
		List<CodeLibrary> ordertypes =codeLibraryService.selectByCodeNo("ORDERTYPE");
		resultmap.put("vendOrders", vendOrders);
		resultmap.put("ordertypes", ordertypes);
		return resultmap;
	}
	/**
	 * 跳转订单信息添加界面
	 * @param model
	 * @return
	 */
	@RequiresPermissions({"order:add"})
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String add(Model model){
		model.addAttribute(new VendOrder());
		return "manage/order/order_add";
	}
   /**
    * 添加订单信息
    * @param model
    * @param vendOrder
    * @param br
    * @return
    */
	@RequiresPermissions({"order:add"})
    @RequestMapping(value="/add",method=RequestMethod.POST)
	public String add(Model model,@Validated VendOrder vendOrder,BindingResult br){
    	if(br.hasErrors()){
    		return "manage/order/order_add";
    	}
    	vendOrderService.insertVendOrder(vendOrder);
    	return "redirect:orders";
	}
    /**
	 * 跳转订单修改界面
	 * @param model
	 * @return
	 */
	@RequiresPermissions({"order:edit"})
	@RequestMapping(value="/{ordercode}/edit",method=RequestMethod.GET)
	public String edit(Model model,@PathVariable String ordercode){
		VendOrder vendOrder=vendOrderService.getOne(ordercode);
		model.addAttribute(vendOrder);
		return "manage/order/order_edit";
	}
	/**
	 * 修改订单信息
	 * @param model
	 * @param vendOrder
	 * @param br
	 * @return
	 */
	@RequiresPermissions({"order:edit"})
    @RequestMapping(value="/edit",method=RequestMethod.POST)
	public String edit(Model model,@Validated VendOrder vendOrder,BindingResult br){
    	if(br.hasErrors()){
    		return "manage/order/order_edit";
    	}
    	int isOk=vendOrderService.editVendOrder(vendOrder);
		return "redirect:orders";
	}
    /**
     * 删除订单信息
     * @param order
     * @param br
     * @return
     */
	@RequiresPermissions({"order:del"})
    @RequestMapping(value="/{orderId}/del")
 	public String del(@PathVariable String orderId){
    	vendOrderService.delVendOrder(orderId);
 		return "redirect:/order/orders";
 	}
    /**
     * 批量删除订单信息
     * @param ids
     * @return
     */
	@RequiresPermissions({"order:dels"})
    @RequestMapping(value="/dels")
  	public String dels(HttpServletRequest request){
    	String ordercodes=request.getParameter("ids");
    	String ordercodeArray[]=ordercodes.split(",");
    	int isOk=vendOrderService.delVendOrders(ordercodeArray);
  		return "redirect:/order/orders";
  	}
}
