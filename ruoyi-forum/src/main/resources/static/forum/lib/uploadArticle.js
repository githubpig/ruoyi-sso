// 'use strict';

var newWin;

// 上传文章，title summary sourceurl的输入限制提示
function characterRestriction() {
    var articleName = parseInt($('#form-data-title').siblings('.count').html());
    if (articleName < 0) {
        // 文章标题最多50个字符;
        pageToastSuccess("文章标题最多50个字符");
        return false;
    }
    var articleDescription = parseInt($('#form-data-summary').siblings('.count').html());
    if (articleDescription < 0) {
        pageToastSuccess("文章简介最多120个字符");
        return false;
    }
    var sourceurl = parseInt($('#form-data-sourceurl').siblings('.count').html());
    if (sourceurl < 0) {
        pageToastSuccess("文章链接最多100个字符");
        return false;
    }
}

// 判断input是否为空；
// function empy(obj, parentObj) {
//     var workName = $.trim(obj.val());
//     if (workName == "") {
//         obj.parents(parentObj).find('.error-prompt').removeClass('hide');

//     } else {
//         obj.parents(parentObj).find('.error-prompt').addClass('hide');
//     }
// }

// 下拉的显示，隐藏；
// function blockToggle(a, b) {
//     if (a.css('display') == 'block') {
//         b.removeClass('select-arrowicon-active')
//         a.hide();
//     } else {

//         b.addClass('select-arrowicon-active')
//         a.show();
//     }
// }


// 获取文章所属领域；
function getArticleCates() {
    var cate = $('#form-data-articlecate-container').attr('data-articlecate').split(",");
    var articleCates = [];
    for (var i = 0; i < cate.length; i++) {
        var obj = {
            'id': parseInt(cate[i])
        };
        articleCates.push(obj);
    }

    return articleCates;
}

//  获取标签
function addLabel() {
    var labels = [];
    $('#form-data-productTags span').each(function (i, j) {
        var obj = {
            "id": $(j).attr('data-id'),
            "name": $(j).attr('title')
        };
        labels.push(obj);
    });
    return labels;
}

/**
 * 发布提交，预览提交，获取页面信息及验证；
 * @param {*} url 异步请求的地址
 * @param {*} refTo 请求成功后跳转的链接地址
 * @param {*} ele 请求点击的元素
 */
