;jQuery( function() {
    //加载列表数据
    $(document).ready(function () {
        $.ajax({
            type: "POST",
            url: "/day-line/line-list",
            dataType: "json",
            beforeSend: function (request) {
                request.setRequestHeader("access_token", sessionStorage.getItem("token"));
            },
            success: function (result) {
                if(result.code == 200){
                    console.log(result.data);
                    $("#day-line-table").bootstrapTable('load', result.data);
                }else{
                    console.log(result.msg);
                }
            },
            error: function () {
                console.log("接口异常。。。。。。");
            }
        });
    });
});