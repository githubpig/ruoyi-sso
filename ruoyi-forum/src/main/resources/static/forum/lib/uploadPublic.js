// 判断input是否为空；
function empy(obj, parentObj) {
    var workName = $.trim(obj.val());
    if (workName == "") {
        obj.parents(parentObj).find('.error-prompt').show();

    } else {
        obj.parents(parentObj).find('.error-prompt').hide();
    }
}

// 下拉的显示，隐藏；
function blockToggle(a, b) {
    if (a.css('display') == 'block') {
        b.removeClass('select-arrowicon-active')
        a.hide();
    } else {
        b.addClass('select-arrowicon-active')
        a.show();
    }
}

// 公共弹窗的关闭
$('.pop-close').on('click', function() {
    hideGlobalMaskLayer();
    $('.pop-up').hide();
});

/**
 * 更多设置的新建分类，点击添加
 * @param successFn 作品，文章创建归类ajax成功后的回调
 * @param cancelFn 点击取消添加分类后调用的，同时remove掉该dom
 */
$('#add-type-btn').on('click', function() {
    $('.classified-display').addClass('hide');
    $("#up-classify-add").removeClass('hide');
    addCateDiv($("#up-classify-add"), successFn, cancelFn);
})

/**
 * 作品，文章创建归类ajax成功后
 * @param data 请求成功后的data
 */
function successFn(data) {
    $("#up-classify-add").addClass('hide');
    $('.classified-display').removeClass('hide');
    $('.custom-selectcon li').removeClass('active');
    var selectOption = '<li data-id="' + data.id + '" class="active">' + data.name + '</li>';
    $('#form-data-mycate').attr('data-id', data.id);
    $('.custom-selectcon>ul').append(selectOption)
    $(".custom-select li .custom-current-type").html(data.name)
    $('#up-classify-add .text-complete').addClass('disabled');
}

function cancelFn() {
    $("#up-classify-add").addClass('hide');
    $('.classified-display').removeClass('hide');
}
// 添加分类的交互操作实现
~ function classificationWorks() {
    var customSelectCon = $('.custom-selectcon');
    var customUl = $('.custom-selectcon>ul');
    var customLi = $('.custom-select li');

    customLi.on('click', function(e) {
        if ($(this).parent().hasClass('custom-select')) {
            e.stopPropagation();
        }
        if ($('.custom-select .select-con').css('display') == 'none') {
            $('.custom-select .select-con').css('display', 'block');
            if($('.custom-select .select-con li').length > 5){
                $('.custom-select .select-con ul').addClass('scrollbar');
            }
        } else {
            $('.custom-select .select-con').css('display', 'none');
            saveDraftMonitorInput()
        }
    })
    $(document).click(function() {
        $('.custom-select .select-con').css('display', 'none');
    });

    customUl.on('click', function(event) {
        var e = event || window.event || arguments.callee.caller.arguments[0];

        if (e.target.nodeName == 'LI') {
            $('#form-data-mycate').html($(e.target).html()).attr('data-id', $(e.target).attr('data-id'));
            customSelectCon.hide();
            saveDraftMonitorInput();
            $(e.target).addClass('active').siblings('li').removeClass('active');
            $('.custom-select>li .select-arrowicon').removeClass('select-arrowicon-active');
        }

    })
}();

// 作品归类
$('.moreset-text').on('click', function() {
    blockToggle($('#more-set-con'), $(this).find('i'));
})