function submit(url, refTo, ele) {
    storedDraftValues.flag = 0;
    var body = {},
        required = [],
        btn = $(this);
    required[0] = body.title = $('#form-data-title').val() == '' ? "" : /[^\s]+/.test($('.work-name').val()) ? $('.work-name').val() : "";
    required[1] = body.cate = $('#form-data-type-container').attr('data-cate') ? parseInt($('#form-data-type-container').attr('data-cate')) : 0;
    // required[6] = body.field =
    required[2] = body.articleCates = $.trim($('#form-data-articlecate-container').attr('data-articlecate')) != "" ? getArticleCates() : [];
    required[3] = body.summary = $('#form-data-summary').val();
    required[4] = body.memo = ue.getContent();
    required[5] = body.coverName = $('#form-data-cover').attr('data-covername') && $('#form-data-cover').attr('data-coverName') !== '0' ? $('.upcoverbtn').attr('data-covername') : null;
    required[6] = body.sourceUrl = $('#form-data-sourceurl').val();
    body.coverPath = $('#form-data-cover').attr('data-coverpath') ? $('.upcoverbtn').attr('data-coverpath') : null;
    body.type = $('#form-data-type-container').attr('data-type') ? parseInt($('#form-data-type-container').attr('data-type')) : 1;
    body.id = parseInt($('#form-data-id').attr('value'));
    // body.sourceUrl = $('#form-data-sourceurl').val();
    body.fileId = $('#form-data-fileId').attr('fileid') ? parseInt($('#form-data-fileId').attr('fileid')) : null;
    body.articleTags = addLabel();
    body.mycate = parseInt($('#form-data-mycate').attr('data-id'));
    body.zteamId = $('#form-data-zteamId').attr('data-id') ? parseInt($('#form-data-zteamId').attr('data-id')) : 0;

    if (ele[0] == $('.biz-draft-btn')[0]) {
        // ****************
        // 存草稿 title为必填项
        // if (!required[0] || required[0] == '') {
        // 	$('.work-title-box .work-nametips').removeClass('hide');
        // 	$(document).scrollTop($('.work-title-box .work-nametips').offset().top - 20);
        // 	return;
        // }
        // *********************
        ele.val("保存中...");
        ajax(url, refTo, body, ele);
        $('.biz-draft-btn').attr('disabled', true);
    } else {
        $('.text-link .work-nametips').addClass('hide');
        if (!required[0] || required[0] == '') {
            $('.work-title-box .work-nametips').removeClass('hide');
            $(document).scrollTop($('.work-title-box .work-nametips').offset().top - 20);
            return;
        } else if (!required[1]) {
            $('.work-selectbox .error-prompt').removeClass('hide');
            $(document).scrollTop($('.work-selectbox .error-prompt').offset().top - 20);
            return;
        } else if (!required[2].length) {
            $('.work-selectbox .error-prompt').removeClass('hide');
            $(document).scrollTop($('.work-selectbox .error-prompt').offset().top - 20);
            return;
        } else if ($('#form-data-type-container').attr('data-type') == 2 && (!required[6] || required[6] == '')) {
            $('.text-link .work-nametips').removeClass('hide');
            $(document).scrollTop($('.text-link .error-prompt').offset().top - 20);
            return;
        } else if (!required[3] || required[3] == '') {
            $('.work-discriptionbox .error-prompt').removeClass('hide');
            $(document).scrollTop($('.work-discriptionbox .error-prompt').offset().top - 20);
            return;
        } else if (!required[4] || required[4].replace(/[^\x20-\x7E]+/g, '').length <= 0) {
            $('.article-edit-wrap').find('.articleWarn').removeClass('hide');
            $(document).scrollTop($('.article-edit-wrap').offset().top - 20);
            return;
        } else if (!required[5]) {
            $('.workup-con').find('.error-prompt').removeClass('hide');
            $(document).scrollTop($('.workup-con').find('.error-prompt').offset().top - 20);
            return;
        } else {
            if (ele[0] == $('.publishbtn')[0]) {
                $('.publishbtn').addClass('btn-default-loading').attr('disabled', true).val("发布中...");
            } else {
                newWin = window.open('');
            }
            ajax(url, refTo, body, ele);
        }
    }
}

function ajax(url, refTo, body, ele) {
    $.ajax({
        type: "POST",
        url: url,
        data: JSON.stringify(body),
        dataType: "json",
        contentType: 'application/json',
        success: function success(data) {
            if (ele) {
                if (ele[0] == $('.biz-draft-btn')[0]) {
                    // location.href = refTo;
                    // 已保存草稿;
                    if (data.code == 0) {
                        ele.val("已保存草稿");
                        if (body.id == 0) {
                            $('#form-data-id').val(data.data.id);
                        }
                    } else {
                        ele.val(data.msg);
                    }
                    storedDraftValues.flag = 0;
                } else {
                    if (data.code == 0) {
                        if (typeof data.data == 'string') {
                            newWin.location.href = refTo + data.data;
                        } else {
                            location.href = refTo;
                        }
                    } else {
                        pageToastFail(data.msg);
                        ele.val('发布').removeAttr('disabled').removeClass('btn-default-loading');
                    }
                }
            } else {
                if (typeof data.data == 'string') {
                    newWin.location.href = refTo + data.data;
                } else {
                    location.href = refTo;
                }
            }
        },
        error: function error(XMLHttpRequest, textStatus, errorThrown) {
            // 服务异常，请稍后重试;
            //  pageToastFail(messagesWeb.comment_exception_hints);
            pageToastFail("服务器异常" + XMLHttpRequest.status);
            // 草稿保存失败提示
            $('.biz-draft-btn').val("保存失败");
            if (ele) {
                if (ele[0] == $('.publishbtn')[0]) {
                    $(ele).removeAttr('disabled').removeClass('btn-default-loading').val('发布');
                    $('.publishbtn').bind('click', btnsubmit);
                } else {
                    $(ele).removeAttr('disabled').removeClass('btn-default-loading').val('预览');
                }
            }
            //var url = serverZLog + "/error.gif?type=upload_article_servererror";
            //new Image().src = url;
        }

    });
}

