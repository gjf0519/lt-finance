//加载列表数据
$(document).ready(function () {
    initTable();
});

function initTable() {
    $('#day-line-table').bootstrapTable({
        url: '/day-line/line-list',
        method: 'post',
        dataType : 'json',
        uniqueId: 'id',
        idField: 'id',
        toolbar: '#toolbar',
        showColumns: true,
        showRefresh: true,
        showToggle: true,
        pagination: true,
        queryParams: function(params) {
            var temp = collectForm();
            temp["limit"] = params.limit;
            temp["offset"] = params.offset;
            return temp;
        },
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        columns: [{
            field: 'id',
            checkbox: true
        }, {
            field: 'tsCode',
            title: '股票代码'
        }, {
            field: 'tradeDate',
            title: '交易日期'
        }, {
            field: 'pctChg',
            title: '股票振幅',
        }, {
            field: 'operate',
            title: '操作',
            formatter: btnGroup,
            events: {
                'click .edit-btn': function (event, value, row, index) {
                    editUser(row);
                },
                'click .del-btn': function (event, value, row, index) {
                    delUser(row);
                }
            }
        }],
        ajaxOptions:{
            headers: {"Access_Token":sessionStorage.getItem("token")}
        },
        responseHandler: function (res) {
            return {
                "rows": res.data.rows,
                "total": res.data.total
            };
        }
    });
}

//检索数据
function queryForm() {
    $("#day-line-table").bootstrapTable('refresh');
}

//收集查询参数
function collectForm() {
    var data = {};
    var t = $('#dayLineForm').serializeArray();
    $.each(t, function() {
        if(!empty(this.value)){
            data[this.name] = this.value;
        };
    });
    return data;
}

// 操作按钮
function btnGroup ()
{
    var operation =
        '<a href="#!" class="btn btn-xs btn-default m-r-5 edit-btn" title="编辑" data-toggle="tooltip"><i class="mdi mdi-pencil"></i></a>' +
        '<a href="#!" class="btn btn-xs btn-default del-btn" title="删除" data-toggle="tooltip"><i class="mdi mdi-window-close"></i></a>';
    return operation;
}
// 操作方法 - 编辑
function editUser()
{
    alert('跳转修改信息');
}
// 操作方法 - 删除
function delUser()
{
    alert('信息删除成功');
}