// 上传附件
var appendixXhr;
$('#file').change(function(e) {
    var fileName = $('#file')[0].files[0].name;
    var fileNum = $('#file')[0].files[0].name.lastIndexOf('.');
    var extend = fileName.substring(fileNum).toLocaleUpperCase();
    if (extend == '.RAR' || extend == '.ZIP') {
        var size = $('#file')[0].files[0].size / 1024 / 1024;
        if (size < 20) {
            $('.upattastatus .progress').css('display', 'inline-block');
            $('.upattastatus .percent').css('display', 'inline-block');
            $('.upattastatus .closebtn').parent().css('display', 'inline-block');
            $('.upattastatus .closebtn').css('display', 'inline-block');
            $('.statustext label').find('.name').css('width', '0');
            var formData = new FormData();
            formData.append('file', $('#file')[0].files[0]);
            appendixXhr = new XMLHttpRequest();

            appendixXhr.onreadystatechange = function() {
                if (appendixXhr.readyState == 4 && appendixXhr.status == 200) {
                    var res = JSON.parse(appendixXhr.responseText);
                    $('.percent').html('100%');
                    $('.statustext').attr('fileid', res.fileid)
                    $('.upattastatus .progress').css('display', 'none');
                    $('.upattastatus .percent').css('display', 'none');
                    $('.statustext').find('.name').css('width', '214px');
                    $('.statustext').find('.name').html('<span class="rar-icon"></span>' + fileName);
                    $('.statustext').find('.name').css('display', 'inline-block');
                    $('.statustext label .tips').html("上传成功");
                    $('#attachmentId').val(res.guid);
                    console.log(res);
                    $('#file').val('');
                    //saveDraftMonitorInput();
                }
            }

            appendixXhr.upload.onprogress = function(event) {
                if (event.lengthComputable) {
                    var complete = (event.loaded / event.total * 100 | 0) - 1;
                    $('.filling-progress').css('width', complete + '%');
                    $('.percent').html(complete + '%');
                    $('.statustext label').find('.tips').html("上传中");
                    $('#form-data-fileId').addClass('status-progress');
                }
            };
            var zid=$("#form-data-id").val();
            appendixXhr.open('POST',  '/cms/attachment/upload?zid='+zid, true);

            appendixXhr.setRequestHeader("X-File-Name", encodeURIComponent(fileName));

            appendixXhr.send(formData);


        } else {
            // 附件上传超过大小限制
            pageToastFail("附件上传超过大小限制");
            $('#file').val('');
        }
    } else {
        // 附件上传仅支持RAR/ZIP格式
        pageToastFail("附件上传仅支持RAR/ZIP格式");
        $('#file').val('');
    }
})
// 点击附件关闭
$('.upattastatus .closebtn').on('click', function() {


    var guid=$('#attachmentId').val();
    $.ajax({
        type: "post",
        url: "/cms/attachment/delete",
        data: {"ids":guid},
        xhrFields: {
            withCredentials: true
        },
        dataType: "json",
        success: function(data) {
            if(data.code==0){
                appendixXhr&&appendixXhr.abort();
                $('.upattastatus .progress').css('display', 'none');
                $('.upattastatus .percent').css('display', 'none');
                $('.upattastatus .closebtn').css('display', 'none');
                $('.statustext label').find('span').html(' ');
                $('.statustext label .tips').html("选择");
                $('.filling-progress').css('width', 0 + '%');
                $('.percent').html('');
                $('.upattastatus .name').css('display', 'none');
                $('.upattastatus .closebtn').parent().css('display', 'none');
                $('#file').val('');
                $('#form-data-fileId').attr('fileid', 0);
            }else{
                pageToastFail("附件删除失败!");
            }

        }
    })
})

//预览前调用一次是否绑定实名制的接口，异步打开新窗会被拦截，所以点击预览前先请求好。(main.js中定义)
var isExistPhone = false;
asyncRequestIsExistPhone(bindPhoneSuccess)
function bindPhoneSuccess(data){
    if(data.data == 0){
        // 未绑定
        isExistPhone = false;
    }else{
        // 已绑定
        isExistPhone = true;
    }
}