function uploadProgressStatusTips() {
    if ($('#coverPicker').is('.status-progress') || $('.upattastatus .progress:visible').length) {
        return false;
    }
}
function btnsubmit() {
    showRemindBindLayer(publishUnbindTis);
    function publishUnbindTis() {
        if (uploadProgressStatusTips() == false) {
            pageToastFail("上传中，请稍后发布");
        }
        if (characterRestriction() != false && uploadProgressStatusTips() != false && storedDraftValues.flag == 0) {
            submit(proMyZDomain + '/uploadArticle', proMyZDomain + '/articles', $('.publishbtn'));
        }
    }
}

// 初始必填项为元素标记属性；
function initRequireVerify() {
    // 文章
    !parseInt($('#form-data-type-container').attr('data-cate')) && $('#form-data-type-container').attr({ 'data-type': '0' });
    parseInt($('#form-data-type-container').attr('data-cate')) ? $(".selectmenu > li .selected[data-validate='need']").attr('c-required', 1) : $(".selectmenu > li .selected[data-validate='need']").removeAttr('c-required');
    $.trim($('#form-data-articlecate-container').attr('data-articlecate')) != "" ? $(".selectmenu > li .selected[data-validate='needx']").attr('c-required', 1) : $(".selectmenu > li .selected[data-validate='needx']").removeAttr('c-required');
    $('#form-data-summary').val() == '' ? $('#form-data-summary').removeAttr('c-required') : $('#form-data-summary').attr('c-required', 1);
    ue.ready(function () {
        ue.getContent() == "" ? $('.article-edit-wrap').removeAttr('c-required') : $('.article-edit-wrap').attr('c-required', 1);
    });
    $('#form-data-cover').attr('data-coverName') ? $('#form-data-cover').attr('c-required', 1) : $('#form-data-cover').removeAttr('c-required');
}

