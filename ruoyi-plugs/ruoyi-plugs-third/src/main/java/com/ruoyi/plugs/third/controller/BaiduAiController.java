package com.ruoyi.plugs.third.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.config.Global;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.plugs.third.baidu.bean.ocr.*;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.plugs.third.baidu.api.BaiduAi;
import com.ruoyi.plugs.third.baidu.bean.face.FaceDetectResult;
import com.ruoyi.plugs.third.baidu.bean.imgClassify.AnimalResult;
import com.ruoyi.plugs.third.baidu.bean.imgClassify.CarResult;
import com.ruoyi.plugs.third.baidu.bean.imgClassify.DishResult;
import com.ruoyi.plugs.third.baidu.bean.imgClassify.PlantResult;
import com.ruoyi.plugs.third.baidu.service.FaceService;
import com.ruoyi.plugs.third.baidu.service.ImageClassifyService;
import com.ruoyi.plugs.third.baidu.service.OcrService;
import com.ruoyi.plugs.third.domain.AiHis;
import com.ruoyi.plugs.third.service.IAiHisService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wujiyue
 */
@Controller
@RequestMapping("/third/ai")
public class BaiduAiController extends BaseController {

    @Autowired
    IAiHisService aiHisService;
    private static String prefix="/third/ai/";
    public static String DEFAULT_UPLOAD_BASE_PATH=FileUploadUtils.getDefaultBaseDir()+"/temp";

    @RequestMapping(value={"","/"})
    @RequiresPermissions("third:ai")
    public String index(){
        return prefix+"/baiduAi";
    }

