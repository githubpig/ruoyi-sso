/**
 * 作品，文章创建归类
 * @param {*} parentDiv 添加分类input输入的父容器
 * @param {*} callback1 分类请求成功后的回调
 * @param {*} callback2 注册事件，取消后调用callback2，同时remove掉该dom
 */

function addCateDiv(parentDiv,callback1,callback2){
    var cateDom = ''+
        '<div class="new-typebox">'+
        '<input type="text" name="addtype" placeholder="' + messagesWeb.classification_restrictions + '" class="text-style newtype-text">'+
        '</div>'+
        '<span class="text-complete disabled">' + messagesWeb.common_complete + '</span>'+
        '<span class="text-cancel">' + messagesWeb.common_cancel + '</span>';
    parentDiv.append(cateDom);
    //name
    var name = parentDiv.data('name');
    if (name) {
        $('input[name=addtype]').val(name);
    }
    zCharCount_withExceedCount(parentDiv.find('input[name=addtype]'),{
        allowed:24
    })
    ////提交的时候判断是否有id,进行传参
    var complete = parentDiv.find('.text-complete');
    var cancel = parentDiv.find('.text-cancel');
    var objEvt = $._data(complete[0], "events");
    // if(objEvt && objEvt["click"]){
    // 	// complete.unbind();
    // 	// cancel.unbind();
    // }else{
    parentDiv.unbind();
    //注册事件，点击完成，改按钮状态（包括完成和取消），同时提交请求，
    // addAndModifyCateUrl 在页面中定义或当前js中赋值定义；
    parentDiv.on('click','.text-complete:not(.disabled)',function(){
        //id
        var id = parentDiv.data('id');
        var newTypeVal = $('input[name=addtype]').val();
        var formData = new FormData();
        formData.mycateName = newTypeVal;
        if (id) {
            formData.mycateId=id;
        }
        complete.addClass('disabled');
        $.ajax({
            type: 'POST',
            xhrFields: {
                withCredentials: true
            },
            crossDomain: true,
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            },

            url: addAndModifyCateUrl,
            data: getAddAndModifyCateUrlData(formData.mycateName,formData.mycateId),
            //根据请求回来的结果进行判断，
            success: function(data) {//成功，调用callback1，同时remove掉该dom
                if (data.code == 0) {
                    parentDiv.html('');
                    callback1(data.data);
                    isFresh = true
                } else {//失败，toast提示，并且重新让输入框获取焦点
                    pageToastFail(data.msg);
                    $('input[name=addtype]').focus();
                    complete.removeClass('disabled');
                }
            },
            error: function() {//失败，toast提示，并且重新让输入框获取焦点
                // 服务器异常
                pageToastFail(messagesWeb.comment_exception_hints);
                $('input[name=addtype]').focus();
                complete.removeClass('disabled');
            }
        })
    })
    //注册事件，取消后调用callback2，同时remove掉该dom
    cancel.on('click',function(){
        parentDiv.html('')
        callback2();
    })
    // }
    // input输入的监听
    $('input[name=addtype]').on('keyup',function(e) {
        var count = parseInt($(this).siblings('.count').html())
        newTypeVal = $(this).val();
        if (newTypeVal != "") {
            complete.removeClass('disabled')
            term = true;

        } else {
            complete.addClass('disabled')
        }
        if(count < 0){
            complete.addClass('disabled')
        }else{
            complete.removeClass('disabled')
        }
    })




}