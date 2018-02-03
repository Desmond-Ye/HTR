package com.htr.loan;

import com.htr.loan.Utils.Constants;
import com.htr.loan.domain.Resource;
import com.htr.loan.domain.Role;
import com.htr.loan.domain.User;
import com.htr.loan.service.ResourceService;
import com.htr.loan.service.RoleService;
import com.htr.loan.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class BaseTest extends LoanApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ResourceService resourceService;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUserAccount("admin");
        user.setUserName("管理员");
        user.setPassword("htr1qaz@WSX");
        Role userRole = roleService.findRoleByRoleName("超级管理员");
        user.setRoles(new ArrayList<Role>(Arrays.asList(userRole)));
        userService.saveUser(user, false);
    }

    @Test
    public void testSaveRole() {
        Role role = new Role();
        role.setDescription("超级管理员拥有至高无上的权利!!");
        role.setRoleName("超级管理员");
        role = roleService.saveRole(role);
        System.out.println(role.getUuid());
    }

    @Test
    public void testUpdateRole() {
        Role role = roleService.findRoleByRoleName("超级管理员");
        List<Resource> resources = role.getResources();

        Resource resource1 = resourceService.findByResourceName("系统管理");
        Resource resource11 = resourceService.findByResourceName("用户管理");
        Resource resource12 = resourceService.findByResourceName("角色管理");
        Resource resource121 = resourceService.findByResourceName("分配权限");
        Resource resource13 = resourceService.findByResourceName("日志查询");

        Resource resource2 = resourceService.findByResourceName("贷款管理");
        Resource resource21 = resourceService.findByResourceName("档案管理");
        Resource resource22 = resourceService.findByResourceName("人员管理");
        Resource resource23 = resourceService.findByResourceName("车辆管理");
        Resource resource24 = resourceService.findByResourceName("银行卡管理");
        resources.add(resource1);
        resources.add(resource11);
        resources.add(resource12);
        resources.add(resource13);

        resources.add(resource2);
        resources.add(resource21);
        resources.add(resource22);
        resources.add(resource23);
        resources.add(resource24);

        resources.add(resource121);

        role.setResources(resources);
        role = roleService.saveRole(role);
        System.out.println(role.getUuid());
    }

    @Test
    public void testSaveResource() {

//        Resource resource = new Resource();
//        resource.setResourceName("系统管理");
//        resource.setActive(true);
//        resource = resourceService.saveResource(resource);
//
//        Resource subResource1 = new Resource();
//        subResource1.setResourceName("日志查询");
//        subResource1.setResPath("sys/systemLog");
//        subResource1.setParentRes(resource);
//        subResource1.setActive(true);
//        subResource1 = resourceService.saveResource(subResource1);
//
//        Resource subResource2 = new Resource();
//        subResource2.setResourceName("用户管理");
//        subResource2.setResPath("sys/userList");
//        subResource2.setParentRes(resource);
//        subResource2.setActive(true);
//        subResource2 = resourceService.saveResource(subResource2);
//
//        Resource subResource3 = new Resource();
//        subResource3.setResourceName("角色管理");
//        subResource3.setResPath("sys/roleList");
//        subResource3.setParentRes(resource);
//        subResource3.setActive(true);
//        subResource3 = resourceService.saveResource(subResource3);
//
//
//        resource.setChildrenRes(Arrays.asList(subResource1,subResource2,subResource3));
//        resource = resourceService.saveResource(resource);
//        System.out.println(resource.getUuid());


//        Resource resource = new Resource();
//        resource.setResourceName("贷款管理");
//        resource = resourceService.saveResource(resource);
//
//        Resource subResource1 = new Resource();
//        subResource1.setResourceName("档案管理");
//        subResource1.setResPath("loan/loanInfo");
//        subResource1.setParentRes(resource);
//        subResource1 = resourceService.saveResource(subResource1);
//
//        Resource subResource2 = new Resource();
//        subResource2.setResourceName("人员管理");
//        subResource2.setResPath("loan/person");
//        subResource2.setParentRes(resource);
//        subResource2 = resourceService.saveResource(subResource2);
//
//        Resource subResource3 = new Resource();
//        subResource3.setResourceName("车辆管理");
//        subResource3.setResPath("loan/vehicle");
//        subResource3.setParentRes(resource);
//        subResource3 = resourceService.saveResource(subResource3);
//
//        Resource subResource4 = new Resource();
//        subResource4.setResourceName("银行卡管理");
//        subResource4.setResPath("loan/bankCard");
//        subResource4.setParentRes(resource);
//        subResource4 = resourceService.saveResource(subResource4);
//
//        Resource subResource5 = new Resource();
//        subResource5.setResourceName("还款记录查询");
//        subResource5.setResPath("loan/subLoanRecord");
//        subResource5.setParentRes(resource);
//        subResource5 = resourceService.saveResource(subResource5);
//
//
//        resource.setChildrenRes(Arrays.asList(subResource1,subResource2,subResource3,subResource4,subResource5));
//        resource = resourceService.saveResource(resource);
//        System.out.println(resource.getUuid());



//        Resource resource = new Resource();
//        resource.setResourceName("北斗业务管理");
//        resource = resourceService.saveResource(resource);
//
//        Resource subResource1 = new Resource();
//        subResource1.setResourceName("北斗档案管理");
//        subResource1.setResPath("beidou/beidouRecord");
//        subResource1.setParentRes(resource);
//        subResource1 = resourceService.saveResource(subResource1);
//
//        Resource subResource4 = new Resource();
//        subResource4.setResourceName("分部管理");
//        subResource4.setResPath("beidou/beidouBranch");
//        subResource4.setParentRes(resource);
//        subResource4 = resourceService.saveResource(subResource4);
//
//        Resource subResource2 = new Resource();
//        subResource2.setResourceName("续费记录查询");
//        subResource2.setResPath("beidou/beidouRenewal");
//        subResource2.setParentRes(resource);
//        subResource2 = resourceService.saveResource(subResource2);
//
//        Resource subResource3 = new Resource();
//        subResource3.setResourceName("维修记录查询");
//        subResource3.setResPath("beidou/beidouRepair");
//        subResource3.setParentRes(resource);
//        subResource3 = resourceService.saveResource(subResource3);
//
//        resource.setChildrenRes(Arrays.asList(subResource1,subResource4,subResource2,subResource3));
//        resource = resourceService.saveResource(resource);
//        System.out.println(resource.getUuid());





//        Resource resource11 = resourceService.findByResourceName("用户管理");
//        Resource resource11_button1 = new Resource();
//        resource11_button1.setParentRes(resource11);
//        resource11_button1.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource11_button1.setResourceName("新增");
//        resource11_button1.setResPath("new");
//
//        Resource resource11_button2 = new Resource();
//        resource11_button2.setParentRes(resource11);
//        resource11_button2.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource11_button2.setResourceName("修改");
//        resource11_button2.setResPath("update");
//
//        Resource resource11_button3 = new Resource();
//        resource11_button3.setParentRes(resource11);
//        resource11_button3.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource11_button3.setResourceName("禁用");
//        resource11_button3.setResPath("stop");
//
//        Resource resource11_button4 = new Resource();
//        resource11_button4.setParentRes(resource11);
//        resource11_button4.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource11_button4.setResourceName("启用");
//        resource11_button4.setResPath("active");
//        resource11.setChildrenRes(Arrays.asList(resource11_button1, resource11_button2, resource11_button3, resource11_button4));
//        resourceService.saveResource(resource11);
//
//        Resource resource12 = resourceService.findByResourceName("角色管理");
//        Resource resource12_button1 = new Resource();
//        resource12_button1.setParentRes(resource12);
//        resource12_button1.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource12_button1.setResourceName("新增");
//        resource12_button1.setResPath("new");
//
//        Resource resource12_button2 = new Resource();
//        resource12_button2.setParentRes(resource12);
//        resource12_button2.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource12_button2.setResourceName("修改");
//        resource12_button2.setResPath("update");
//
//        Resource resource12_button3 = new Resource();
//        resource12_button3.setParentRes(resource12);
//        resource12_button3.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource12_button3.setResourceName("禁用");
//        resource12_button3.setResPath("stop");
//
//        Resource resource12_button4 = new Resource();
//        resource12_button4.setParentRes(resource12);
//        resource12_button4.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource12_button4.setResourceName("启用");
//        resource12_button4.setResPath("active");
//
//        Resource resource12_button5 = new Resource();
//        resource12_button5.setParentRes(resource12);
//        resource12_button5.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource12_button5.setResourceName("分配权限");
//        resource12_button5.setResPath("permission");
//
//        resource12.setChildrenRes(Arrays.asList(resource12_button1, resource12_button2, resource12_button3, resource12_button4, resource12_button5));
//        resourceService.saveResource(resource12);
//
//
//
//        Resource resource21 = resourceService.findByResourceName("档案管理");
//        Resource resource21_button1 = new Resource();
//        resource21_button1.setParentRes(resource21);
//        resource21_button1.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource21_button1.setResourceName("新增");
//        resource21_button1.setResPath("new");
//
//        Resource resource21_button2 = new Resource();
//        resource21_button2.setParentRes(resource21);
//        resource21_button2.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource21_button2.setResourceName("删除");
//        resource21_button2.setResPath("delete");
//
//        Resource resource21_button3 = new Resource();
//        resource21_button3.setParentRes(resource21);
//        resource21_button3.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource21_button3.setResourceName("还款");
//        resource21_button3.setResPath("repayment");
//
//        Resource resource21_button8 = new Resource();
//        resource21_button8.setParentRes(resource21);
//        resource21_button8.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource21_button8.setResourceName("撤销还款");
//        resource21_button8.setResPath("backRepayment");
//
//        Resource resource21_button4 = new Resource();
//        resource21_button4.setParentRes(resource21);
//        resource21_button4.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource21_button4.setResourceName("查看档案详情");
//        resource21_button4.setResPath("loanInfoDetail");
//
//        Resource resource21_button5 = new Resource();
//        resource21_button5.setParentRes(resource21);
//        resource21_button5.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource21_button5.setResourceName("查看还款明细");
//        resource21_button5.setResPath("loanRecordDetail");
//
//        Resource resource21_button6 = new Resource();
//        resource21_button6.setParentRes(resource21);
//        resource21_button6.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource21_button6.setResourceName("导出欠款单");
//        resource21_button6.setResPath("exportLoanInfo");
//
//        Resource resource21_button7 = new Resource();
//        resource21_button7.setParentRes(resource21);
//        resource21_button7.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource21_button7.setResourceName("导出月报表");
//        resource21_button7.setResPath("exportMonthReport");
//
//        resource21.setChildrenRes(Arrays.asList(resource21_button1, resource21_button2, resource21_button3, resource21_button4, resource21_button5,resource21_button6,resource21_button7,resource21_button8));
//        resourceService.saveResource(resource21);
////
////
//        Resource resource22 = resourceService.findByResourceName("人员管理");
//        Resource resource22_button1 = new Resource();
//        resource22_button1.setParentRes(resource22);
//        resource22_button1.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource22_button1.setResourceName("新增");
//        resource22_button1.setResPath("new");
//
//        Resource resource22_button2 = new Resource();
//        resource22_button2.setParentRes(resource22);
//        resource22_button2.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource22_button2.setResourceName("修改");
//        resource22_button2.setResPath("update");
//
//        Resource resource22_button3 = new Resource();
//        resource22_button3.setParentRes(resource22);
//        resource22_button3.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource22_button3.setResourceName("删除");
//        resource22_button3.setResPath("delete");
//
//        resource22.setChildrenRes(Arrays.asList(resource22_button1, resource22_button2, resource22_button3));
//        resourceService.saveResource(resource22);
//
////
//        Resource resource23 = resourceService.findByResourceName("车辆管理");
//        Resource resource23_button1 = new Resource();
//        resource23_button1.setParentRes(resource23);
//        resource23_button1.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource23_button1.setResourceName("新增");
//        resource23_button1.setResPath("new");
//
//        Resource resource23_button2 = new Resource();
//        resource23_button2.setParentRes(resource23);
//        resource23_button2.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource23_button2.setResourceName("修改");
//        resource23_button2.setResPath("update");
//
//        Resource resource23_button3 = new Resource();
//        resource23_button3.setParentRes(resource23);
//        resource23_button3.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource23_button3.setResourceName("删除");
//        resource23_button3.setResPath("delete");
//
//        Resource resource23_button4 = new Resource();
//        resource23_button4.setParentRes(resource23);
//        resource23_button4.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource23_button4.setResourceName("扣押");
//        resource23_button4.setResPath("detain");
//
//        Resource resource23_button5 = new Resource();
//        resource23_button5.setParentRes(resource23);
//        resource23_button5.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource23_button5.setResourceName("解扣");
//        resource23_button5.setResPath("unDetain");
//
//        resource23.setChildrenRes(Arrays.asList(resource23_button1, resource23_button2, resource23_button3, resource23_button4, resource23_button5));
//        resourceService.saveResource(resource23);
////
////
//        Resource resource24 = resourceService.findByResourceName("银行卡管理");
//        Resource resource24_button1 = new Resource();
//        resource24_button1.setParentRes(resource24);
//        resource24_button1.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource24_button1.setResourceName("新增");
//        resource24_button1.setResPath("new");
//
//        Resource resource24_button2 = new Resource();
//        resource24_button2.setParentRes(resource24);
//        resource24_button2.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource24_button2.setResourceName("修改");
//        resource24_button2.setResPath("update");
//
//        Resource resource24_button3 = new Resource();
//        resource24_button3.setParentRes(resource24);
//        resource24_button3.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource24_button3.setResourceName("禁用");
//        resource24_button3.setResPath("stop");
//
//        Resource resource24_button4 = new Resource();
//        resource24_button4.setParentRes(resource24);
//        resource24_button4.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource24_button4.setResourceName("启用");
//        resource24_button4.setResPath("active");
//        resource24.setChildrenRes(Arrays.asList(resource24_button1, resource24_button2, resource24_button3, resource24_button4));
//        resourceService.saveResource(resource24);
//
//        Resource resource31 = resourceService.findByResourceName("北斗档案管理");
//        Resource resource31_button1 = new Resource();
//        resource31_button1.setParentRes(resource31);
//        resource31_button1.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource31_button1.setResourceName("新增");
//        resource31_button1.setResPath("new");
//
//        Resource resource31_button6 = new Resource();
//        resource31_button6.setParentRes(resource31);
//        resource31_button6.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource31_button6.setResourceName("修改");
//        resource31_button6.setResPath("update");
//
//        Resource resource31_button2 = new Resource();
//        resource31_button2.setParentRes(resource31);
//        resource31_button2.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource31_button2.setResourceName("删除");
//        resource31_button2.setResPath("delete");
//
//        Resource resource31_button3 = new Resource();
//        resource31_button3.setParentRes(resource31);
//        resource31_button3.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource31_button3.setResourceName("续费");
//        resource31_button3.setResPath("renewal");
//
//        Resource resource31_button4 = new Resource();
//        resource31_button4.setParentRes(resource31);
//        resource31_button4.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource31_button4.setResourceName("查看续费记录");
//        resource31_button4.setResPath("renewalDetail");
//
//        Resource resource31_button5 = new Resource();
//        resource31_button5.setParentRes(resource31);
//        resource31_button5.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource31_button5.setResourceName("查看维修记录");
//        resource31_button5.setResPath("repairDetail");
//
//        Resource resource31_button7 = new Resource();
//        resource31_button7.setParentRes(resource31);
//        resource31_button7.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource31_button7.setResourceName("维修");
//        resource31_button7.setResPath("repair");
//
//        resource31.setChildrenRes(Arrays.asList(resource31_button1, resource31_button6, resource31_button2, resource31_button3, resource31_button4, resource31_button5, resource31_button7));
//        resourceService.saveResource(resource31);
//
//        Resource resource32 = resourceService.findByResourceName("分部管理");
//        Resource resource32_button1 = new Resource();
//        resource32_button1.setParentRes(resource32);
//        resource32_button1.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource32_button1.setResourceName("新增");
//        resource32_button1.setResPath("new");
//
//        Resource resource32_button2 = new Resource();
//        resource32_button2.setParentRes(resource32);
//        resource32_button2.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource32_button2.setResourceName("修改");
//        resource32_button2.setResPath("update");
//
//        Resource resource32_button3 = new Resource();
//        resource32_button3.setParentRes(resource32);
//        resource32_button3.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource32_button3.setResourceName("禁用");
//        resource32_button3.setResPath("stop");
//
//        Resource resource32_button4 = new Resource();
//        resource32_button4.setParentRes(resource32);
//        resource32_button4.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
//        resource32_button4.setResourceName("启用");
//        resource32_button4.setResPath("active");
//        resource32.setChildrenRes(Arrays.asList(resource32_button1, resource32_button2, resource32_button3, resource32_button4));
//        resourceService.saveResource(resource32);

        Resource resource21 = resourceService.findByResourceName("贷款管理");
        List<Resource> resources = resource21.getChildrenRes();

        Resource subResource2 = new Resource();
        subResource2.setResourceName("保险管理");
        subResource2.setResPath("loan/insurance");
        subResource2.setParentRes(resource21);
        resources.add(subResource2);

        Resource resource31_button1 = new Resource();
        resource31_button1.setParentRes(subResource2);
        resource31_button1.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
        resource31_button1.setResourceName("新增");
        resource31_button1.setResPath("new");

        Resource resource31_button6 = new Resource();
        resource31_button6.setParentRes(subResource2);
        resource31_button6.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
        resource31_button6.setResourceName("修改");
        resource31_button6.setResPath("update");

        Resource resource31_button2 = new Resource();
        resource31_button2.setParentRes(subResource2);
        resource31_button2.setResourceType(Constants.RESOURCE_TYPE_BUTTON);
        resource31_button2.setResourceName("删除");
        resource31_button2.setResPath("delete");

        subResource2.setChildrenRes(Arrays.asList(resource31_button1,resource31_button6,resource31_button2));

        resource21.setChildrenRes(resources);
        resourceService.saveResource(resource21);
    }

    @Test
    public void testSaveBeidouBranch(){

    }
}