    //????????????????????????
    @ResponseBody
    @RequestMapping("/queryAi")
    public Object upload(@RequestParam("type") String aiType,@RequestParam("file") MultipartFile multipartFile) throws Exception{
        Map resultMap =new HashMap<String,Object>();
        if(StringUtils.isEmpty(aiType)){
            return AjaxResult.error("type??????????????????!");
        }
        String path=FileUploadUtils.upload(DEFAULT_UPLOAD_BASE_PATH,multipartFile);
        resultMap.put("path",path);
        //????????????????????????
        path= path.replace(Constants.RESOURCE_PREFIX,"");
        String localFilePath=Global.getProfile()+path;
        return queryAi(aiType,localFilePath);
    }
    public Object queryAi(String aiType,String localFilePath){

        String json="";//??????????????????json
        String aiName="";
        if(BaiduAi.AiType.faceDetect.name().equals(aiType)){
            FaceDetectResult result = FaceService.faceDetect_localPath(localFilePath);
            json=result.getJson();
            aiName="????????????";
        }else if(BaiduAi.AiType.plant.name().equals(aiType)){
            PlantResult result= ImageClassifyService.plant(localFilePath);
            json=result.getJson();
            aiName="????????????";
        }else if(BaiduAi.AiType.bankCard.name().equals(aiType)){
            BankCardOcrResult result = OcrService.bankCardOcr(localFilePath);
            json=result.getJson();
            aiName="???????????????";
        }else if(BaiduAi.AiType.idCard.name().equals(aiType)){
            IdCardOcrResult result = OcrService.idcardOcr_Z(localFilePath);
            json=result.getJson();
            aiName="???????????????";
        }else if(BaiduAi.AiType.plate.name().equals(aiType)){
            //???????????????
            PlateNumberResult result = OcrService.license_plateOcr(localFilePath);
            json=result.getJson();
            aiName="???????????????";
        }else if(BaiduAi.AiType.driver.name().equals(aiType)){
            //???????????????
            aiName="???????????????";
            DriveLicenseOcrResult result = OcrService.drivingLicenseOcr(localFilePath);
            json=result.getJson();
        }else if(BaiduAi.AiType.animal.name().equals(aiType)){
            //????????????
            AnimalResult result = ImageClassifyService.animal(localFilePath);
            json=result.getJson();
        }else if(BaiduAi.AiType.car.name().equals(aiType)){
            //????????????
            aiName="????????????";
            CarResult result = ImageClassifyService.car(localFilePath);
            json=result.getJson();
        }else if(BaiduAi.AiType.dish.name().equals(aiType)){
            //????????????
            aiName="????????????";
            DishResult result = ImageClassifyService.dish(localFilePath);
            json=result.getJson();
        }else if(BaiduAi.AiType.general_basic.name().toString().equals(aiType)){
            //??????????????????
            aiName="??????????????????";
            GeneralBasicIOcrResult result= OcrService.general_basic(localFilePath);
            json=result.getJson();
        }
        AiHis aiHis=new AiHis();
        JSONObject jsonObject= JSONObject.parseObject(json);
        String e_code=String.valueOf(jsonObject.get("error_code"));
        String e_msg=String.valueOf(jsonObject.get("error_msg"));
        String log_id=String.valueOf(jsonObject.get("log_id"));
        aiHis.setId(log_id);
        if(StringUtils.isNotEmpty(e_code)&&!"0".equals(e_code)){
            aiHis.setResult("0");
        }else{
            aiHis.setResult("1");
        }
        if(StringUtils.isNotEmpty(e_msg)){
            aiHis.setErrorMsg(e_msg);
        }
        aiHis.setAiType(aiType);
        aiHis.setTypeName(aiName);
        aiHis.setJsonResult(json);
        SysUser user=ShiroUtils.getSysUser();
        aiHis.setYhid(String.valueOf(user.getUserId()));
        aiHis.setYhmc(user.getUserName());
        aiHisService.insertAiHis(aiHis);
        return AjaxResult.success("",json);
    }
    public Object queryAi2(String aiType,String localFilePath,Map resultMap){
        if(BaiduAi.AiType.faceDetect.name().equals(aiType)){
            FaceDetectResult result = FaceService.faceDetect_localPath(localFilePath);
            if(result.isRequestOk()){
                resultMap.put("result","??????");
                resultMap.put("age",result.getAge());
                resultMap.put("gender","female".equals(result.getGender())?"???":"???");
                resultMap.put("beauty",result.getBeauty());
                resultMap.put("expression",result.getExpression());
                resultMap.put("hasGlasses",result.isHasGlasses()?"???":"???");
            }else{
                resultMap.put("result","??????"+result.getError_msg());
            }
        }else if(BaiduAi.AiType.plant.name().equals(aiType)){
            PlantResult result= ImageClassifyService.plant(localFilePath);
            if(result.isRequestOk()){
                //????????????
                if(result.isPlant()){
                    //???????????????
                    resultMap.put("result","??????");
                    resultMap.put("name",result.getResult_name());
                    resultMap.put("probability",result.getResult_probability());
                }else{
                    //????????????????????????
                    resultMap.put("result","?????????");
                    resultMap.put("resultJson", JSON.toJSONString(result.getResult()));
                }
            }else{
                //????????????
                resultMap.put("result","????????????!"+result.getError_msg());
            }
        }else if(BaiduAi.AiType.bankCard.name().equals(aiType)){
            BankCardOcrResult result = OcrService.bankCardOcr(localFilePath);
            if(result.isRequestOk()){
                resultMap.put("result","??????");
                resultMap.put("cardNum",result.getBank_card_number());
                resultMap.put("bankName",result.getBank_name());
            }else{
                resultMap.put("result","????????????!"+result.getError_msg());
            }
        }else if(BaiduAi.AiType.idCard.name().equals(aiType)){
            IdCardOcrResult result = OcrService.idcardOcr_Z(localFilePath);
            if(result.isRequestOk()){
                resultMap.put("result","??????");
                resultMap.put("gender",result.getGender());
                resultMap.put("address",result.getAddress());
                resultMap.put("birthday",result.getBirthday());
                resultMap.put("ID",result.getID());
                resultMap.put("name",result.getName());
                resultMap.put("minzhu",result.getMinZhu());
            }else{
                resultMap.put("result","????????????!"+result.getError_msg());
            }
        }else if(BaiduAi.AiType.plate.name().equals(aiType)){
            //???????????????
            PlateNumberResult result = OcrService.license_plateOcr(localFilePath);
            if(result.isRequestOk()){
                resultMap.put("result","??????");
                resultMap.put("number",result.getPlateNumber());
                resultMap.put("color",result.getColor());
            }else{
                resultMap.put("result","????????????!"+result.getError_msg());
            }
        }else if(BaiduAi.AiType.driver.name().equals(aiType)){
            //???????????????
            DriveLicenseOcrResult result = OcrService.drivingLicenseOcr(localFilePath);
            if(result.isRequestOk()){
                resultMap.put("result","??????");
                resultMap.put("ID",result.getID());
                resultMap.put("name",result.getName());
                resultMap.put("gender",result.getGender());
                resultMap.put("address",result.getAddress());
                resultMap.put("birthday",result.getBirthday());
                resultMap.put("country",result.getCountry());
                resultMap.put("time",result.getStarttime()+"-"+result.getEndtime());
                resultMap.put("type",result.getType());//C1
            }else{
                resultMap.put("result","????????????!"+result.getError_msg());
            }
        }else if(BaiduAi.AiType.animal.name().equals(aiType)){
            //????????????
            AnimalResult result = ImageClassifyService.animal(localFilePath);
            if(result.isRequestOk()){
                if(result.isAnimal()){
                    resultMap.put("result","??????");
                    resultMap.put("name",result.getResult_name());
                    resultMap.put("probability",result.getResult_probability());
                }else{
                    resultMap.put("result","?????????");
                    resultMap.put("resultJson", JSON.toJSONString(result.getResult()));
                }
            }else{
                resultMap.put("result","????????????!"+result.getError_msg());
            }
        }else if(BaiduAi.AiType.car.name().equals(aiType)){
            //????????????
            CarResult result = ImageClassifyService.car(localFilePath);
            if(result.isRequestOk()){
                if(result.isCar()){
                    resultMap.put("result","??????");
                    resultMap.put("name",result.getResult_name());
                    resultMap.put("probability",result.getResult_probability());
                    resultMap.put("year",result.getResult_year());
                }else{
                    resultMap.put("result","?????????");
                    resultMap.put("resultJson", JSON.toJSONString(result.getResult()));
                }
            }else{
                resultMap.put("result","????????????!"+result.getError_msg());
            }
        }else if(BaiduAi.AiType.dish.name().equals(aiType)){
            //????????????
            DishResult result = ImageClassifyService.dish(localFilePath);
            if(result.isRequestOk()){
                if(result.isDish()){
                    resultMap.put("result","??????");
                    resultMap.put("name",result.getResult_name());
                    resultMap.put("probability",result.getResult_probability());
                    resultMap.put("calorie",result.getResult_calorie());
                }else{
                    resultMap.put("result","??????");
                    resultMap.put("resultJson", JSON.toJSONString(result.getResult()));
                }
            }else{
                resultMap.put("result","????????????!"+result.getError_msg());
            }
        }else if(BaiduAi.AiType.general_basic.name().toString().equals(aiType)){
            //??????????????????
            GeneralBasicIOcrResult result= OcrService.general_basic(localFilePath);
            if(result.isRequestOk()){
                resultMap.put("result","??????");
                resultMap.put("words_result_num",result.getWords_result_num());
                resultMap.put("words",result.getWords());
                resultMap.put("paragraph",result.getParagraph());
            }else{
                resultMap.put("result","????????????!"+result.getError_msg());
            }
        }
        return resultMap;
    }
}