$(function () {
    initRequireVerify();

    $('.text-style').on('blur', function () {
        $(this).removeClass('borderred').next().removeClass('warning').removeClass('exceeded');
    });
    $('#form-data-title').on('blur', function () {
        empy($(this), '.work-title-box');
    });
    $('#form-data-summary').on('blur', function () {
        empy($(this), '.aricle-box');
    });
    $('#form-data-sourceurl').on('blur', function () {
        if ($('#form-data-type-container').attr('data-type') == 2 && $.trim($(this).val()) == "") {
            $(this).parents('.text-link').find('.error-prompt').removeClass('hide');
        } else {
            $(this).parents('.text-link').find('.error-prompt').addClass('hide');
        }
    });
    $('textarea[name=form-data-memo]').on('blur', function () {
        if ($('textarea[name=form-data-memo]').val() == '') {
            $('.articleWarn').removeClass('hide');
        } else {
            $('.articleWarn').addClass('hide');
        }
    });
    if ($('#form-data-type-container .radio-0 input').val() == 2) {
        $('.link-error-box .redwarn').removeClass('hide');
    } else {
        $('.link-error-box .redwarn').addClass('hide');
    }

    function inputCheck(inputParent, inputName) {
        $(inputParent + ' label').on('click', function () {
            $(this).siblings('label').removeClass('radio-0').addClass('radio-1');
            $(this).addClass('radio-0').removeClass('radio-1');
            $('.selected .type').html($(this).text());
            var type = $('#form-data-type-container .radio-0 input').val();
            if (type == 2) {
                $('.link-error-box .redwarn').removeClass('hide');
            } else {
                $('.link-error-box .redwarn').addClass('hide');
            }

            var id = $('#form-data-cate-container li[class="active"]').attr('data-id');
            var getTypeText = $('#form-data-type-container .radio-0').text();
            var oneCon = $('#form-data-cate-container li[class="active"]').html();
            $('#form-data-type-container').attr({
                'data-type': type,
                'data-cate': id,
                'data-info': getTypeText + '-' + oneCon
            });
            $(".selectmenu > li .selected[data-validate='need']").attr({
                'data-cate': id,
                'data-type': type
            });
            saveDraftMonitorInput();
        });
    }

    inputCheck('.radio', 'name="radio"');
    inputCheck('.radio', 'name="forbidden-option"');
    inputCheck('.radio', 'name="original-copy"');

    // 取消冒泡
    $('.select-con').on('click', function (e) {
        cancelbuble(e);
    });

    // 所属领域
    function Trade(limit) {
        if ($('#up-field-data').find('input:checked').parents('label').length >= limit) {

            return 2;
        } else if ($('#up-field-data').find('input:checked').parents('label').length == 0) {

            return 0;
        } else {
            return 1;
        }
    }

    var validateTrade = function validateTrade(limit, errorEle) {
        if (errorEle) {
            if (Trade(limit) == 2) {
                $('#up-field-data').find('input:checkbox').not("input:checked").attr('disabled', true).parents('label').addClass('disabled');
                errorEle.hide();
            } else if (Trade(limit) == 0) {
                errorEle.show();
            } else {
                $('#up-field-data').find('input:checkbox').not("input:checked").removeAttr('disabled').parents('label').removeClass('disabled');
                errorEle.hide();
            }
        } else {
            if (Trade(limit) == 2) {
                $('#up-field-data').find('input:checkbox').not("input:checked").attr('disabled', true).parents('label').addClass('disabled');
            } else {
                $('#up-field-data').find('input:checkbox').not("input:checked").removeAttr('disabled').parents('label').removeClass('disabled');
            }
        }
    };
    validateTrade(3);
    $('#up-field-data label').on('click', function () {
        validateTrade(3);
        if ($('#up-field-data').find('input:checked').parents('label').length == 0) {
            $('#form-data-articlecate-container').attr('data-articlecate', '');
            $(".selectmenu > li .selected[data-validate='needx']").attr('data-articlecate', '');
        }
    });
    // 作品归类
    //  $('.moreset-text').on('click', function() {
    //      blockToggle($('#more-set-con'), $(this).find('i'))
    //  })
    $('label.label-checkbox').on('click', function () {
        $("input[type='checkbox']").parent().removeClass('check-cd').addClass('check-c');
        $("input[type='checkbox']:checked").parent().removeClass('check-c').addClass('check-cd');
        var type = $('#form-data-type-container .radio-0 input').val();
        var id = $('#form-data-cate-container li[class="active"]').attr('data-id');
        var getTypeText = $('#form-data-type-container .radio-0').text();
        var oneCon = $('#form-data-cate-container li[class="active"]').html();
        $('#form-data-type-container').attr({
            'data-type': type,
            'data-cate': id,
            'data-info': getTypeText + '-' + oneCon
        });
        $(".selectmenu > li .selected[data-validate='need']").attr('data-cate', id);
    });
    $(".selectmenu > li").on('click', function (e) {

        var _this = $(this);
        var seleCon = $(this).find('.selected');
        var selectBox = $(this).find('.select-con');
        var thisOneList = selectBox.find('.select-option li');
        var arrow = $(this).find('.select-arrowicon');

        var siblingsArrow = $(this).siblings('li').find('.select-arrowicon');
        var selectContent = $('.bigconbox');
        var validateSelect = $(".selectmenu > li .selected[data-validate='need']");
        var validateSelectX = $(".selectmenu > li .selected[data-validate='needx']");
        var selectContrim = selectBox.find('.select-confrim');
        var checkOption = $('.select-option-checkbox > li');
        var checkCon = ' ';
        var textLink = $('.text-link');
        // needx

        blockToggle(selectBox, arrow);

        siblingsArrow.removeClass('select-arrowicon-active');

        $(this).siblings('li').find('.select-con').hide().find('.select-two').hide();
        thisOneList.on('click', function (e) {
            // ////// 第二个 三个 文章3     文章1
            var oneCon = $(this).html();
            var id = $(this).attr('data-id');
            _this.addClass('active');
            $(this).addClass('active').siblings().removeClass('active');
            arrow.removeClass('select-arrowicon-active');

            if ($(this).parents('.select-option-label').hasClass('select-option-label')) {
                console.log($('input[name="original-copy"]:checked').parent().text());
                var getTypeText = $('#form-data-type-container .radio-0').text();

                var selectedCon = "<span class='type'>" + getTypeText + "</span>" + "/" + oneCon;

                var type = $('#form-data-type-container .radio-0 input').val();
                seleCon.html(selectedCon).attr('c-required', '1');
                $('#form-data-type-container').attr({
                    'data-type': type,
                    'data-cate': id,
                    'data-info': getTypeText + '-' + oneCon
                });
                $(".selectmenu > li .selected[data-validate='need']").attr({
                    'data-cate': id,
                    'data-type': type
                });
            } else {
                seleCon.html($(this).text()).attr({
                    'data-id': id,
                    'c-required': '1'
                });
            }

            selectBox.hide();
            saveDraftMonitorInput();
        });
        checkOption.on('click', function () {
            checkCon = " ";
        });

        function selectFn() {
            // ;;;;;  文章2

            var selectCheckBox = checkOption.find('label');

            if (selectCheckBox.hasClass('check-cd')) {
                var mycate = [];
                $('.select-option-checkbox').parents('li').find('.selected').html("");
                for (var i = 0; i < checkOption.length; i++) {
                    if (checkOption.eq(i).find('label').hasClass('check-cd')) {
                        if (checkCon == " ") {
                            checkCon = checkCon + checkOption.eq(i).find('label').text();
                        } else {
                            checkCon = checkCon + "/" + checkOption.eq(i).find('label').text();
                        }
                        mycate.push(checkOption.eq(i).find('label').attr('data-id'));
                    }
                }
                $('.select-option-checkbox').parents('li').find('.selected').html(checkCon);
                if (checkOption.length > 1) {
                    $('#form-data-type-container').attr('data-msg', '多领域');
                } else {
                    $('#form-data-type-container').attr('data-msg', checkCon);
                }

                $('#form-data-articlecate-container').attr('data-articlecate', mycate);
                $(".selectmenu > li .selected[data-validate='needx']").attr({
                    'data-articlecate': mycate,
                    'c-required': '1'
                });
                arrow.removeClass('select-arrowicon-active');

                saveDraftMonitorInput();
                selectBox.hide();
            } else {
                $(".selectmenu > li .selected[data-validate='needx']").removeAttr('c-required');
            }
        }

        selectContrim.on('click', function () {
            selectFn();
        });
        $(document).on('click', function () {
            if ($.trim(validateSelect.attr('data-cate')) == 0) {

                $('.selectmenu').next('.error-prompt').removeClass('hide').html('<i class="error-icon">!</i>' + "请选择文章类型");
            } else if ($.trim(validateSelectX.attr('data-articlecate')) == "") {
                // 请选择所属领域;
                $('.selectmenu').next('.error-prompt').removeClass('hide').html('<i class="error-icon">!</i>' + "请选择所属领域");
                $('#form-data-articlecate-container').attr('data-articlecate', '');
                $(".selectmenu > li .selected[data-validate='needx']").attr('data-articlecate', '');
            } else {

                $('.selectmenu').next('.error-prompt').addClass('hide');
            }
            var selectCheckBox = checkOption.find('label');
            if (!selectCheckBox.hasClass('check-cd')) {
                // 请选择所属领域;
                selectContrim.parents('li').find('.selected').html("请选择所属领域");
                $('.selectmenu').next('.error-prompt').removeClass('hide').html('<i class="error-icon">!</i>' +"请选择所属领域");
                $('#form-data-articlecate-container').attr('data-articlecate', '');
                $(".selectmenu > li .selected[data-validate='needx']").attr('data-articlecate', '');
            } else {
                checkCon = " ";
                selectFn();
            }

            selectBox.hide();
            $(".selectmenu > li").find('.select-arrowicon').removeClass('select-arrowicon-active');
        });
        if ($(this).parent().hasClass('selectmenu')) {
            cancelbuble(e);
        }
    });

    var recordingSwiperProps = {
        slides: [],
        clickIndex: 0
    };

    $('.upcoverbtn').on('click', function () {
        $('.btn-submit .pop-confirm').removeAttr('disabled').val("确定").removeClass('cursor-default');
        $('.editor-portrait').removeClass('hide');
        showGlobalMaskLayer();
        $('.editor-portrait').show();
        var hidden = $('#coverCard-data-hidden');
        var dataTitle = $("#form-data-title").val();
        // 标题
        $(".img-preview-wrap .data-title").html(dataTitle).attr("title", dataTitle);
        // 类别
        var typeCate = $('#form-data-type-container').attr('data-type') === '0' ? $('#form-data-type-cate').text() : $('#form-data-type-container').attr('data-info') + '-' + $('#form-data-type-container').attr('data-msg');
        $(".img-preview-wrap .data-type-cate-subcate").html(typeCate);
        // 数量
        $(".img-preview-wrap .data-viewCount").html(hidden.attr('data-viewcount')).attr("title", hidden.attr('data-viewcountstr'));
        $(".img-preview-wrap .data-commentCount").html(hidden.attr('data-commentcount')).attr("title", hidden.attr('data-commentcountstr'));
        $(".img-preview-wrap .data-recommendCount").html(hidden.attr('data-recommendcount')).attr("title", hidden.attr('data-recommendcountstr'));
        // 作者
        $(".img-preview-wrap .data-member").contents().filter(function () {
            return this.nodeType == 3;
        }).remove();
        $(".img-preview-wrap .data-member").append(hidden.attr('data-member-username')).attr("title", hidden.attr('data-member-username')).attr("href", hidden.attr('data-member-pageurl'));
        $(".img-preview-wrap .data-member-avatar").attr("src", hidden.attr('data-member-avatar'));
        // 发布时间
        $(".img-preview-wrap .data-publishTime").html(hidden.attr('data-publishtimediffstr')).attr("title", hidden.attr('data-publishtime'));

        var imgLi = [];
        ue.getContent().replace(/<img.*?src="([^"].*?)".*?\/?>/g, function (match, param) {
            if (!_.includes(param, 'ueditorzwt.')) imgLi = _.concat(imgLi, param);
        });

        if (recordingSwiperProps.slides.length === 0 || !_.isEqual(recordingSwiperProps.slides, imgLi)) {
            recordingSwiperProps.slides = imgLi;

            var coverImgList = "";
            _(imgLi).forEach(function (value) {
                if (value.indexOf(zUploadDomain) !== -1 || value.indexOf('img.zcool.cn') !== -1) {
                    value = value.replace(/(http|https).*(upload-test.zcool.cn|upload.zcool.com.cn|img.zcool.cn)/i, '');
                    coverImgList += '<div class="swiper-slide swiper-slide-active"><img src="' + value + '@240w_180h_1c_1e_1l_2o.jpg" data-_src="' + value + '" /></div>';
                }
            });
            $('.swiper-wrapper').html(coverImgList);

            var mySwiper = new Swiper('#zCoverSlider', {
                slidesPerView: 3.76,
                spaceBetween: 10,
                prevButton: '.swiper-button-prev',
                nextButton: '.swiper-button-next',
                observer: true,
                onInit: function onInit(swiper) {
                    if ($('#zCoverSlider .swiper-slide').length > 4) {
                        _($('#zCoverSlider .swiper-slide')).forEach(function (item, index) {
                            if ($(item).find('img').attr('data-_src') === $('#image').attr('src')) {
                                swiper.slideTo(index, 1000, false);
                                swiper.slides.removeClass('swiper-slide-active');
                                swiper.slides.eq(index).addClass('swiper-slide-active');
                            }
                        });
                    } else {
                        swiper.setWrapperTranslate(0);
                    }
                },
                onTransitionStart: function onTransitionStart() {
                    mySwiper.slides.removeClass('swiper-slide-active');
                    mySwiper.slides.eq(recordingSwiperProps.clickIndex).addClass('swiper-slide-active');
                },
                onClick: function onClick(swiper) {
                    var clickedSlideActive = mySwiper.slides.eq(swiper.clickedIndex);
                    var activeImgPath = clickedSlideActive.find('img').attr('data-_src');

                    if (swiper.clickedIndex !== undefined) {
                        mySwiper.slides.removeClass('swiper-slide-active');
                        clickedSlideActive.addClass('swiper-slide-active');
                        recordingSwiperProps.clickIndex = swiper.clickedIndex;
                        if ($('#image').attr('src') !== activeImgPath) {
                            cropperDestroyImage(activeImgPath);
                        }
                    }
                }
            });

            if ($('#form-data-cover').attr('data-covername') === "" || $('#form-data-cover').attr('data-covername') === "0") {
                $('#form-data-cover').attr('data-covername', 0);
                $('.btn-zoomout, .btn-zoomin, .btn-rotater').addClass('edit-disabled');
                cropperDestroyImage($('.swiper-slide.swiper-slide-active img').attr('data-_src'));
            }
        }

        function cropperDestroyImage(activeImgPath) {
            if (activeImgPath) {
                $('.z-swiper-wrapper').addClass('edit-disabled');
                $('.btn-submit .pop-confirm').attr('disabled', 'disabled');
                $('#image').cropper('destroy').attr('src', activeImgPath).cropper({
                    viewMode: 1,
                    cropBoxResizable: true,
                    autoCropArea: 1,
                    aspectRatio: 104 / 78,
                    preview: '.img-preview',
                    dragMode: 'move',
                    toggleDragModeOnDblclick: false
                });
            }
        }
    });
});

