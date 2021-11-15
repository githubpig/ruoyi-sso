(function ($) {
    $.extend({
        album:{
            initAlbum:function(containerId,code){

                $.ajax({
                        url: ctx+"cms/album/getAlbum",
                        data: {"code":code},
                        type: 'POST',
                        success: function(json){
                            if(json.code!=0){
                                console.log("initAlbum失败!"+json.msg);
                                $.modal.alertError(result.msg);
                            }
                            var swiperContainer="";
                            swiperContainer+='<div class="swiper-container">';
                            swiperContainer+=' <div class="swiper-wrapper"></div>';
                            swiperContainer+='  <div class="swiper-pagination"></div>';
                            swiperContainer+=' <div class="swiper-button-prev"></div>';
                            swiperContainer+=' <div class="swiper-button-next"></div></div>';
                            $("#"+containerId).append(swiperContainer);
                            var width=json.data.width;
                            var height=json.data.height;
                            var html="";
                            var imgs=json.data.images;

                            for(var i=0;i<imgs.length;i++){
                                if(ctx=='/'){
                                    html+="<div class=\"swiper-slide\"><a  href="+json.data.images[i].link+"><img src=\""+imgs[i].savePath+"\"/></a></div>";
                                }else{
                                    html+="<div class=\"swiper-slide\"><a  href=\"+json.data.images[i].link+\"><img  src=\""+ctx+imgs[i].savePath+"\"/></a></div>";
                                }

                            }
                            $(".swiper-container").css("width",width+"px").css("height",height+"px");
                            $(".swiper-wrapper").html(html);
                            var mySwiper = new Swiper ('.swiper-container', {
                                loop: true,
                                pagination: {
                                    el: '.swiper-pagination',
                                },
                                autoplay:true,
                                navigation: {
                                    nextEl: '.swiper-button-next',
                                    prevEl: '.swiper-button-prev',
                                }
                            })
                        }
                    }
                );
            }
        }
    });
})(jQuery);
