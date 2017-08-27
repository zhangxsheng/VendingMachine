package vend.controller;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import base.util.Function;
import base.util.Page;
import vend.entity.CodeLibrary;
import vend.entity.VendShopQrcode;
import vend.service.VendShopQrcodeService;

@Controller
@RequestMapping("/qrcode")
public class VendShopQrcodeController{
	public static Logger logger = Logger.getLogger(VendShopQrcodeController.class);
	
	@Autowired
	VendShopQrcodeService vendShopQrcodeService;
	/**
	 * 根据输入信息条件查询二维码列表，并分页显示
	 * @param model
	 * @param vendShopQrcode
	 * @param page
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/qrcodes")
	public String listVendShopQrcode(Model model,@ModelAttribute VendShopQrcode vendShopQrcode, @ModelAttribute Page page,HttpServletRequest request) {
		String currentPageStr = request.getParameter("currentPage");
		logger.info(currentPageStr + "===========");
		if(currentPageStr != null){
			int currentPage = Integer.parseInt(currentPageStr);
			page.setCurrentPage(currentPage);
		}
		logger.info(page.toString());
		logger.info(vendShopQrcode.toString());
		List<VendShopQrcode> vendShopQrcodes = vendShopQrcodeService.listVendShopQrcode(vendShopQrcode, page);
		model.addAttribute("vendShopQrcodes",vendShopQrcodes);
		return "manage/qrcode/qrcode_list";
	}
	/**
	 * 跳转二维码信息添加界面
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.GET)
	public String qrcoded(Model model){
		model.addAttribute(new VendShopQrcode());
		return "manage/qrcode/qrcode_qrcoded";
	}
   /**
    * 添加二维码信息
    * @param request
    * @param model
    * @param vendShopQrcode
    * @param br
    * @return
    */
    @RequestMapping(value="/add",method=RequestMethod.POST)
	public String qrcoded(HttpServletRequest request,Model model,@Validated VendShopQrcode vendShopQrcode,BindingResult br){
    	if(br.hasErrors()){
    		return "manage/qrcode/qrcode_qrcoded";
    	}
    	vendShopQrcodeService.insertVendShopQrcode(vendShopQrcode);
    	return "redirect:qrcodes";
	}
    /**
	 * 跳转二维码修改界面
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/{id}/edit",method=RequestMethod.GET)
	public String edit(Model model,@PathVariable int id){
		VendShopQrcode vendShopQrcode=vendShopQrcodeService.getOne(id);
		model.addAttribute(vendShopQrcode);
		return "manage/qrcode/qrcode_edit";
	}
	/**
	 * 修改二维码信息
	 * @param request
	 * @param model
	 * @param vendShopQrcode
	 * @param br
	 * @return
	 */
    @RequestMapping(value="/edit",method=RequestMethod.POST)
	public String edit(HttpServletRequest request,Model model,@Validated VendShopQrcode vendShopQrcode,BindingResult br){
    	if(br.hasErrors()){
    		return "manage/qrcode/qrcode_edit";
    	}
    	int isOk=vendShopQrcodeService.editVendShopQrcode(vendShopQrcode);
		return "redirect:qrcodes";
	}
    /**
     * 删除二维码信息
     * @param user
     * @param br
     * @return
     */
    @RequestMapping(value="/{id}/del")
 	public String del(@PathVariable Integer id){
    	vendShopQrcodeService.delVendShopQrcode(id);;
 		return "redirect:/qrcode/qrcodes";
 	}
    /**
     * 批量删除二维码信息
     * @param ids
     * @return
     */
    @RequestMapping(value="/dels")
  	public String dels(HttpServletRequest request){
    	String ids=request.getParameter("ids");
    	String idArray[]=ids.split(",");
    	int[] idArray1=new int[idArray.length];
    	for(int i=0;i<idArray.length;i++){
    		idArray1[i]=Function.getInt(idArray[i], 0);
    	}
    	int isOk=vendShopQrcodeService.delVendShopQrcodes(idArray1);
  		return "redirect:/qrcode/qrcodes";
  	}
}