$('.publishbtn').on('click', function (e) {
    //  e.preventDefault();
    if (getUid() > 0) {
        btnsubmit();
    } else {
        pageToastFail("请登录");
        //var url = serverZLog + "/error.gif?type=upload_article_servererror";
        //new Image().src = url;
    }
});
$('.previewbtn').on('click', function (e) {
    if (getUid() > 0) {
        if (isExistPhone) {
            // 已绑定手机号
            // previewNewWin = window.open('');
            if (uploadProgressStatusTips() == false) {
                pageToastFail("上传中，请稍后预览");
            }
            if (characterRestriction() != false && uploadProgressStatusTips() != false) {
                submit(proMyZDomain + '/previewArticle', proMyZDomain + '/preview/article/', $('.previewbtn'));
            }
        } else {
            // 未绑定调用弹窗
            publishedbindLayerShow();
        }
        e.preventDefault();
    } else {
        pageToastFail("请登录");
        //var url = serverZLog + "/error.gif?type=article_preview_servererror";
       //new Image().src = url;
    }
});

// 存草稿功能
// storageStr 存储当前input输入的值
// flag 存草稿时的一个开关，确保异步请求成功后再进行下次请求；
var storedDraftValues = {
    storageStr: "",
    articleTitle: "",
    articleSourceurl: "",
    articleSummary: "",
    articleMemo: "",
    articleCoverName: "",
    flag: 0
};
if ($('#form-data-cover').attr('data-covername') != "") {
    $('#form-data-cover').attr('c-required', '1');
} else {
    $('#form-data-cover').removeAttr('c-required');
}

