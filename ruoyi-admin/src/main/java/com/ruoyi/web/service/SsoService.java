package com.ruoyi.web.service;

import com.ruoyi.cms.domain.Attachment;
import com.ruoyi.cms.service.IAttachmentService;
import com.ruoyi.system.domain.SysSso;
import com.ruoyi.system.service.ISysSsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author piggy
 * @desciption
 * @date 2021/11/13 - 15:25
 */
@Service("ssoService")
public class SsoService {

    @Autowired
    private ISysSsoService sysSsoService;

    public List<SysSso> getSysSsoList(){
        SysSso sysSso = new SysSso();
        sysSso.setStatus(1);
        return sysSsoService.selectSysSsoList(sysSso);
    }
}
