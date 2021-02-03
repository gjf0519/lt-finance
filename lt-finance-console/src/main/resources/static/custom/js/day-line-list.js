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
            checkbox: true,
            formatter : function (value, row) {
                if (row.id == 0)
                    return {
                        disabled : true,//设置是否可用
                        checked : true//设置选中
                    };
                return value;
            }
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
                    editEvent(row);
                },
                'click .del-btn': function (event, value, row, index) {
                    delEvent(row);
                },
                'click .modal-btn': function (event, value, row, index) {
                    modalEvent(row);
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
function btnGroup() {
    var operation =
        '<a href="#!" class="btn btn-xs btn-default m-r-5 edit-btn" title="编辑"><i class="mdi mdi-pencil"></i></a>' +
        '<a href="#!" class="btn btn-xs btn-default m-r-5 del-btn" title="删除"><i class="mdi mdi-window-close"></i></a>' +
        '<a href="#!" class="btn btn-xs btn-default modal-btn" title="K图"><i class="mdi mdi-chart-line"></i></a>';
    return operation;
}

// 操作方法 - 编辑
function editEvent() {
    alert('跳转修改信息');
}

// 操作方法 - 删除
function delEvent() {
    alert('信息删除成功');
}

//模态窗口打开时间
function modalEvent(row){
    var idlist = $('#day-line-table').bootstrapTable('getAllSelections');
    for (var i = 0; i < idlist.length; i++) {
        alert(idlist[i].ID);
    }
    // alert(JSON.stringify(row));
    // $('#myLargeModal').on('show.bs.modal', function (event) {
    //     klineData();
    // })
}

function klineData() {
    var $dashChartLinesCnt = jQuery( '.js-chartjs-lines' )[0].getContext( '2d' );

    var $dashChartLinesData = {
        labels: ['2003', '2004', '2005', '2006', '2007', '2008', '2009', '2010', '2011', '2012', '2013', '2014'],
        datasets: [
            {
                label: '交易资金',
                data: [20, 25, 40, 30, 45, 40, 55, 40, 48, 40, 42, 50],
                borderColor: '#358ed7',
                backgroundColor: 'rgba(53, 142, 215, 0.175)',
                borderWidth: 1,
                fill: false,
                lineTension: 0.5
            }
        ]
    };

    var myLineChart = new Chart($dashChartLinesCnt, {
        type: 'line',
        data: $dashChartLinesData,
    });
}