// 贴标签
function Labelling(obj) {
    this.labelling = obj.labelling;
    this.workmark = obj.workmark;
    this.workcon = obj.workcon;
    this.disbled = obj.disbled;
    this.close = this.workcon.find('span>i');
    this.textnum = this.workmark.siblings('.counter').html();
    this.tagNum;
    this.disabledBtnFn();
    this.clickAdd();
    this.tagClose();
}
Labelling.prototype = {
    fn1: function() {
        var me = this;
        var numberOfTag = this.workcon.find('span').length;
        this.tagNum = 5 - numberOfTag;
        this.labelling.val("贴标签" + '(' + this.tagNum + ')')
        // this.textnum = ;

    },
    AddTag:function(inputc, workc){
        var me = this;
        inputc = html_encode(inputc)
        if (inputc != "") {
            var appendTag = '<span class="mark" title="' + inputc + '">' + inputc + ' <i></i></span>'
            workc.append(appendTag);
            saveDraftMonitorInput()
        }
        me.fn1()
        if (me.tagNum == 0) {
            me.labelling.hide()
            me.disbled.show()
            return;
        }
    },
    disabledBtnFn: function() {
        var me = this;
        this.workmark.on('keyup', function() {
            count = parseInt($(this).parent().find('.count').html());
            if (count < 0) {
                me.labelling.attr('disabled', 'disabled').addClass('disabled-color');
            } else if ($(this).val() != "") {
                me.labelling.removeAttr('disabled').removeClass('disabled-color');
            } else {
                me.labelling.attr('disabled', 'disabled').addClass('disabled-color');
            }
        })
    },
    clickAdd: function() {
        var me = this,
            flag = true,
            tags = [];
        this.fn1()
        this.labelling.on('click', function() {
            count = parseInt($(this).parent().find('.count').html());
            // me.workmark.siblings('.counter').html(me.textnum)

            var inputTag;
            if(me.workmark.val().trim() == ""){
                return;
            }else{
                inputTag = $('#workmark').val();

            }
            tags.push(inputTag)
            localStorage.setItem('tag', JSON.stringify(tags));
            me.workmark.val('')
            me.labelling.attr('disabled', 'disabled').addClass('disabled-color');
            if (!validateTag(inputTag)) {
                return;
            }
            if (count < 0) {
                // 超出允许字符数限制
                pageToastFail("字符超出限制");
            } else {
                if (validateTag(inputTag)) {
                    me.AddTag(inputTag, me.workcon);
                }
            }
        })
        $('#workmark').on('keydown', function(event) {
            var e = event || window.event || arguments.callee.caller.arguments[0];

            if (e.keyCode == 13 || e.keyCode == 32) {
                e.preventDefault()

                count = parseInt($(this).parent().find('.count').html());
                // var inputTag = me.workmark.val()
                var inputTag;
                if(me.workmark.val().trim() == ""){
                    return;
                }else{
                    inputTag = $('#workmark').val();
                }
                tags.push(inputTag)
                localStorage.setItem('tag', JSON.stringify(tags));
                me.workmark.val('')
                me.labelling.attr('disabled', 'disabled').addClass('disabled-color');
                if (!validateTag(inputTag)) {
                    return;
                }
                if (me.tagNum == 0) {
                    return;
                }
                if (count < 0) {
                    // 超出允许字符数限制
                    pageToastFail("字符超出限制");
                } else {
                    if (validateTag(inputTag)) {
                        me.AddTag(inputTag, me.workcon);
                    }
                }
            }
        })

        function validateTag(inputTag) {
            var flag = true
            for (var i = 0; i < $('.mark-con').find('span').length; i++) {
                if ($.trim(inputTag.toLowerCase()) != $.trim($($('.mark-con').find('span')[i]).attr('title').toLowerCase())) {
                    flag = true;
                    continue;
                } else {
                    flag = false;
                    // 标签不能重复;
                    pageToastFail("标签不能重复");
                    break;
                }
            }
            return flag
        }
    },
    tagClose: function() {
        var me = this;
        this.workcon.on('click', this.close, function(event) {
            event.target
            $(event.target).parents('.mark').remove();
            if (me.tagNum == 0) {
                me.labelling.show()
                me.disbled.hide()
            }
            me.fn1()
        })
    }
}
new Labelling({
    labelling: $('.mark-btn'),
    workmark: $('#workmark'),
    workcon: $('.mark-con'),
    disbled: $('.work-markbox .mark-disabled')
})
// 贴标签  end


/**
 * 存草稿调用，
 * @param {*} temporaryComparisonV input输入时当前的值
 * @param {*} curObj 当前input元素
 */
var timmer = null;
function saveDraftMonitorInput(temporaryComparisonV, curObj) {
    var draftApi = $('#product-mark').length ?  '/draftProduct' :  '/draftArticle';

    if(!$('#upload-auto-draft').length) return false;

    clearTimeout(timmer)
    timmer = setTimeout(function() {
        //  存草稿时判断当前是否有改动，和必填项是否是超过2项；
        // if (curObj && temporaryComparisonV == curObj.val() && $('[c-required=1]').length >= 2) {
        //     // 在uploadArticle.js中定义；
        //     submit(draftApi, proMyZDomain + '/works', $('.biz-draft-btn'));
        // }else{
        if($('[c-required=1]').length >= 2){
            submit(draftApi, '', $('.biz-draft-btn'));
        }
        // }
    }, 2000)
}

$(function() {
    if($('#form-data-zteamId').text().trim() === '')
        $('#form-data-zteamId').text($('.creative-team li:first').text())
})
