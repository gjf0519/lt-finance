//加载列表数据
$(document).ready(function () {
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
            var temp = {
                limit: params.limit,
                offset: params.offset,
                page: (params.offset / params.limit) + 1,
                sort: params.sort,
                sortOrder: params.order
            };
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
            headers: {"access_token":sessionStorage.getItem("token")}
        }
    });
});

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