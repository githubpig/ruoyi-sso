/**
 * Created by beanCao on 2016/11/14.
 */

$(function() {

    'use strict';

    var console = window.console || {
        log: function() {}
    };
    var URL = window.URL || window.webkitURL;
    var $image = $('#image');
    var $xhrAjax;
    //初始化cropper属性
    var options = {
        viewMode:1,
        cropBoxResizable: true,
        autoCropArea: 1,
        aspectRatio: 104 / 78,
        preview: '.img-preview',
        dragMode: 'move',
        toggleDragModeOnDblclick: false,
    };
    var originalImageURL = $image.attr('src');
    var uploadedImageURL;
    var originalPicJson,
        originalPicWidth,
        originalPicHeight,
        cropPicJson,
        cropPicWidth,
        cropPIcHeight;


    // Tooltip
    $('[data-toggle="tooltip"]').tooltip();

    // Cropper
    $image.on({
        // 初始化的时候执行次函数内的任务
        'build.cropper': function(e) {
            if ($('#form-data-cover').attr('data-covername') != "") {
                $('.cover').remove();
                $('.cover-preview').remove();
                // $('.rotate-area').css('display', 'block');
                // $('.edt-btn-reset').removeClass('edit-disabled')
                $('.btn-zoomout, .btn-zoomin, .btn-rotater').removeClass('edit-disabled')
                if ($('#form-data-cover').attr('data-covername') !== '0')
                    $('#form-data-cover').addClass('have-data-cover-style');
                $image.on('load', function() {
                    $('.z-swiper-wrapper').addClass('edit-disabled')
                    $('.btn-submit .pop-confirm').attr('disabled', 'disabled')
                })

            }

        },
        'built.cropper': function(e) {
            $('.z-swiper-wrapper').removeClass('edit-disabled')
            $('.btn-submit .pop-confirm').removeAttr('disabled', 'disabled')
        },
        'cropstart.cropper': function(e) {
            // console.log(e.type, e.action);
        },
        'cropmove.cropper': function(e) {
            // console.log(e.type, e.action);
        },
        'cropend.cropper': function(e) {
            // console.log(e.type, e.action);
        },
        'crop.cropper': function(e) {
            // 上传图片大小提示
            originalPicJson = $image.cropper("getImageData");
            originalPicWidth = originalPicJson.naturalWidth;
            originalPicHeight = originalPicJson.naturalHeight;
            cropPicJson = $image.cropper("getData");
            cropPicWidth = Math.round(cropPicJson.width);
            cropPIcHeight = Math.round(cropPicJson.height);
            var limitStr = ""
            // $('.covers-limit').html("")
            // $('.covers-limit2').html("")
            if(originalPicWidth < 800 && originalPicHeight < 600){
                limitStr = messagesWeb.uploadWorksOrArticles.cut_pic_tip
            }else{
                limitStr = messagesWeb.uploadWorksOrArticles.cut_pic_tip2 + cropPicWidth + "x" + cropPIcHeight
            }

            // limitStr += "建议尺寸：800x600或以上"
            $('.covers-limit').html(limitStr)
            // console.log(e.type, e.x, e.y, e.width, e.height, e.rotate, e.scaleX, e.scaleY);
        },
        'zoom.cropper': function(e) {
            // console.log(e.type, e.ratio);
        }
    }).cropper(options);

    // Buttons
    if (!$.isFunction(document.createElement('canvas').getContext)) {
        $('button[data-method="getCroppedCanvas"]').prop('disabled', true);
    }

    if (typeof document.createElement('cropper').style.transition === 'undefined') {
        $('button[data-method="rotate"]').prop('disabled', true);
        $('button[data-method="scale"]').prop('disabled', true);
    }

    // Options
    $('.docs-toggles').on('change', 'input', function() {
        var $this = $(this);
        var name = $this.attr('name');
        var type = $this.prop('type');
        var cropBoxData;
        var canvasData;

        if (!$image.data('cropper')) {
            return;
        }

        if (type === 'checkbox') {
            options[name] = $this.prop('checked');
            cropBoxData = $image.cropper('getCropBoxData');
            canvasData = $image.cropper('getCanvasData');

            options.built = function() {
                $image.cropper('setCropBoxData', cropBoxData);
                $image.cropper('setCanvasData', canvasData);
            };
        } else if (type === 'radio') {
            options[name] = $this.val();
        }

        $image.cropper('destroy').cropper(options);
    });

    $('.btn-zoomout:not(.edit-disabled)').on('click', function() {
        console.log($image);
        $image.cropper('zoom', 0.1);
    })

    $('.btn-zoomin:not(.edit-disabled)').on('click', function() {
        console.log($image);
        $image.cropper('zoom', -0.1);
    })

    // Methods
    $('.docs-buttons').on('click', '[data-method]', function() {
        var $this = $(this);
        var data = $this.data();
        var $target;
        var result;

        if ($this.prop('disabled') || $this.hasClass('disabled')) {
            return;
        }

        if ($image.data('cropper') && data.method) {
            data = $.extend({}, data); // Clone a new one

            if (typeof data.target !== 'undefined') {
                $target = $(data.target);

                if (typeof data.option === 'undefined') {
                    try {
                        data.option = JSON.parse($target.val());
                    } catch (e) {
                        console.log(e.message);
                    }
                }
            }

            if (data.method === 'rotate') {
                $image.cropper('clear');
            }

            result = $image.cropper(data.method, data.option, data.secondOption);

            if (data.method === 'rotate') {
                $image.cropper('crop');
            }

            switch (data.method) {
                case 'scaleX':
                case 'scaleY':
                    $(this).data('option', -data.option);
                    break;

                case 'getCroppedCanvas':
                    if (result) {
                        // 裁切上传图片
                        crop(result);
                    }

                    break;

                case 'destroy':
                    if (uploadedImageURL) {
                        URL.revokeObjectURL(uploadedImageURL);
                        uploadedImageURL = '';
                        $image.attr('src', originalImageURL);
                    }

                    break;
            }

            if ($.isPlainObject(result) && $target) {
                try {
                    $target.val(JSON.stringify(result));
                } catch (e) {
                    console.log(e.message);
                }
            }

        }
    });

    function crop(result) {
        if ($('.cover').length == 0) {
            $('.btn-submit .pop-confirm').attr('disabled', 'disabled').val(messagesWeb.uploadPlugin.upload_confirm_btn).addClass('cursor-default btn-disabled').removeClass('btn-default-main');
            var dataurl = result.toDataURL('image/jpeg');
            var blob = dataURLtoBlob(dataurl);
            // 模拟表单提交的body体
            var fd = new FormData();
            fd.append("file", blob, "image.jpeg");
            //使用ajax发送
            $xhrAjax = $.ajax({
                type: 'POST',
                xhrFields: {
                    withCredentials: true
                },
                crossDomain: true,
                headers: {
                    "X-Requested-With": "XMLHttpRequest"
                },
                url: proUploadZDomain + '/upload/image?uploadtype=productOrArticleCover',
                data: fd,
                // 上传form结构必须设置
                processData: false,
                contentType: false,
                dataType: "json",
                success: function(data) {
                    if (data.code == 0) {
                        //var response = JSON.parse(data);
                        $(".cursor-default").val( messagesWeb.uploadPlugin.upload_confirm_btn2 + 100 +"%" ).removeClass('btn-disabled').addClass('btn-default-main');
                        hideGlobalMaskLayer();
                        $('.editor-portrait').hide();
                        $('#coverPicker').removeClass('status-progress');
                        $('.btn-default-main').find('img').remove();
                        $('.coverchar').find('img').removeClass('hide').attr({
                            'src': result.toDataURL(),
                            '_src': data.data.imagePath
                        });
                        $('.upload-normal-box').html(messagesWeb.uploadWorksOrArticles.modify_cover);
                        // $('.upload-normal-box').css('color', '#fff');
                        $('.upload-normal-box').addClass('upload-yellow-icon');
                        // $('#form-data-cover').css('background', 'rgba(0,0,0,0.5)');
                        $('#form-data-cover').addClass('have-data-cover-style');
                        $('#form-data-cover').attr('data-coverName', data.data.name);
                        $('#form-data-cover').attr('data-coverPath', data.data.path);
                        $('.workup-con').find('.error-prompt').addClass('hide');
                        if($('.biz-draft-btn').length > 0){
                            $('#form-data-cover').attr('c-required','1');
                            saveDraftMonitorInput();
                        }
                    } else {
                        pageToastFail(data.msg)
                    }
                },
                xhr: function(){
                    var xhr = $.ajaxSettings.xhr();
                    if(onprogress && xhr.upload) {
                        xhr.upload.addEventListener("progress" , onprogress, false);
                        return xhr;
                    }
                    function onprogress(evt){
                        if(evt.lengthComputable){
                            var loaded = evt.loaded;     //已经上传大小情况
                            var tot = evt.total;      //附件总大小
                            var per = Math.floor(100*loaded/tot);  //已经上传的百分比
                            if(per == 100){
                                per = 99
                            }
                            $(".cursor-default").val( messagesWeb.uploadPlugin.upload_confirm_btn2 + per +"%" ).addClass('btn-disabled').removeClass('btn-default-main');

                            $('#coverPicker').addClass('status-progress');
                        }
                    }
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){

                    if(textStatus != "abort"){
                        if(XMLHttpRequest.readyState === 0){
                            pageToastFail(messagesWeb.comment_network_rror)
                        }else{
                            pageToastFail(textStatus + XMLHttpRequest.status)
                        }
                        // pageToastFail(messagesWeb.uploadPlugin.upload_pic_too_big)
                    }
                    $('.btn-submit .pop-confirm').removeAttr('disabled').val(messagesWeb.common_btn_confirm).removeClass('cursor-default btn-disabled').addClass('btn-default-main');
                },
                complete:function(XMLHttpRequest,textStatus){
                    // console.log(XMLHttpRequest.status)
                }
            })


            // var xmlhttp;
            // function loadXMLDoc(url) {
            //     xmlhttp = null;
            //     if (window.XMLHttpRequest) {// code for all new browsers
            //         xmlhttp = new XMLHttpRequest();
            //     } else if (window.ActiveXObject) {// code for IE5 and IE6
            //         xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
            //     }

            //     if (xmlhttp != null) {
            //         xmlhttp.onreadystatechange = state_Change;
            //         xmlhttp.open("POST", url, true);
            //         xmlhttp.setRequestHeader("X-Requested-With","XMLHttpRequest");
            //         xmlhttp.send(fd);
            //     }
            // }

            // function state_Change() {
            //     if (xmlhttp.readyState == 4) {
            //         console.log(xmlhttp.status);
            //         console.info(xmlhttp.responseText);
            //         if (xmlhttp.status == 200) {
            //             console.log(xmlhttp.responseText)
            //             var json = eval('(' + xmlhttp.responseText + ')');
            //             console.log(json)
            //         } else {
            //             alert(xmlhttp.status);
            //             alert("Problem retrieving XML data");
            //         }
            //     }
            // }
            // loadXMLDoc('http://' + zUploadDomain + '/upload/image?uploadtype=productOrArticleCover')
        } else {
            // 请上传封面
            pageToastFail(messagesWeb.uploadWorksOrArticles.upload_cover);
        }
    }

    // 图片转化为baxe64进行裁切预览
    function dataURLtoBlob(dataurl) {
        var arr = dataurl.split(','),
            mime = arr[0].match(/:(.*?);/)[1],
            bstr = atob(arr[1]),
            n = bstr.length,
            u8arr = new Uint8Array(n);
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new Blob([u8arr], {
            type: mime
        });
    }

    // Keyboard
    $(document.body).on('keydown', function(e) {

        if (!$image.data('cropper') || this.scrollTop > 300) {
            return;
        }
        if($('.editor-portrait').is(':visible') && $image.attr('src')){
            switch (e.which) {
                case 37:
                    e.preventDefault();
                    $image.cropper('move', -1, 0);
                    break;

                case 38:
                    e.preventDefault();
                    $image.cropper('move', 0, -1);
                    break;

                case 39:
                    e.preventDefault();
                    $image.cropper('move', 1, 0);
                    break;

                case 40:
                    e.preventDefault();
                    $image.cropper('move', 0, 1);
                    break;
            }
        }


    });


    // Import image
    var $inputImage = $('#inputImage');

    if (URL) {
        //input的上传事件
        $inputImage.change(function() {
            var size = 0
            var type = 'image/jpeg'
            if ($inputImage[0].files && $inputImage[0].files[0]) {
                size = $inputImage[0].files[0].size / (1024 * 1024);
                type = $inputImage[0].files[0].type;
            }


            if (type != "image/gif" && type != "image/png" && type != "image/jpeg") {
                // 封面图片格式只支持gif/png/jpeg！
                pageToastFail(messagesWeb.uploadPlugin.image_format);
            } else {
                ajax(size, $inputImage);
            }
        });
    } else {
        $inputImage.prop('disabled', true).parent().addClass('disabled');
    }

    function ajax(size, $inputImage) {
        if (size > 5) {
            // 封面图片超过5M！
            pageToastFail(messagesWeb.uploadWorksOrArticles.cover_limit);
        } else {
            $('.cover').remove();
            $('.cover-preview').remove();
            // $('.rotate-area').show()
            // $('.editor-portrait').hide()
            // $('.edt-btn-reset').removeClass('edit-disabled')
            $('.btn-zoomout, .btn-zoomin, .btn-rotater').removeClass('edit-disabled')
            var files = $inputImage[0].files;
            var file;

            if (!$image.data('cropper')) {
                return;
            }

            if (files && files.length) {
                file = files[0];

                if (/^image\/\w+$/.test(file.type)) {
                    if (uploadedImageURL) {
                        URL.revokeObjectURL(uploadedImageURL);
                    }

                    uploadedImageURL = URL.createObjectURL(file);
                    $image.cropper('destroy').attr('src', uploadedImageURL).cropper(options);
                    $inputImage.val('');
                } else {
                    // 请上传封面
                    pageToastFail(messagesWeb.uploadWorksOrArticles.upload_cover);
                }
            }
        }
    }

    $('.edtior-crop-close,.btn-submit .btn-group-crop .btn-default-secondary ').on('click', function() {
        if($xhrAjax){
            $xhrAjax.abort();
            $('#coverPicker').removeClass('status-progress');
        }
        hideGlobalMaskLayer();
        $('.editor-portrait').hide();

    })

});