// /**
//  * 存草稿调用，
//  * @param {*} temporaryComparisonV input输入时当前的值
//  * @param {*} curObj 当前input元素
//  */
// var timmer = null;
// function saveDraftMonitorInput(temporaryComparisonV, curObj) {
//     clearTimeout(timmer)
//     timmer = setTimeout(function() {
//     //  存草稿时判断当前是否有改动，和必填项是否是超过2项；
//     if (curObj && temporaryComparisonV == curObj.val() && $('[c-required=1]').length >= 2) {
//         // 在uploadArticle.js中定义；
//         submit(proMyZDomain + '/draftArticle', proMyZDomain + '/articles', $('.biz-draft-btn'));
//     }else{
//         if($('[c-required=1]').length >= 2){
//             submit(proMyZDomain + '/draftArticle', proMyZDomain + '/articles', $('.biz-draft-btn'));
//         }
//     }
//     }, 2000)
// }

// saveDraftMonitorInput();


$('#form-data-title,#form-data-sourceurl,#form-data-summary').on('keyup', function () {
    saveDraftMonitorInput(storedDraftValues.storageStr, $(this));
});

$('#form-data-title').on('input', function () {
    storedDraftValues.storageStr = $(this).val();
    if ($(this).val().trim() != "") {
        $(this).attr('c-required', '1');
    } else {
        $(this).removeAttr('c-required');
    }
});
$('#form-data-summary').on('input', function () {
    storedDraftValues.storageStr = $(this).val();
    if ($(this).val().trim() != "") {
        $(this).attr('c-required', '1');
    } else {
        $(this).removeAttr('c-required');
    }
});
$('#form-data-sourceurl').on('input', function () {
    storedDraftValues.storageStr = $(this).val();
});

// ue编辑器插件，内容监听；
ue.addListener('beforeSelectionChange', function (editor) {
    var ueContent = ue.getContent();
    if (ueContent != "") {
        $('.article-edit-wrap').attr('c-required', '1');
    } else {
        $('.article-edit-wrap').removeAttr('c-required');
    }
    saveDraftMonitorInput();
});

//  input计数插件调用，在main.js 中封装；
zCharCount_withExceedCount($("#form-data-title"), {
    allowed: 80
});
zCharCount_withExceedCount($("#workmark"), {
    allowed: 16
});

$("#note-text").charCount({
    allowed: 100,
    warning: 0
});
zCharCount_withExceedCount($("#form-data-summary"), {
    allowed: 120
});

zCharCount_withExceedCount($("#form-data-sourceurl"), {
    allowed: 100
});